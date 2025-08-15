package br.com.dio.service.boardcolumn;

import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class BoardColumnServiceImpl implements  BoardColumnService{

    private final ConnectionStrategy connectionStrategy;

    public BoardColumnServiceImpl(ConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }


    @Override
    public BoardColumnEntity insert(BoardColumnEntity entity)throws SQLException {
        try(
                Connection connection = connectionStrategy.getConnection();
                ) {
            connection.setAutoCommit(false);

            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);

            try {
                boardColumnDAO.insert(entity);
                connection.commit();
                return entity;
            }catch (SQLException e){
                connection.rollback();
                throw new SQLException("Erro ao tentar Inserir o board column " , e);

            }

        }catch (SQLException e){
            throw new SQLException("Falha ao conectar ao banco", e);
        }

    }
}
