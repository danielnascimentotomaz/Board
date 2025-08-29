package br.com.dio.service.card;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.exception.BoardColumnNotFoundException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.service.boardcolumn.BoardColumnService;
import br.com.dio.service.card.validator.CardValidator;

import java.sql.SQLException;
import java.util.List;
public class MoveToNextColumnAction implements CardAction {
    // Classe que executa a ação de mover um card para a próxima coluna

    private final CardDAO cardDAO;
    // DAO para acessar os dados do card no banco

    private final BoardColumnService boardColumnService;
    // Serviço para buscar informações das colunas

    public MoveToNextColumnAction(CardDAO cardDAO, BoardColumnService boardColumnService) {
        this.cardDAO = cardDAO; // injeta o DAO de card
        this.boardColumnService = boardColumnService; // injeta o serviço de colunas
    }

    @Override
    public void execute(long cardId) throws SQLException {
        // Metodo que executa a ação de mover para a próxima coluna

        CardDetailsDTO card = cardDAO.findById(cardId)
                // Busca card pelo ID
                .orElseThrow(() -> new EntityNotFoundException("O card de id " + cardId + " nao foi encontrado"));
        // Se não existir, lança exceção

        BoardColumnInfoDTO currentColumn = getColumn(card.columnId());
        // Busca a coluna atual do card

        CardValidator.validateCanMove(card, currentColumn);
        // Valida se o card pode ser movido (não bloqueado, não finalizado)

        List<BoardColumnEntity> columns = getAllColumns(currentColumn.boardId());
        // Busca todas as colunas do board do card

        BoardColumnEntity nextColumn = getNextColumn(columns ,currentColumn,cardId);
        // Pega a próxima coluna depois da atual

        cardDAO.moveToColumn(nextColumn.getId(), cardId);
        // Atualiza o card no banco para a nova coluna
    }

    private BoardColumnInfoDTO getColumn(Long columnId) {
        // Busca informações de uma coluna pelo ID
        return boardColumnService.findInfoById(columnId)
                // Tenta buscar coluna
                .orElseThrow(() -> new EntityNotFoundException(
                        "Coluna não encontrada com id = " + columnId
                ));

    }

    private List<BoardColumnEntity> getAllColumns(long boardId){
        // Busca todas as colunas de um board
        List<BoardColumnEntity> columns = boardColumnService.findAllColumnsByBoardId(boardId);
        return columns; // Retorna lista de colunas
    }

    private BoardColumnEntity getNextColumn(List<BoardColumnEntity> columns, BoardColumnInfoDTO currentColumn, Long cardId) {
        // Busca a próxima coluna após a atual
        return columns.stream()
                .filter(c -> c.getOrder() > currentColumn.order())
                // Filtra colunas com ordem maior que a atual
                .findFirst()
                // Pega a primeira que encontrar
                .orElseThrow(() ->
                        new IllegalStateException("Não existe próxima coluna para mover o card " + cardId)
                );
        // Se não achar, lança exceção
    }
}