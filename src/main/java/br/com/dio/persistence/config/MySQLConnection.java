package br.com.dio.persistence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

import java.sql.SQLException;


public class MySQLConnection implements ConnectionStrategy {

    private static volatile MySQLConnection INSTANCE;
    private final HikariDataSource dataSource;

    private MySQLConnection() {
        HikariConfig config = new HikariConfig();

        String jdbcUrl = System.getenv("DB_URL");
        if (jdbcUrl == null) throw new IllegalStateException("DB_URL environment variable not set");

        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");
        if (user == null || pass == null) throw new IllegalStateException("DB_USER or DB_PASS not set");

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
    }

    public static MySQLConnection getInstance() {
        if (INSTANCE == null) {
            synchronized (MySQLConnection.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MySQLConnection();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Connection getConnection() throws SQLException {

        return dataSource.getConnection();
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}