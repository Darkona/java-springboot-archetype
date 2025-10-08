package com.fci.sdk.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * REST controller for PostgreSQL operations.
 * 
 * This controller provides endpoints for storing and retrieving data from PostgreSQL.
 * It implements the DatabaseOperations interface to ensure consistent API behavior
 * across different database implementations.
 * 
 * @author Gonzalo Barco
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/dummy/postgresql")
@Slf4j
public class PostgreSQLController implements DatabaseOperations {

    /** PostgreSQL table name for storing string data */
    private static final String TABLE_NAME = "dummy_strings_table";

    /** JDBC template for database operations */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** Data source for database connections */
    @Autowired
    private DataSource dataSource;

    @Override
    public ResponseEntity<List<String>> store(final List<String> data) {
        try {
            log.info("Storing {} items in PostgreSQL table: {}", data.size(), TABLE_NAME);

            // Create table if it doesn't exist
            createTableIfNotExists(TABLE_NAME);

            // Clear existing data and insert new data
            jdbcTemplate.execute("DELETE FROM " + TABLE_NAME);

            for (int i = 0; i < data.size(); i++) {
                jdbcTemplate.update(
                    "INSERT INTO " + TABLE_NAME + " (id, value, index_position) VALUES (?, ?, ?)",
                    i + 1, data.get(i), i
                );
            }

            log.info("Successfully stored {} items in PostgreSQL table: {}", data.size(), TABLE_NAME);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            final String errorMessage = String.format("Error storing data in PostgreSQL: %s", e.getMessage());
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(errorMessage));
        }
    }

    @Override
    public ResponseEntity<List<String>> retrieve() {
        try {
            log.info("Retrieving data from PostgreSQL table: {}", TABLE_NAME);

            // Check if table exists
            if (!tableExists(TABLE_NAME)) {
                final String message = String.format("Table not found: %s", TABLE_NAME);
                log.warn(message);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(message));
            }

            final List<String> result = jdbcTemplate.queryForList(
                "SELECT value FROM " + TABLE_NAME + " ORDER BY index_position",
                String.class
            );

            if (result.isEmpty()) {
                final String message = String.format("Table is empty: %s", TABLE_NAME);
                log.warn(message);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(message));
            }

            log.info("Successfully retrieved {} items from PostgreSQL table: {}", result.size(), TABLE_NAME);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            final String errorMessage = String.format("Error retrieving data from PostgreSQL: %s", e.getMessage());
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(errorMessage));
        }
    }

    /**
     * Creates the table if it doesn't exist in the database.
     *
     * @param tableName the name of the table to create
     * @throws SQLException if there's an error creating the table
     */
    private void createTableIfNotExists(final String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData metaData = connection.getMetaData();
            final ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});

            if (!tables.next()) {
                // Table doesn't exist, create it
                final String createTableSql = String.format(
                    "CREATE TABLE %s (" +
                    "id SERIAL PRIMARY KEY, " +
                    "value TEXT NOT NULL, " +
                    "index_position INTEGER NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")", tableName
                );

                try (tables; Statement statement = connection.createStatement()) {
                    statement.execute(createTableSql);
                    log.info("Created table: {}", tableName);
                }
            }
        }
    }

    /**
     * Checks if a table exists in the database.
     * 
     * @param tableName the name of the table to check
     * @return true if the table exists, false otherwise
     * @throws SQLException if there's an error checking table existence
     */
    private boolean tableExists(final String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            final DatabaseMetaData metaData = connection.getMetaData();
            final ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            return tables.next();
        }
    }
} 
