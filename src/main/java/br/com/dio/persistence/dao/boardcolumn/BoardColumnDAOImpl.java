package br.com.dio.persistence.dao.boardcolumn;

import br.com.dio.dto.BoardColumnDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

    @Override
    public List<BoardColumnDTO> findByBoardIdWithDetails(Long  boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        String sql ="""
                    SELECT bc.id,
                           bc.name,
                           bc.kind,
                           (
                             SELECT COUNT(*)
                             FROM CARDS c
                             WHERE c.boards_columns_id = bc.id
                           ) AS Cards_amount
                    FROM BOARDS_COLUMNS bc
                    WHERE bc.boards_id = ?
                    ORDER BY bc.`order`
                    """;
        /* String sql_1 = """
                SELECT bc.id,
                       bc.name,
                       bc.kind,
                       COUNT(c.id) AS Cards_amount
                FROM BOARDS_COLUMNS bc
                LEFT JOIN CARDS c
                       ON c.boards_columns_id = bc.id
                WHERE bc.boards_id = ?
                GROUP BY bc.id, bc.name, bc.kind
                ORDER BY bc.`order`;
                """;
        */


        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {

                    BoardColumnDTO dto = new BoardColumnDTO(
                            resultSet.getLong("bc.id"),
                            resultSet.getString("bc.name"),
                            findByName(resultSet.getString("bc.kind")),
                            resultSet.getInt("Cards_amount")
                    );

                    dtos.add(dto);
                }


            }
        }
        return dtos;


    }
}



