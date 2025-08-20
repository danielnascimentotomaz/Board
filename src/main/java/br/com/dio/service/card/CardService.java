package br.com.dio.service.card;


import br.com.dio.entity.CardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;

import java.sql.SQLException;

public interface CardService {



    CardEntity insert(CardEntity entity)throws SQLException;
}
