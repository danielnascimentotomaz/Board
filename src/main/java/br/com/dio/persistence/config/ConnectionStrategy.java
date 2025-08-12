package br.com.dio.persistence.config;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionStrategy {
    Connection getConnection()throws SQLException;
}
