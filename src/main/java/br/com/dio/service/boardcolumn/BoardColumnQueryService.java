package br.com.dio.service.boardcolumn;


import br.com.dio.dto.boardcolumn.BoardColumnDetailsDTO;
import br.com.dio.dto.card.CardDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.exception.BoardColumnAccessException;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j // 📝 Lombok gera automaticamente um logger estático e final chamado `log`
public class BoardColumnQueryService {

    private final ConnectionStrategy connectionStrategy;

    public BoardColumnQueryService(ConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

    public Optional<BoardColumnDetailsDTO> findBYId(final Long id){

        try(Connection connection = connectionStrategy.getConnection()) {

            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);


            Optional<BoardColumnEntity> boardColumnEntity = boardColumnDAO.findById(id);

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
                                                ).toList())
                        );
        }catch (SQLException e){
            var menssage = "Falha ao acessar o banco para buscar coluna: id=";

            log.error(menssage+ id, e);

            throw new BoardColumnAccessException(menssage+ id, e);
        }
    }

}
