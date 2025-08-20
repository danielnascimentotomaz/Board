package br.com.dio.service.board;

import br.com.dio.dto.BoardColumnDTO;
import br.com.dio.dto.BoardDetailsDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.board.BoardDAO;
import br.com.dio.persistence.dao.board.BoardDAOImpl;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAO;
import br.com.dio.persistence.dao.boardcolumn.BoardColumnDAOImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {

    private final ConnectionStrategy connectionStrategy;


    public Optional<BoardEntity> findById(final Long id)throws SQLException{


        try (
                Connection connection = connectionStrategy.getConnection();

                ){
            BoardDAO boardDAO = new BoardDAOImpl(connection);
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);

            Optional<BoardEntity>  boardEntity = boardDAO.findById(id).map(board -> {

                try {
                    // Busca as colunas relacionadas ao board
                    List<BoardColumnEntity> boardColumns = boardColumnDAO.findAllColumnsByBoardId(id);

                    board.setBoardColumns(boardColumns);

                    return board;
                } catch (SQLException e) {

                    throw new RuntimeException("Erro ao buscar colunas do Board", e);
                }
            });
            return boardEntity;
        }
    }

    public Optional<BoardDetailsDTO> showBoardDetails(final Long id){

        try(
                Connection connection = connectionStrategy.getConnection();
                ) {

            BoardDAO boardDAO = new BoardDAOImpl(connection);
            BoardColumnDAO boardColumnDAO = new BoardColumnDAOImpl(connection);

            Optional<BoardEntity> optional = boardDAO.findById(id);

            if (optional.isPresent()){
                BoardEntity boardEntity = optional.get();
                List<BoardColumnDTO> boardColumnDTOS = boardColumnDAO.findByBoardIdWithDetails(boardEntity.getId());
                BoardDetailsDTO boardDetailDTO = new BoardDetailsDTO( boardEntity.getId(),
                                                                    boardEntity.getName(),
                                                                    boardColumnDTOS);

                return Optional.of(boardDetailDTO); // ✅ dentro do if

            }

            return Optional.empty(); // ✅ se não encontrar

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar colunas do Board", e);
        }


    }






}
