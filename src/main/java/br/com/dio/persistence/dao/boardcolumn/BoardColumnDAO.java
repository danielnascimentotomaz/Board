package br.com.dio.persistence.dao.boardcolumn;

import br.com.dio.dto.boardcolumn.BoardColumnDTO;
import br.com.dio.dto.boardcolumn.BoardColumnDetailsDTO;
import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.entity.BoardColumnEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BoardColumnDAO {

    BoardColumnEntity insert(BoardColumnEntity entity)throws SQLException;

    List<BoardColumnEntity>findAllColumnsByBoardId(final  Long id) throws SQLException;

    List<BoardColumnDTO> findByBoardIdWithDetails(final Long  boardId)throws SQLException;

    Optional<BoardColumnDetailsDTO> findById01(final Long id)throws SQLException;

    public Optional<BoardColumnEntity> findById(Long id) throws SQLException;


    boolean exists(Long id) throws SQLException;

    Optional<BoardColumnInfoDTO> findInfoById(Long id) throws SQLException;
}
