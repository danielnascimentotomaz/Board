package br.com.dio.persistence.dao.boardcolumn;

import br.com.dio.dto.boardcolumn.BoardColumnDTO;
import br.com.dio.dto.boardcolumn.BoardColumnDetailsDTO;
import br.com.dio.dto.card.CardDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;
import br.com.dio.entity.CardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.dio.entity.BoardColumnKindEnum.findByName;


public class BoardColumnDAOImpl implements BoardColumnDAO {
    private final Connection connection;



    public BoardColumnDAOImpl(Connection connection) {
        this.connection = connection;


    }

    @Override
    public BoardColumnEntity insert(BoardColumnEntity entity) throws SQLException {
        String sql = "INSERT INTO BOARDS_COLUMNS(name, `order`, kind, boards_id) VALUES (?,?,?,?)";

        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getOrder());
            ps.setString(3, entity.getKind().name());
            ps.setLong(4, entity.getBoard().getId());

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
    public List<BoardColumnEntity> findAllColumnsByBoardId(Long boardId) throws SQLException {
        List<BoardColumnEntity> boardColumnEntityList = new ArrayList<>();
        String sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE boards_id = ? ORDER BY `order` ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {

                    BoardColumnEntity boardColumn = new BoardColumnEntity();
                    boardColumn.setId(resultSet.getLong("id"));
                    boardColumn.setName(resultSet.getString("name"));
                    boardColumn.setOrder(resultSet.getInt("order"));
                   // boardColumn.setKind(BoardColumnKindEnum.valueOf(resultSet.getString("kind")));
                    boardColumn.setKind(findByName(resultSet.getString("kind")));

                    boardColumnEntityList.add(boardColumn);
                }
            }
        }

        return boardColumnEntityList;
    }

    @Override
    public List<BoardColumnDTO> findByBoardIdWithDetails(Long  boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        String sql ="""
                    SELECT bc.id,
                           bc.name,
                           bc.kind,
                           (
                             SELECT COUNT(*)
                             FROM CARDS c
                             WHERE c.boards_columns_id = bc.id
                           ) AS Cards_amount
                    FROM BOARDS_COLUMNS bc
                    WHERE bc.boards_id = ?
                    ORDER BY bc.`order`
                    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {

                    BoardColumnDTO dto = new BoardColumnDTO(
                            resultSet.getLong("bc.id"),
                            resultSet.getString("bc.name"),
                            findByName(resultSet.getString("bc.kind")),
                            resultSet.getInt("Cards_amount")
                    );

                    dtos.add(dto);
                }


            }
        }
        return dtos;


    }


    // Fazendo findById Usando a Entidade
    @Override
    public Optional<BoardColumnEntity> findById(Long id) throws SQLException {
        String sql = """
                 SELECT bc.id AS board_column_id,
                        bc.name,
                        bc.kind,
                        c.id as card_id,
                        c.title,
                        c.description
                 FROM BOARDS_COLUMNS bc
                 LEFT JOIN CARDS c ON c.boards_columns_id = bc.id
                 WHERE bc.id = ?
                 """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                BoardColumnEntity column = null;
                List<CardEntity> cards = new ArrayList<>();

                while (rs.next()) {
                    if (column == null) {
                        // System.out.println("Linha encontrada: id=" + rs.getLong("board_column_id"));

                        column = new BoardColumnEntity(
                                rs.getLong("board_column_id"),
                                rs.getString("name"),
                                BoardColumnKindEnum.valueOf(rs.getString("kind"))
                        );
                    }

                    // Usei IF porque tou usando LEFT JOIN
                    if (rs.getLong("card_id") != 0) {
                        cards.add(new CardEntity(
                                rs.getLong("card_id"),
                                rs.getString("title"),
                                rs.getString("description")
                        ));
                    }
                }

                if (column != null) {
                    column.setCardEntityList(cards);
                    return Optional.of(column);
                }
                return Optional.empty();
            }
        }
    }


    // Fazendo findById Usando a DTO
    @Override
    public Optional<BoardColumnDetailsDTO> findById01(Long Id) throws SQLException {
        String sql = """
                     SELECT bc.name,
                            bc.kind,
                            c.id,
                            c.title,
                            c.description
                          FROM  BOARDS_COLUMNS bc
                          INNER JOIN CARDS c
                          ON  c.boards_columns_id = bc.id
                          WHERE  bc.id = ?
                      """;

        try(PreparedStatement ps = connection.prepareStatement(sql) ) {
            ps.setLong(1,Id);

            try(ResultSet resultSet = ps.executeQuery()) {
                String columnName = null;
                BoardColumnKindEnum kind = null;
                List<CardDTO> cards = new ArrayList<>();
        /*
            id      Coluna     Kind       BoardId   CardId   Título                Descrição
            41      Inicial    INITIAL    17        3        Implementar Login     Criar funcionalidade de login
            41      Inicial    INITIAL    17        5        Implementar Tela      Criar Funcionalidade Tela
        */

                while (resultSet.next()){ //Há Proxima linha

                    if (columnName == null){
                    columnName = resultSet.getString("bc.name");
                    kind = findByName(resultSet.getString(" bc.kind"));
                    }

                    CardDTO cardDTO = new CardDTO(
                            resultSet.getLong("c.id"),
                            resultSet.getString("c.title"),
                            resultSet.getString("c.description")
                    );

                    cards.add(cardDTO);
                }

                if (columnName != null){
                    BoardColumnDetailsDTO boardColumnDetailsDTO = new BoardColumnDetailsDTO(columnName,kind,cards);

                    return  Optional.of(boardColumnDetailsDTO);
                }else{
                    return Optional.empty();
                }




            }

        }
    }


}



