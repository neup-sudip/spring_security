package com.example.security.security;


import com.example.security.models.ActivityProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private final ApplicationContext context;

    private static String CHARSET = "UTF-8";
    private static int STR_MAX_LEN = 12000;


    /**
     * Do filter internal.
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long beginTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        String requestParam = convertString(requestWrapper.getContentAsByteArray());
        ActivityProperty prop = getActivityProperty(request);

        updateResponse(prop.getUri(), responseWrapper);

        long timeCost = System.currentTimeMillis() - beginTime;

        trace(responseWrapper, request, requestParam, prop, timeCost);

    }

    /**
     * Trace.
     *
     * @param responseWrapper the response wrapper
     * @param request         the request
     * @param requestParam    the request param
     * @param prop            the prop
     * @param timeCost        the timeCost
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private void trace(ContentCachingResponseWrapper responseWrapper, HttpServletRequest request, String requestParam,
                       ActivityProperty prop, long timeCost) throws UnsupportedEncodingException {

        String username = request.getUserPrincipal() == null ? "NO AUTH" : request.getUserPrincipal().getName();
        final String prettyRequestParam = beautifyJson(requestParam);

        if (isNotJsonContentType(responseWrapper.getContentType())) {

            log.debug(
                    "\r\n[REQUEST({})|> username: {}, ip: {}, Agent: {}, URI: {}, \r\n Parameters: {}, TotalTimeCost: [{}]ms \n",
                    prop.getMethod(), username, prop.getIp(), prop.getAgent(), prop.getUri(), prettyRequestParam, timeCost);

            // SAVE ACTIVITY LOG
        } else {
            final String result = convertString(responseWrapper.getContentAsByteArray());
            final String prettyResult = beautifyJson(result);
            final String httpsStatus = HttpStatus.valueOf(responseWrapper.getStatus()).name();

            log.debug(
                    "\r\n[REQUEST({})|> username: {}, ip: {}, Agent: {}, URI: {}, \r\n Parameters: {}, \r\nRESPONSE({}), TotalTimeCost: [{}]ms \n",
                    prop.getMethod(), username, prop.getIp(), prop.getAgent(), prop.getUri(), prettyRequestParam,
                    httpsStatus, timeCost);

            log.trace("RESPONSE DATA=>{}", prettyResult);
        }
    }


    private boolean isNotJsonContentType(String contentType) {
        return contentType == null || !contentType.toLowerCase().startsWith("application/json");
    }

    private String convertString(byte[] contentByte) throws UnsupportedEncodingException {
        try {
            String contentStr = new String(contentByte, CHARSET);
            String postpfix = "......";
            if (StringUtils.isNotEmpty(contentStr)) {
                if (contentStr.length() <= STR_MAX_LEN) {
                    return contentStr;
                } else {
                    return contentStr.subSequence(0, STR_MAX_LEN) + postpfix;
                }
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException ex) {
            log.error("UnsupportedEncodingException: ", ex);
            throw ex;
        }
    }

    /**
     * Update response.
     *
     * @param requestURI      the request URI
     * @param responseWrapper the response wrapper
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void updateResponse(String requestURI, ContentCachingResponseWrapper responseWrapper) throws IOException {
        try {

            HttpServletResponse rawResponse = (HttpServletResponse) responseWrapper.getResponse();
            byte[] body = responseWrapper.getContentAsByteArray();
            try (ServletOutputStream outputStream = rawResponse.getOutputStream()) {

                if (rawResponse.isCommitted() && body.length > 0) {
                    StreamUtils.copy(body, outputStream);

                } else if (body.length > 0) {
                    rawResponse.setContentLength(body.length);
                    StreamUtils.copy(body, rawResponse.getOutputStream());

                }

                finishResponse(outputStream, body);
            }
        } catch (Exception ex) {
            log.error("Error on request address:  {}, cause by: {}", requestURI, ex.getMessage());
        }
    }

    /**
     * Finish response.
     *
     * @param outputStream the output stream
     * @param body         the body
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void finishResponse(ServletOutputStream outputStream, byte[] body) throws IOException {
        if (body.length > 0) {
            outputStream.flush();
            outputStream.close();
        }
    }


    /**
     * Parses the rawJson to map.
     *
     * @param rawJson the rawJson
     * @return the map
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> parseToResult(String rawJson) {

        Map<String, String> jsonData = new HashMap<>();
        try {

            rawJson = StringUtils.isEmpty(rawJson) ? "{}" : rawJson;

            int firstPos = rawJson.indexOf("data\":") + "data\":".length();
            String appendString = "\"\"}";
            String firstPart = rawJson.substring(0, firstPos);

            String resultJson = firstPart + appendString;

            ObjectMapper mapper = new ObjectMapper();
            jsonData = mapper.readValue(resultJson, Map.class);

        } catch (JsonMappingException e) {
            log.error("Minor Type JsonMappingException: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Minor Type JsonProcessingException: {}", e.getMessage());
        }
        return jsonData;
    }

    /**
     * Beautify json.
     *
     * @param json the json
     * @return the string
     */
    private String beautifyJson(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            JsonElement je = JsonParser.parseString(json);
            return gson.toJson(je);
        } catch (Exception e) {
            return json;
        }
    }


    /**
     * Gets the activity annotation.
     *
     * @param request the request
     * @return the activity annotation
     */
    public static ActivityProperty getActivityProperty(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String ip = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String method = request.getMethod();
        return ActivityProperty.builder().uri(requestURI).ip(ip).agent(userAgent).method(method).build();
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            // As of https://en.wikipedia.org/wiki/X-Forwarded-For
            // The general format of the field is: X-Forwarded-For: client, proxy1, proxy2
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }
}