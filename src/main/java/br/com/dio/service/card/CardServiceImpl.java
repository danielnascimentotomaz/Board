package br.com.dio.service.card;

import br.com.dio.entity.CardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.persistence.dao.card.CardDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class CardServiceImpl implements CardService {

    private final  ConnectionStrategy connectionStrategy;

    public CardServiceImpl(ConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

    @Override
    public CardEntity insert(CardEntity entity) throws SQLException {

        try(Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            CardDAO cardDAO = new CardDAOImpl(connection);

            /*BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection); // precisa ter esse DAO

            try {
                // ✅ verifica se a coluna existe
                if (!boardColumnDAO.exists(entity.getBoardColumn().getId())) {
                    throw new SQLException("A coluna informada não existe: " + entity.getBoardColumn().getId());
                }
            */

            try {
                cardDAO.insert(entity);
                connection.commit();
                return entity;
            }catch (SQLException e){
                connection.rollback();
                throw new SQLException("Erro ao tentar Inserir um Card" , e);
            }

        }catch (SQLException e){
            throw new SQLException("Falha ao conectar ao banco", e);
        }

    }
}
