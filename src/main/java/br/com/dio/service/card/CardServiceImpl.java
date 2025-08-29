package br.com.dio.service.card;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.entity.CardEntity;
import br.com.dio.exception.CardActionException;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardCancelledException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.CardNotFoundException;
import br.com.dio.exception.CardPersistenceException;
import br.com.dio.exception.CardUnblockedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.persistence.dao.card.CardDAOImpl;
import br.com.dio.service.block.BlockService;
import br.com.dio.service.boardcolumn.BoardColumnService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

import static br.com.dio.service.card.validator.CardValidator.validateblock;
@Slf4j
public class CardServiceImpl implements CardService {

    private final ConnectionStrategy connectionStrategy;
    private final BoardColumnService boardColumnService;
    private final CardDAO cardDAO;
    private final BlockService blockService;


    public CardServiceImpl(ConnectionStrategy connectionStrategy, BoardColumnService boardColumnService, CardDAO cardDAO, BlockService blockService) {
        this.connectionStrategy = connectionStrategy;
        this.boardColumnService = boardColumnService;
        this.cardDAO = cardDAO;
        this.blockService = blockService;
    }

    @Override
    public CardEntity insert(CardEntity entity) throws SQLException {
        try (Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            CardDAO cardDAO = new CardDAOImpl(connection);

            if (!boardColumnService.exists(entity.getBoardColumn().getId())) {
                throw new IllegalArgumentException("A coluna informada não existe: " + entity.getBoardColumn().getId());
            }

            try {
                cardDAO.insert(entity);
                connection.commit();
                return entity;
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Erro ao tentar inserir um Card", e);
            }
        }
    }

    @Override
    public CardDetailsDTO findById(long cardId) throws SQLException {
        try (Connection connection = connectionStrategy.getConnection()) {
            CardDAO cardDAO = new CardDAOImpl(connection);
            return cardDAO.findById(cardId)
                    .orElseThrow(() -> new EntityNotFoundException("O card de id " + cardId + " não foi encontrado"));
        }
    }

    @Override
    public boolean exists(Long id) throws SQLException {
        try(Connection connection = connectionStrategy.getConnection()) {

            boolean exists = cardDAO.exists(id);

            if (!exists){
                String notFoundMessage = "Card não encontrado com id = " + id;
                throw  new CardNotFoundException(notFoundMessage);
            }
            return true;

        }catch (SQLException e){
            String errorMessage = "Erro ao verificar se o card existe no banco de dados";

            throw new SQLException(errorMessage, e);
        }
    }

    @Override
    public void moveToNextColumn(long cardId) throws Exception {
        try (Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            CardDAO cardDAO = new CardDAOImpl(connection);
            CardAction action = new MoveToNextColumnAction(cardDAO, boardColumnService);
            action.execute(cardId);

            connection.commit();
        } catch (SQLException e) {
            throw new SQLException("Erro ao mover card " + cardId, e);
        }
    }

    @Override
    public void cancelCard(long cardId) {
        try (Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            CardDAO cardDAO = new CardDAOImpl(connection);
            CardAction action = new CancelCardAction(cardDAO, boardColumnService);

            try {
                action.execute(cardId);
                connection.commit();
                log.info("Card cancelado com sucesso: id={}", cardId);

            }catch (SQLException e){
                connection.rollback();

                var message = "Erro ao executar ação de cancelamento para card: id=" + cardId;

                log.error(message, cardId, e);

                throw new CardActionException(message,e);

            }

        } catch (SQLException e) {
            var message = "Falha ao acessar o banco ao cancelar card com id: ";
            throw new CardPersistenceException(message+ cardId, e);
        }
    }



    @Override
    public boolean block(long cardId, String reason) throws SQLException, EntityNotFoundException {
        try (Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Buscar card
                CardDetailsDTO cardDetailsDTO = findById(cardId);

                // Buscar coluna
                BoardColumnInfoDTO currentColumn = boardColumnService.findInfoById(cardDetailsDTO.columnId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Coluna não encontrada para id: " + cardDetailsDTO.columnId()));

                // Validação de regras de negócio
                validateblock(cardDetailsDTO, currentColumn);

                // Bloquear card
                boolean blocked = blockService.block(cardId, reason);

                connection.commit();
                return blocked;

            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Falha ao acessar o banco ao bloquear card com id: " + cardId, e);
            }
        }
    }

    @Override
    public boolean unblock(long cardId, String reason) {
        try{
            // Buscar Card
            CardDetailsDTO cardDetailsDTO = findById(cardId);

            if (!cardDetailsDTO.blocked()){
                var message = "O card com " + cardId +" não está bloqueado e não pode ser desbloqueado.";
                log.info(message);
                throw  new CardUnblockedException(message);
            }

            // Desbloquear Car
            boolean unblock = blockService.unblock(reason,cardId);
            return unblock;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}