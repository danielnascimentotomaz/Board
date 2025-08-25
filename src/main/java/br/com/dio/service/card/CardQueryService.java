package br.com.dio.service.card;

import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.persistence.dao.card.CardDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class CardQueryService {
    private final ConnectionStrategy connectionStrategy;

    public CardQueryService(ConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

    public Optional<CardDetailsDTO> findById(final Long idCard) throws SQLException {
        try (Connection connection = connectionStrategy.getConnection()) {
            CardDAO cardDAO = new CardDAOImpl(connection);
            Optional<CardDetailsDTO> cardDetailsDTO = cardDAO.findById(idCard);
            return cardDetailsDTO;
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar Card pelo ID: " + idCard, e);
        }
    }



}
