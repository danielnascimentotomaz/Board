package br.com.dio.persistence.dao.card;

import br.com.dio.entity.CardEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CardDAO {

    /**
     * Insere um novo card no banco de dados.
     *
     * @param entity Card a ser inserido
     * @return Card inserido com ID gerado
     * @throws SQLException
     */
    CardEntity insert(CardEntity entity) throws SQLException;

    /**
     * Retorna todos os cards existentes.
     *
     * @return Lista de cards
     * @throws SQLException
     */
    List<CardEntity> findAll() throws SQLException;

    /**
     * Busca um card pelo seu ID.
     *
     * @param cardId ID do card
     * @return Card, se encontrado
     * @throws SQLException
     */
    Optional<CardEntity> findById(Long cardId) throws SQLException;

    /**
     * Atualiza os dados completos de um card.
     *
     * @param card Card com dados atualizados (ID obrigatório)
     * @return true se a atualização foi bem sucedida
     * @throws SQLException
     */
    boolean update(CardEntity card) throws SQLException;

    /**
     * Atualiza o status de um card (ex: BLOQUEADO, CANCELADO, FINALIZADO)
     *
     * @param cardId ID do card
     * @return true se a atualização foi bem sucedida
     * @throws SQLException
     */
    boolean updateStatus(Long cardId) throws SQLException;

    /**
     * Move o card para outra coluna.
     *
     * @param cardId   ID do card
     * @param columnId ID da coluna destino
     * @return true se o movimento foi bem sucedido
     * @throws SQLException
     */
    boolean moveToColumn(Long cardId, Long columnId) throws SQLException;

}