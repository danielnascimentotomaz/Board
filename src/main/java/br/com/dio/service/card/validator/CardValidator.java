package br.com.dio.service.card.validator;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.exception.CardAlreadyCancelledException;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardCancelledException;
import br.com.dio.exception.CardFinishedException;

import static br.com.dio.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.entity.BoardColumnKindEnum.FINAL;

public class CardValidator {
    public static void validateCanMove(CardDetailsDTO card, BoardColumnInfoDTO currentColumn) throws CardBlockedException, CardFinishedException {
        // Verifica se o card pertence à coluna atual
        if (!card.columnId().equals(currentColumn.id())) {
            throw new IllegalStateException("O card informado pertence a outro board");
        }

        if (card.blocked()) {
            throw new CardBlockedException("O card " + card.id() + " esta bloqueado, e necessario desbloquea-lo para mover");
        }
        if (currentColumn.kind().equals(FINAL)) {
            throw new CardFinishedException("O card ja foi finalizado");
        }
    }

    public static void validateCanCancel(CardDetailsDTO card, BoardColumnInfoDTO currentColumn) throws CardBlockedException, CardFinishedException, CardAlreadyCancelledException {

        // Verifica se o card pertence à coluna atual
        if (!card.columnId().equals(currentColumn.id())) {
            throw new IllegalStateException("O card informado pertence a outro board");
        }

        if (card.blocked()) {
            throw new CardBlockedException("O card " + card.id()+ " está bloqueado, é necessario desbloquea-lo para cancelar");
        }
        if (currentColumn.kind().equals(FINAL)) {
            throw new CardFinishedException("O card ja foi finalizado, nao pode ser cancelado");
        }
        if (currentColumn.kind().equals(CANCEL)) {
            throw new CardAlreadyCancelledException("O card já está cancelado");
        }
    }

    public static void validateblock(CardDetailsDTO card, BoardColumnInfoDTO currentColumn) throws CardBlockedException, CardFinishedException {
        // Verifica se o card pertence à coluna atual
        if (!card.columnId().equals(currentColumn.id())) {
            throw new IllegalStateException("O card informado pertence a outro board");
        }

        if (card.blocked()) {
            throw new CardBlockedException("O card " + card.id() + " esta bloqueado, e necessario desbloquea-lo.");
        }
        if (currentColumn.kind().equals(FINAL)) {
            String mensage = "O card está em coluna %s e não pode ser bloqueado".formatted(currentColumn.kind());
            throw new CardFinishedException(mensage);
        }

        if (currentColumn.kind().equals(CANCEL)){
            String mensage = "O card está em coluna %s e não pode ser bloqueado".formatted(currentColumn.kind());
            throw new CardCancelledException(mensage);


        }


    }


}
