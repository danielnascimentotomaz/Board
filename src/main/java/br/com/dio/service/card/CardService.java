package br.com.dio.service.card;


import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.entity.CardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;

import java.sql.SQLException;
import java.util.List;

public interface CardService {



    CardEntity insert(CardEntity entity)throws SQLException;

    void moveToNextColumn(final long cardId, final List<BoardColumnInfoDTO> boardColumnInfoDTOS) throws Exception;

}
