package br.com.dio.service.boardcolumn;

import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.entity.BoardColumnEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BoardColumnService {

    BoardColumnEntity insert(BoardColumnEntity entity)throws SQLException;

    boolean exists(Long  id)throws SQLException;

    Optional<BoardColumnInfoDTO> findInfoById(Long id) throws SQLException;

    public List<BoardColumnEntity> findAllColumnsByBoardId(Long boardId) throws SQLException;




}
