package br.com.dio.service.card;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;
import br.com.dio.entity.CardEntity;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.persistence.dao.card.CardDAOImpl;
import br.com.dio.service.boardcolumn.BoardColumnService;
import br.com.dio.service.boardcolumn.BoardColumnServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.dio.entity.BoardColumnKindEnum.FINAL;

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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void moveToNextColumn(long cardId, List<BoardColumnInfoDTO> boardColumnsInfo) throws Exception {
        try(Connection connection = connectionStrategy.getConnection()){
            connection.setAutoCommit(false); // inicia transação manual

            CardDAO cardDAO = new CardDAOImpl(connection);

            // Recupera o Card pelo ID
            Optional<CardDetailsDTO> optional = cardDAO.findById(cardId);

            // Verifica se o Card foi encontrado, caso contrário lança EntityNotFoundException
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );

            // Verifica se o card está bloqueado
            if (dto.blocked()){
                var mensage = "O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId);
                throw new CardBlockedException(mensage);
            }

            // Verifica se o card pertence ao board informado
            BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));

            // Verifica se o card já está na coluna final
            if(currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card já foi finalizado");
            }

            try {
                // Localiza a próxima coluna baseada na ordem
                BoardColumnInfoDTO nextColumn = boardColumnsInfo.stream()
                        .filter(bc -> bc.order() == currentColumn.order() + 1)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Não existe próxima coluna para mover o card " + cardId));

                // Atualiza o card no banco, movendo para a próxima coluna
                cardDAO.moveToColumn(nextColumn.id(), cardId);

                // Se chegou até aqui, confirma a transação
                connection.commit();

            }catch (SQLException e){
                // Se der erro na atualização, faz rollback da transação
                connection.rollback();
                throw new SQLException("Erro ao atualizar a coluna do card " + cardId, e);
            }

        }catch (SQLException ex){
            // Captura erro de conexão ou execução e propaga com mensagem mais clara
            throw new SQLException("Erro de banco ao mover card " + cardId + " para a próxima coluna", ex);
        }
    }



    public void moveToNextColumn(long cardId) throws Exception {
        try(Connection connection = connectionStrategy.getConnection()) {
            connection.setAutoCommit(false); // inicia transação manual


            CardDAO cardDAO = new CardDAOImpl(connection);

            // Busca o Card pelo ID
            Optional<CardDetailsDTO> cardDetailsDTOOptional = cardDAO.findById(cardId);

            // Verifica se o Card existe
            if (cardDetailsDTOOptional.isEmpty()){
                throw new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId));
            }

            // Se chegou aqui, já sabemos que existe
            CardDetailsDTO dto = cardDetailsDTOOptional.get();

            // Verifica se o card está bloqueado
            if (dto.blocked()){
                var mensage = "O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId);
                throw new CardBlockedException(mensage);
            }

            try {
                BoardColumnService boardColumnService = new BoardColumnServiceImpl(connectionStrategy);


                // Coluna Atual CARD
                BoardColumnInfoDTO currentColumn = boardColumnService.findInfoById(dto.columnId()).
                        orElseThrow(() -> new EntityNotFoundException("Coluna não encontrada"));



                    //Verifando a posição da coluna atual
                    if (currentColumn.kind().equals(FINAL)){
                    throw new CardFinishedException("O card já foi finalizado");
                    }

                    // 🔒 Garantia extra: card pertence ao mesmo board da coluna
                    if (!Objects.equals(currentColumn.boardId(), dto.boardId())) {
                    throw new IllegalStateException(
                            "O card %s não pertence ao board %s".formatted(cardId, currentColumn.boardId()));
                    }


                // Buscando todas colunas do pertencente ao Board
                List<BoardColumnEntity> columns = boardColumnService.findAllColumnsByBoardId(currentColumn.boardId());

                // Localiza a próxima coluna baseada na ordem
                BoardColumnEntity nextColumn = columns.stream()
                        .filter(c -> c.getOrder() > currentColumn.order())
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Não há próxima coluna"));


                // Atualiza o card para a próxima coluna
                cardDAO.moveToColumn(nextColumn.getId(),cardId);

                connection.commit();

            }catch (SQLException e){
                connection.rollback();
                throw e;
            }
        }






    }


}
