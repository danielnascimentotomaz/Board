package br.com.dio.persistence.dao.board;

import br.com.dio.entity.BoardEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BoardDAO {

    BoardEntity insert(final BoardEntity entity) throws SQLException;

    void delete(final Long id) throws SQLException;

    Optional<BoardEntity> findById(final Long id)throws SQLException;

    Optional<BoardEntity> findBoardWithColumnsById(Long boardId) throws SQLException;

    boolean exists(final Long id)throws SQLException;

    List<BoardEntity> findAll()throws  SQLException;


}
