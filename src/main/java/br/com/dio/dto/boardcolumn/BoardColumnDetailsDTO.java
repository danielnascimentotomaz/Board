package br.com.dio.dto.boardcolumn;

import br.com.dio.dto.card.CardDTO;
import br.com.dio.entity.BoardColumnKindEnum;

import java.util.List;

public record BoardColumnDetailsDTO(
        String name,
        BoardColumnKindEnum kind,
        List<CardDTO> cards

) {
}
