package br.com.dio.persistence.dao.board;

import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;
import br.com.dio.entity.BoardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import com.mysql.cj.jdbc.StatementImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardDAOImpl implements BoardDAO {
    private final Connection connection;

    public BoardDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public BoardEntity insert(BoardEntity entity) throws SQLException {
        String sql = "INSERT INTO BOARDS(name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getName());
            ps.executeUpdate();


            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1)); // ID gerado pelo banco
                }
            }
            return entity;
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM BOARDS WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No record found to delete with id: " + id);
            }
        }
    }

    @Override
    public Optional<BoardEntity> findById(Long id) throws SQLException {
        String sql = "SELECT id, name FROM BOARDS WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BoardEntity board = new BoardEntity();
                board.setId(rs.getLong("id"));
                board.setName(rs.getString("name"));
                return Optional.of(board);
            }
            return Optional.empty();
        }
    }


    @Override
    public Optional<BoardEntity> findBoardWithColumnsById(Long boardId) throws SQLException {
        String sql = """
        SELECT 
            b.id AS board_id, 
            b.name AS board_name,
            c.id AS column_id, 
            c.name AS column_name, 
            c.`order` AS column_order, 
            c.kind AS column_kind
        FROM BOARDS b
        LEFT JOIN BOARDS_COLUMNS c ON b.id = c.boards_id
        WHERE b.id = ?
    """;

    /*
    // A consulta SQL usa LEFT JOIN para trazer o board e suas colunas em uma única query.
    // Exemplo de ResultSet gerado para board_id = 1:
    //
    // board_id | board_name | column_id | column_name | column_order | column_kind
    // 1        | Board A    | 1         | To Do       | 1            | INITIAL
    // 1        | Board A    | 2         | Doing       | 2            | PENDING
    // 1        | Board A    | NULL      | NULL        | NULL         | NULL
    //
    // Observação:
    // Cada coluna do board gera uma linha no ResultSet, repetindo os dados do board.
    // No código Java, o BoardEntity deve ser criado apenas uma vez, e as colunas
    // devem ser adicionadas à lista de BoardColumnEntity para montar corretamente a entidade completa.

    */


        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);

            try (ResultSet rs = ps.executeQuery()) {
                BoardEntity board = null;
                List<BoardColumnEntity> columns = new ArrayList<>();

                while (rs.next()) { // Há proxima linha
                    if (board == null) {// Cria board apenas na primeira linha
                        board = new BoardEntity();
                        board.setId(rs.getLong("board_id"));
                        board.setName(rs.getString("board_name"));
                    }

                    Long columnId = rs.getLong("column_id");
                    if (!rs.wasNull()) { // Se column_id não for NULL, cria o BoardColumnEntity
                        BoardColumnEntity column = new BoardColumnEntity();
                        column.setId(columnId);
                        column.setName(rs.getString("column_name"));
                        column.setOrder(rs.getInt("column_order"));
                        column.setKind(BoardColumnKindEnum.valueOf(rs.getString("column_kind")));
                        columns.add(column);
                    }
                }

                if (board != null) {
                    board.setBoardColumns(columns);
                    return Optional.of(board);
                }

                return Optional.empty();
            }
        }
    }

    @Override
    public boolean exists(Long id) throws SQLException {
        String sql = "SELECT 1 FROM BOARDS WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
    }

    @Override
    public List<BoardEntity> findAll() throws SQLException {
        return List.of(); // implementar depois
    }
}
