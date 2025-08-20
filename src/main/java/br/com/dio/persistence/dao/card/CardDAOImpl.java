package br.com.dio.persistence.dao.card;

import br.com.dio.entity.CardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

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
    public Optional<CardEntity> findById(Long cardId) throws SQLException {
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
