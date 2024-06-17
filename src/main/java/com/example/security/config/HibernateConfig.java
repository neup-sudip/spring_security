package com.example.security.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "pmEntityManagerFactory", transactionManagerRef = "pmTransactionManager", basePackages = {"com.example.security"})
public class HibernateConfig {

    @Primary
    @Bean(name = "pmDataSource")
    @ConfigurationProperties("spring.datasource.pm")
    public DataSource pmDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "pmEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pmEntityManagerFactory(@Qualifier("pmDataSource") DataSource dataSource, EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.security")
                .persistenceUnit("pm")
                .build();
    }

    @Primary
    @Bean(name = "pmTransactionManager")
    public PlatformTransactionManager pmTransactionManager(@Qualifier("pmEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
