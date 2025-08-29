package br.com.dio.persistence.dao.card;

import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.entity.CardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;


import static br.com.dio.utils.OffSetDateTimeConverter.toOffsetDateTime;

/**
 * Implementação JDBC de CardDAO.
 *
 * Recebe a conexão via construtor e executa operações SQL.
 * A gestão de transações deve ser feita pelo service.
 */
public class CardDAOImpl implements CardDAO {

    private final Connection connection;

    public CardDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CardEntity insert(CardEntity entity) throws SQLException {
        String sql = "INSERT INTO CARDS (title, description, boards_columns_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getTitle());
            ps.setString(2, entity.getDescription());
            ps.setLong(3, entity.getBoardColumn().getId());

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
    public Optional<CardDetailsDTO> findById(Long cardId) throws SQLException {
        String sql = """
                SELECT c.id, c.title, c.description,
                       b.blocked_at, b.block_reason,
                       (SELECT COUNT(sub_b.id) FROM BLOCKS sub_b WHERE sub_b.card_id = c.id) AS blocks_amount,
                       c.boards_columns_id, bc.name AS name_column_board, bc.boards_id AS board_id
                  FROM CARDS c
                  LEFT JOIN BLOCKS b ON c.id = b.card_id AND b.unblocked_at IS NULL
                  INNER JOIN BOARDS_COLUMNS bc ON bc.id = c.boards_columns_id
                 WHERE c.id = ?;
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean blocked = rs.getTimestamp("blocked_at") != null;

                    CardDetailsDTO cardDetailsDTO = new CardDetailsDTO(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            blocked,
                            toOffsetDateTime(rs.getTimestamp("blocked_at")),
                            rs.getString("block_reason"),
                            rs.getInt("blocks_amount"),
                            rs.getLong("boards_columns_id"),
                            rs.getString("name_column_board"),
                            rs.getLong("board_id")
                    );
                    return Optional.of(cardDetailsDTO);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean exists(Long id) throws SQLException {
        String sql = "SELECT 1 FROM CARDS WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean moveToColumn(Long columnId, Long cardId) throws SQLException {
        String sql = "UPDATE CARDS SET boards_columns_id = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            ps.setLong(2, cardId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(CardEntity card) throws SQLException {
        String sql = "UPDATE CARDS SET title = ?, description = ?, boards_columns_id = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, card.getTitle());
            ps.setString(2, card.getDescription());
            ps.setLong(3, card.getBoardColumn().getId());
            ps.setLong(4, card.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long cardId) throws SQLException {
        String sql = "DELETE FROM CARDS WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            return ps.executeUpdate() > 0;
        }
    }
}