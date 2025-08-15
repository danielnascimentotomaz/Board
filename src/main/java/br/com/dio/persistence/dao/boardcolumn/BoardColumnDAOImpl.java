package br.com.dio.persistence.dao.boardcolumn;

import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.dio.entity.BoardColumnKindEnum.findByName;


public class BoardColumnDAOImpl implements BoardColumnDAO {
    private final Connection connection;



    public BoardColumnDAOImpl(Connection connection) {
        this.connection = connection;


    }

    @Override
    public BoardColumnEntity insert(BoardColumnEntity entity) throws SQLException {
        String sql = "INSERT INTO BOARDS_COLUMNS(name, `order`, kind, boards_id) VALUES (?,?,?,?)";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getOrder());
            ps.setString(3, entity.getKind().name());
            ps.setLong(4, entity.getBoard().getId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        }
    }

    @Override
    public List<BoardColumnEntity> findAllColumnsByBoardId(Long boardId) throws SQLException {
        List<BoardColumnEntity> boardColumnEntityList = new ArrayList<>();
        String sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE boards_id = ? ORDER BY `order` ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {

                    BoardColumnEntity boardColumn = new BoardColumnEntity();
                    boardColumn.setId(resultSet.getLong("id"));
                    boardColumn.setName(resultSet.getString("name"));
                    boardColumn.setOrder(resultSet.getInt("order"));
                   // boardColumn.setKind(BoardColumnKindEnum.valueOf(resultSet.getString("kind")));
                    boardColumn.setKind(findByName(resultSet.getString("kind")));

                    boardColumnEntityList.add(boardColumn);
                }
            }
        }

        return boardColumnEntityList;
    }


}
