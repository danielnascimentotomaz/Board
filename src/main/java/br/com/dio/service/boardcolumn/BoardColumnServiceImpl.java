package br.com.dio.service.boardcolumn;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


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

    @Override
    public boolean exists(Long id) throws SQLException {
        try(Connection connection = connectionStrategy.getConnection()) {
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);
            try {
                return boardColumnDAO.exists(id);
            }catch (RuntimeException e){
                // Pode lançar uma exceção de negócio ou retornar false
                throw new RuntimeException("Não foi possível verificar a coluna", e);
            }


        }

    }

    @Override
    public Optional<BoardColumnInfoDTO> findInfoById(Long id) throws SQLException {
        try (Connection connection = connectionStrategy.getConnection()) {
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);
            try {
                return boardColumnDAO.findInfoById(id);
            } catch (SQLException e) {
                // Mantém a causa original
                throw new SQLException("Erro ao buscar BoardColumnInfoDTO com id " + id, e);
            }
        } catch (SQLException e) {
            throw new SQLException("Falha ao conectar ao banco", e);
        }
    }

    @Override
    public List<BoardColumnEntity> findAllColumnsByBoardId(Long boardId) throws SQLException {
        try(Connection connection = connectionStrategy.getConnection()) {
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);
            try {
                return boardColumnDAO.findAllColumnsByBoardId(boardId);
            }catch (SQLException e){
                throw new SQLException("Erro ao buscar BoardColumnEntity com boarId " + boardId, e);
            }

        }catch (SQLException e) {
            throw new SQLException("Falha ao conectar ao banco", e);
        }


    }




}
