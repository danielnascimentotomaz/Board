package br.com.dio.service.board;

import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.board.BoardDAO;
import br.com.dio.persistence.dao.board.BoardDAOImpl;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BoardService {
    private final ConnectionStrategy connectionStrategy;

    public BoardService(ConnectionStrategy connectionStrategy) {

        this.connectionStrategy = connectionStrategy;
    }

    public BoardEntity insert(final BoardEntity entity) throws SQLException{
        try (
                Connection connection = connectionStrategy.getConnection()
        ) {
            connection.setAutoCommit(false);
            BoardDAO boardDAO = new BoardDAOImpl(connection);
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);

            try {

                boardDAO.insert(entity);
                List<BoardColumnEntity> columnEntities = entity.getBoardColumns().
                        stream().map(c -> {
                            c.setBoard(entity);
                            return c;
                        }).toList();

                for (var column : columnEntities){

                    boardColumnDAO.insert(column);

                }

        connection.commit();
                return entity;

            }catch (SQLException e ){
                connection.rollback();
                throw new SQLException("Erro ao tentar Inserir o board " , e);

            }

        }catch (SQLException e){
            throw new SQLException("Falha ao conectar ao banco", e);
            
        }


    }








    public boolean delete(Long id) throws SQLException {
        try (
                Connection connection = connectionStrategy.getConnection()
        ) {
            connection.setAutoCommit(false);
            BoardDAO boardDAO = new BoardDAOImpl(connection);

            try {
                if (!boardDAO.exists(id)) {
                    return false;
                }

                boardDAO.delete(id);
                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Erro ao tentar deletar o board com ID: " + id, e);
            }

        } catch (SQLException e) {
            throw new SQLException("Falha ao conectar ao banco", e);
        }
    }
}