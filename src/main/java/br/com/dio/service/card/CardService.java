package br.com.dio.service.card;


import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.dto.card.CardDetailsDTO;
import br.com.dio.entity.CardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;

import java.sql.SQLException;
import java.util.List;

public interface CardService {



    CardEntity insert(CardEntity entity) throws SQLException;
    CardDetailsDTO findById(long cardId) throws SQLException;
    boolean exists(Long  id)throws SQLException;

    //void moveToNextColumn(final long cardId, final List<BoardColumnInfoDTO> boardColumnInfoDTOS) throws Exception;
    void moveToNextColumn(long cardId) throws Exception;
    void cancelCard(long cardId) throws Exception;
    boolean block(final long id,final String reason)throws Exception;

}
