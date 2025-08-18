package br.com.dio.persistence.config;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionStrategy extends AutoCloseable {
    Connection getConnection()throws SQLException;

    @Override
     void close() throws SQLException;
}
