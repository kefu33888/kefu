package com.kefu.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataSourceConfigTest {

    @Test
    void resolvesH2DriverForEmbeddedUrl() {
        assertEquals("org.h2.Driver", DataSourceConfig.resolveDriver("com.mysql.cj.jdbc.Driver", "jdbc:h2:mem:kefu;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"));
    }

    @Test
    void resolvesMysqlDriverForMysqlUrl() {
        assertEquals("com.mysql.cj.jdbc.Driver", DataSourceConfig.resolveDriver(null, "jdbc:mysql://localhost:3306/test"));
    }
}
