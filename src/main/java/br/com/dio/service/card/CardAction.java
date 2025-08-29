package br.com.dio.service.card;

import java.sql.SQLException;

public interface CardAction {
    void execute(long cardId) throws SQLException;
}
