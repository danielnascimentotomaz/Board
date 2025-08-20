package br.com.dio.dto;

import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind,
                             int cardsAmount ) {


    // Método de fábrica para converter entidade -> DTO
    public static BoardColumnDTO fromEntity(BoardColumnEntity entity) {
        return new BoardColumnDTO(
                entity.getId(),
                entity.getName(),
                entity.getKind(),
                1
        );
    }

}
