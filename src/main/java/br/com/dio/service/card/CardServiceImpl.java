package br.com.dio.service.card;

import br.com.dio.entity.CardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.persistence.dao.card.CardDAOImpl;
import br.com.dio.service.boardcolumn.BoardColumnService;
import br.com.dio.service.boardcolumn.BoardColumnServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class CardServiceImpl implements CardService {

    private final  ConnectionStrategy connectionStrategy;
    private final BoardColumnService boardColumnService;

    public CardServiceImpl(ConnectionStrategy connectionStrategy, BoardColumnService boardColumnService) {
        this.connectionStrategy = connectionStrategy;
        this.boardColumnService = boardColumnService;
    }

    @Override
    public CardEntity insert(CardEntity entity) throws SQLException {
        // Tenta obter a conexão
        try (Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            CardDAO cardDAO = new CardDAOImpl(connection);

            BoardColumnService boardColumnService = new BoardColumnServiceImpl(connectionStrategy);

            // Verificar se existe o Id BoardColumn
            if (!boardColumnService.exists(entity.getBoardColumn().getId())) {
                throw new IllegalArgumentException("A coluna informada não existe: " + entity.getBoardColumn().getId());
            }


            try {
                cardDAO.insert(entity);      // insere o card
                connection.commit();         // confirma transação
                return entity;
            } catch (SQLException e) {
                connection.rollback();       // desfaz transação em caso de erro
                throw new SQLException("Erro ao tentar inserir um Card", e);
            }

        } catch (IllegalArgumentException e){
            throw e;
        }

        catch (SQLException e) {
            throw new SQLException("Falha ao conectar ou operar no banco", e);
        }
    }
}
