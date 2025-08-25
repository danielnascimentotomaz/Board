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

public class CardDAOImpl implements CardDAO{
    private final Connection connection;

    public CardDAOImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public CardEntity insert(CardEntity entity) throws SQLException {
        String sql = "INSERT INTO CARDS (title,description,boards_columns_id) VALUES (?,?,?)";

        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,entity.getTitle());
            ps.setString(2,entity.getDescription());
            ps.setLong(3,entity.getBoardColumn().getId());

            ps.executeUpdate();

           try(ResultSet rs = ps.getGeneratedKeys()) {
               if (rs.next()){
                   entity.setId(rs.getLong(1)); // ID gerado pelo banco
               }

           }

           return  entity;


        }

    }

    @Override
    public List<CardEntity> findAll() throws SQLException {
        return List.of();
    }

    @Override
    public Optional<CardDetailsDTO> findById(Long cardId) throws SQLException {
        String sql =  """
                        SELECT
                            c.id,
                            c.title,
                            c.description,
                            b.blocked_at,
                            b.block_reason,
                            (
                                SELECT COUNT(sub_b.id)
                                FROM BLOCKS sub_b
                                WHERE sub_b.card_id = c.id
                            ) AS blocks_amount,
                            c.boards_columns_id,
                            bc.name AS name_column_board
                        FROM CARDS c
                        LEFT JOIN BLOCKS b
                               ON c.id = b.card_id
                              AND b.unblocked_at IS NULL   /* somente bloqueios ativos */
                        INNER JOIN BOARDS_COLUMNS bc
                               ON bc.id = c.boards_columns_id
                        WHERE c.id = ?;
                        """;
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
           ps.setLong(1,cardId);

           try(ResultSet rs = ps.executeQuery()) {

               if (rs.next()) {
                   // bloqueado se houver registro ativo em BLOCKS (blocked_at não nulo)
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
                           rs.getString("name_column_board"));
                   return Optional.of(cardDetailsDTO);
               }
           }
        }

        return Optional.empty();
    }


    @Override
    public boolean update(CardEntity card) throws SQLException {
        return false;
    }

    @Override
    public boolean updateStatus(Long cardId) throws SQLException {
        return false;
    }

    @Override
    public boolean moveToColumn(Long cardId, Long columnId) throws SQLException {
        return false;
    }
}
