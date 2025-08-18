package br.com.dio.persistence.migration;


import br.com.dio.persistence.config.MySQLConnection;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;


public class MigrationStrategy {

    // Conexão do banco que será usada para executar a migração
    private final Connection connection;

    public MigrationStrategy(Connection connection) {
        this.connection = connection;
    }

    // Metodo  para executar a migração usando Liquibase
    public void executeMigration() {

       /* ======= Quando for trabalhar com terminal criar um arquivo pra salvar os log do
          ======= Liquibase
        */

        // Salva os streams originais de saída e erro para restaurar depois
        var originalOut = System.out;
        var originalErr = System.err;

        try (var fos = new FileOutputStream("liquibase.log")) { // Abre arquivo para logar saída

            // Redireciona saída padrão e erro padrão para o arquivo de log
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));


            /// //////////////// criar Instância Liquibase  ////////////////

            // Usa a conexão passada no construtor para criar um JdbcConnection para Liquibase
            try (var jdbcConnection = new JdbcConnection(connection)) {
                // Inicializa Liquibase apontando para o arquivo master changelog e usando o class loader
                var liquibase = new Liquibase(
                        "/db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection
                );

                // Executa a atualização das mudanças no banco
                liquibase.update("");
            } catch (LiquibaseException e) {
                // Caso ocorra erro, lança runtime exception para interromper a execução
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            // Se não conseguir abrir o arquivo para log, lança runtime exception
            throw new RuntimeException(e);
        }finally {
            // 🔥 restaura os streams originais
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}