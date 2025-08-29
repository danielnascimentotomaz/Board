package br.com.dio.service.card;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.service.boardcolumn.BoardColumnService;
import br.com.dio.service.card.validator.CardValidator;

import java.sql.SQLException;
import java.util.List;

import static br.com.dio.entity.BoardColumnKindEnum.CANCEL;

// Classe responsável por cancelar um card (mover para coluna CANCEL)
public class CancelCardAction implements CardAction {


    // DAO para acessar os dados do card no banco
    private final CardDAO cardDAO;

    // Serviço para buscar informações das colunas do board
    private final BoardColumnService boardColumnService;


    public CancelCardAction(CardDAO cardDAO, BoardColumnService boardColumnService) {
        this.cardDAO = cardDAO; // injeta dependência do DAO
        this.boardColumnService = boardColumnService; // injeta dependência do serviço
    }

    // Metodo que executa a ação de cancelar um card
    @Override
    public void execute(long cardId) throws SQLException {



            // Busca o card no banco pelo ID
            CardDetailsDTO card = cardDAO.findById(cardId)
                    .orElseThrow(() -> new EntityNotFoundException("O card de id " + cardId + " nao foi encontrado"));


            // Busca a coluna atual em que o card está
            BoardColumnInfoDTO currentColumn = getColumn(card.columnId());


            // Validação se o Card
            CardValidator.validateCanCancel(card, currentColumn);


            // Busca todas as colunas do board
            List<BoardColumnEntity> columns = getAllColumns(currentColumn.boardId());


            // Encontra a coluna de tipo CANCEL
            BoardColumnEntity cancelColumn = getCancelColumn(columns);


            // Atualiza o card no banco, movendo para a coluna CANCEL
            cardDAO.moveToColumn(cancelColumn.getId(), cardId);





    }




    // Busca informações de uma coluna pelo ID
    private BoardColumnInfoDTO getColumn(Long columnId) {

        // Tenta buscar coluna pelo serviço
        return boardColumnService.findInfoById(columnId)

                .orElseThrow(() -> new EntityNotFoundException(
                        "Coluna não encontrada com id = " + columnId
                ));

    }


    // Busca todas as colunas de um board
    private List<BoardColumnEntity> getAllColumns(long boardId){

        List<BoardColumnEntity> columns = boardColumnService.findAllColumnsByBoardId(boardId);

        // Retorna a lista encontrada
        return columns;
    }


    // Busca a coluna que representa CANCEL
    private BoardColumnEntity getCancelColumn(List<BoardColumnEntity> columns){

        return columns.stream()
                // Filtra apenas colunas com tipo CANCEL
                .filter(c -> c.getKind().equals(CANCEL))
                // Pega a primeira encontrada
                .findFirst()
                // Se não existir, lança exceção
                .orElseThrow(() -> new IllegalStateException(
                        "Board não possui coluna de cancelamento"));

    }
}
