package br.com.dio.service.boardcolumn;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.exception.BoardColumnAccessException;
import br.com.dio.exception.BoardColumnNotFoundException;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class BoardColumnServiceImpl implements  BoardColumnService{

    // Cria o logger da classe
    private static final Logger log = LoggerFactory.getLogger(BoardColumnServiceImpl.class);


    private final ConnectionStrategy connectionStrategy;


    public BoardColumnServiceImpl(ConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

    @Override
    public BoardColumnEntity insert(BoardColumnEntity entity) {
        try (Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            // ⚠️ ATENÇÃO: Criar um DAO a cada metodo nao e uma boa prática.
            // ✅ Melhor prática: criar o DAO no construtor da classe e reutilizar.
            // 🔹 Aqui estamos instanciando no metodo apenas como exemplo didático.
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);

            try {
                BoardColumnEntity saved = boardColumnDAO.insert(entity);
                connection.commit();
                log.info("BoardColumn inserida com sucesso: name={}", entity.getName());
                return saved;

            } catch (SQLException e) {
                connection.rollback();
                var menssage = "Falha ao acessar o banco para inserir BoardColumn: name= ";

                log.error(menssage+ entity.getName(), e);

                throw new BoardColumnAccessException(menssage+ entity.getName(), e);
            }

        } catch (SQLException e) {
            var menssage = "Falha ao acessar o banco para inserir BoardColumn: name=";

            log.error(menssage+ entity.getName(), e);

            throw new BoardColumnAccessException(menssage+ entity.getName(), e);
        }
    }

    @Override
    public boolean exists(Long id) {
        try (Connection connection = connectionStrategy.getConnection()) {
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);
            return boardColumnDAO.exists(id);

        } catch (SQLException e) {
            var menssage = "Falha ao acessar o banco para verificar existência da coluna: id=";

            log.error(menssage+ id, e);

            throw new BoardColumnAccessException(menssage+ id, e);
        }
    }

    @Override
    public Optional<BoardColumnInfoDTO> findInfoById(Long id) {
        try (Connection connection = connectionStrategy.getConnection()) {
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);

            Optional<BoardColumnInfoDTO> column = boardColumnDAO.findInfoById(id);

            if (column.isEmpty()) {
                var message = "Coluna não encontrada: id= ";

                log.warn(message + id);

                throw new BoardColumnNotFoundException(message + id);
            }
            return column;

        } catch (SQLException e) {
            var menssage = "Falha ao acessar o banco para buscar coluna: id=";

            log.error(menssage+ id, e);

            throw new BoardColumnAccessException(menssage+ id, e);
        }
    }

    @Override
    public List<BoardColumnEntity> findAllColumnsByBoardId(Long boardId) {
        try (Connection connection = connectionStrategy.getConnection()) {
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);
            return boardColumnDAO.findAllColumnsByBoardId(boardId);

        } catch (SQLException e) {
            var menssage = "Falha ao acessar o banco para buscar colunas do board: boardId = ";

            log.error(menssage + boardId, e);

            throw new BoardColumnAccessException(menssage + boardId, e);
        }
    }

}
