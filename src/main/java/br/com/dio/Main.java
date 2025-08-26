package br.com.dio;

import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.config.MySQLConnection;
import br.com.dio.persistence.migration.MigrationStrategy;
import br.com.dio.service.board.BoardService;
import br.com.dio.ui.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;


public class Main {
    private final static MySQLConnection mySQLConnection = MySQLConnection.getInstance();

    public static void main(String[] args) throws SQLException {


            new MigrationStrategy(mySQLConnection.getConnection()).executeMigration();
            MainMenu mainMenu = new MainMenu(mySQLConnection);
            mainMenu.execute();



    }
}