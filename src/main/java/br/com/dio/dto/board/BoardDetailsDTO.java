package br.com.dio.dto.board;

import br.com.dio.dto.boardcolumn.BoardColumnDTO;

import java.util.List;

public record BoardDetailsDTO(
        Long id,
        String name,
        List<BoardColumnDTO> columnDTOS


) {
}
