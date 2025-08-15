package br.com.dio.persistence.dao.boardcolumn;

import br.com.dio.entity.BoardColumnEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BoardColumnDAO {

    BoardColumnEntity insert(BoardColumnEntity entity)throws SQLException;

    List<BoardColumnEntity>findAllColumnsByBoardId(final  Long id) throws SQLException;
}
