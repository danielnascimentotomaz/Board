package br.com.dio.service.block;

import br.com.dio.exception.BlockPersistenceException;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardUnblockedException;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.block.BlockDAO;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.service.boardcolumn.BoardColumnServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.SQLException;

@Slf4j // 📝 Lombok gera automaticamente um logger estático e final chamado `log`
public class BlockServiceImpl implements BlockService {

    private final ConnectionStrategy connectionStrategy;
    private final BlockDAO blockDAO;


    public BlockServiceImpl(ConnectionStrategy connectionStrategy, BlockDAO blockDAO) {
        this.connectionStrategy = connectionStrategy;
        this.blockDAO = blockDAO;

    }

    @Override
    public boolean block(Long cardId, String reason){
        try (Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            try {
                boolean blocked = blockDAO.block(cardId,reason);

                connection.commit();

                log.info("Card bloqueado com sucesso: IDCard={}", cardId);

                return blocked;
            } catch (SQLException e) {
                connection.rollback();

                var message = "Erro ao tentar bloquear o card id = ";

                log.error(message + cardId, e);

                throw new CardBlockedException(message + cardId, e);
            }
        }catch (SQLException e) {
            String message = "Falha ao obter conexao com o banco de dados. Verifique a configuracao de conexão.";
            log.error(message, e);
            throw new BlockPersistenceException(message, e);
        }
    }

    @Override
    public boolean unblock(String reason, Long cardId){
        try(Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            try {
                boolean unblock = blockDAO.unblock(reason, cardId);

                connection.commit();

                log.info("Card desbloqueado com sucesso: IDCard={}", cardId);

                return  unblock;


            } catch (SQLException e) {
                connection.rollback();
                var message = "Erro ao tentar desbloquear o card id = ".formatted(cardId);

                log.error(message, e);

                throw new CardUnblockedException(message,e);
            }


        } catch (SQLException e) {
            String message = "Falha ao obter conexao com o banco de dados. Verifique a configuracao de conexão.";
            log.error(message, e);
            throw new BlockPersistenceException(message, e);
        }



    }
}
