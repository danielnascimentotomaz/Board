package br.com.dio.dto.boardcolumn;

import br.com.dio.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id ,
                                 int order,
                                 BoardColumnKindEnum kind,
                                 Long boardId

                                    ) {
}
