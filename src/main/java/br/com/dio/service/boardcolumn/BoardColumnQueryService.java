package br.com.dio.service.boardcolumn;


import br.com.dio.dto.boardcolumn.BoardColumnDetailsDTO;
import br.com.dio.dto.card.CardDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;


public class BoardColumnQueryService {

    private final ConnectionStrategy connectionStrategy;

    public BoardColumnQueryService(ConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

    public Optional<BoardColumnDetailsDTO> findBYId(final Long id) throws SQLException {

        try(Connection connection = connectionStrategy.getConnection()) {

            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);

            try {
                Optional<BoardColumnEntity> boardColumnEntity = boardColumnDAO.findById(id);

                if (boardColumnEntity.isPresent()) {

                    return boardColumnEntity.
                            map(entity -> new BoardColumnDetailsDTO(
                                            entity.getName(),
                                            entity.getKind(),
                                            entity.getCardEntityList().stream().
                                                    map(cardEntity ->
                                                            new CardDTO(
                                                                    cardEntity.getId(),
                                                                    cardEntity.getTitle(),
                                                                    cardEntity.getDescription())
                                                    ).toList()
                                    )
                            );
                }else{
                    return Optional.empty();
                }

            }catch (SQLException e){
                throw new RuntimeException("Erro ao buscar BoardColumns por id", e);
            }
        }

    }



}
