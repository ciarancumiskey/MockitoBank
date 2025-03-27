package com.ciarancumiskey.mockitobank;

import com.ciarancumiskey.mockitobank.utils.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class MysqlBackedContainerTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = TestUtils.getMySqlEmulatorContainer();

    @BeforeAll
    public static void setup() throws SQLException {
        mySQLContainer.start();
        try (Connection conn = DriverManager.getConnection(mySQLContainer.getJdbcUrl(), "test", "test");
             Statement stmt = conn.createStatement()) {
            // TODO Create Account table
            // TODO Create Transaction table
        }
    }

    @Test
    public void testContainerIsRunning(){
        assertTrue(mySQLContainer.isCreated());
        assertTrue(mySQLContainer.isRunning());
    }

    @AfterAll
    public static void cleanup(){
        mySQLContainer.stop();
    }
}
