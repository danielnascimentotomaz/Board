package br.com.dio.service.boardcolumn;

import br.com.dio.entity.BoardColumnEntity;

import java.sql.SQLException;

public interface BoardColumnService {

    BoardColumnEntity insert(BoardColumnEntity entity)throws SQLException;

    boolean exists(Long  id)throws SQLException;


}
