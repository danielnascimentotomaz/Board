package br.com.dio.persistence.dao.block;


import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.dio.utils.OffSetDateTimeConverter.toTimestamp;


/**
 * Implementação de {@link BlockDAO} usando JDBC para registrar bloqueios de Cards.
 */
public class BlockDAOImpl implements BlockDAO {
    private final Connection connection;



    /**
     * Construtor que recebe a conexão JDBC.
     *
     * @param connection Conexão com o banco de dados.
     */
    public BlockDAOImpl(Connection connection) {
        this.connection = connection;
    }



    @Override
    public boolean block(Long cardId, String reason) throws SQLException {
        String sql = "INSERT INTO BLOCKS (card_id, blocked_at, block_reason) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setTimestamp(2, toTimestamp(OffsetDateTime.now()));
            ps.setString(3, reason);

            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public boolean unblock(final String reason,final Long cardId) throws SQLException {
        String sql = """
                        UPDATE BLOCKS
                        SET
                            unblocked_at = ?,
                            unblock_reason = ?
                        WHERE card_id = ?
                          AND unblock_reason IS NULL
                       """;


        try(PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setTimestamp(1,toTimestamp(OffsetDateTime.now()));
            ps.setString(2,reason);
            ps.setLong(3,cardId);


            int row = ps.executeUpdate();
            return row > 0;

        }

    }


}
