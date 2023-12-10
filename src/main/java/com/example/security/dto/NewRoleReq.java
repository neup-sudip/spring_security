package com.example.security.dto;

public class NewRoleReq {
    private long authorityId;

    private String name;

    public NewRoleReq(long authorityId, String name) {
        this.authorityId = authorityId;
        this.name = name;
    }

    public long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(long authorityId) {
        this.authorityId = authorityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
