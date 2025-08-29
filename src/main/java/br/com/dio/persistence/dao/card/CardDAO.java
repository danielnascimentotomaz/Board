package br.com.dio.persistence.dao.card;

import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.entity.CardEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operações de persistência de Card.
 *
 * Este DAO não gerencia transações ou conexões, apenas executa operações SQL.
 */
public interface CardDAO {

    /**
     * Insere um novo card no banco de dados.
     *
     * @param entity Card a ser inserido (ID deve ser null)
     * @param connection Conexão JDBC fornecida pelo service
     * @return Card inserido com ID gerado
     * @throws SQLException em caso de falha na inserção
     */
    CardEntity insert(CardEntity entity) throws SQLException;

    /**
     * Busca um card pelo seu ID.
     *
     * @param cardId ID do card
     * @return Optional contendo o CardDetailsDTO se encontrado, ou vazio caso não exista
     * @throws SQLException em caso de erro no banco
     */
    Optional<CardDetailsDTO> findById(Long cardId) throws SQLException;

    /**
     * Verifica se um card existe no banco.
     *
     * @param id ID do card
     * @return true se o card existir, false caso contrário
     * @throws SQLException em caso de erro no banco
     */
    boolean exists(Long id) throws SQLException;

    /**
     * Move um card para uma coluna específica.
     *
     * @param columnId ID da coluna destino
     * @param cardId ID do card
     * @return true se a operação atualizou algum registro, false caso contrário
     * @throws SQLException em caso de erro no banco
     */
    boolean moveToColumn(Long columnId, Long cardId) throws SQLException;

    /**
     * Atualiza os dados completos de um card.
     *
     * @param card Card com dados atualizados (ID obrigatório)
     * @return true se a atualização foi bem sucedida, false caso contrário
     * @throws SQLException em caso de erro no banco
     */
    boolean update(CardEntity card) throws SQLException;

    /**
     * Remove um card do banco de dados.
     *
     * @param cardId ID do card a ser removido
     * @return true se a remoção foi bem sucedida, false caso contrário
     * @throws SQLException em caso de erro no banco
     */
    boolean delete(Long cardId) throws SQLException;
}