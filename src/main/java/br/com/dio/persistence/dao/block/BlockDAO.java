package br.com.dio.persistence.dao.block;

import java.sql.SQLException;

/**
 * Interface para operações de bloqueio de Cards no banco de dados.
 */
public interface BlockDAO {

    /**
     * Bloqueia um Card específico, registrando o motivo do bloqueio.
     *
     * @param cardId O ID do Card que será bloqueado.
     * @param reason O motivo do bloqueio.
     * @return {@code true} se o bloqueio foi registrado com sucesso, {@code false} caso contrário.
     * @throws SQLException Se ocorrer algum erro de acesso ao banco de dados.
     */
    boolean block(final Long cardId, final String reason) throws SQLException;


    /**
     * Desbloqueia um Card específico, registrando o motivo do desbloqueio.
     *
     * @param cardId O ID do Card que será desbloqueado.
     * @param reason O motivo do desbloqueio.
     * @return {@code true} se o desbloqueio foi registrado com sucesso, {@code false} caso contrário.
     * @throws SQLException Se ocorrer algum erro de acesso ao banco de dados.
     */
    boolean unblock(final String reason,final Long cardId) throws SQLException;
}