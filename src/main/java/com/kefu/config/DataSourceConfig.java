package com.kefu.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Locale;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String jdbcUrl = properties.getUrl();
        String resolvedDriver = resolveDriver(properties.getDriverClassName(), jdbcUrl);
        if (resolvedDriver != null && !resolvedDriver.isBlank()) {
            properties.setDriverClassName(resolvedDriver);
        }

        return properties.initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
    }

    static String resolveDriver(String configuredDriver, String jdbcUrl) {
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            String normalizedUrl = jdbcUrl.trim().toLowerCase(Locale.ROOT);
            if (normalizedUrl.startsWith("jdbc:h2:")) {
                return "org.h2.Driver";
            }
            if (normalizedUrl.startsWith("jdbc:mysql:")) {
                return "com.mysql.cj.jdbc.Driver";
            }
        }

        if (configuredDriver != null && !configuredDriver.isBlank()) {
            return configuredDriver;
        }
        return "org.h2.Driver";
    }
}
