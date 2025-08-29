package br.com.dio.ui;

import br.com.dio.dto.boardcolumn.BoardColumnDetailsDTO;
import br.com.dio.dto.boardcolumn.BoardColumnInfoDTO;
import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;
import br.com.dio.entity.BoardEntity;
import br.com.dio.entity.CardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.dao.block.BlockDAOImpl;
import br.com.dio.persistence.dao.board.BoardDAO;
import br.com.dio.persistence.dao.board.BoardDAOImpl;
import br.com.dio.persistence.dao.card.CardDAO;
import br.com.dio.persistence.dao.card.CardDAOImpl;
import br.com.dio.service.block.BlockService;
import br.com.dio.service.block.BlockServiceImpl;
import br.com.dio.service.board.BoardQueryService;
import br.com.dio.service.boardcolumn.BoardColumnQueryService;
import br.com.dio.service.boardcolumn.BoardColumnService;
import br.com.dio.service.boardcolumn.BoardColumnServiceImpl;
import br.com.dio.service.card.CardQueryService;
import br.com.dio.service.card.CardService;
import br.com.dio.service.card.CardServiceImpl;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static br.com.dio.entity.BoardColumnKindEnum.INITIAL;

public class BoardMenu {
    Scanner scanner = new Scanner(System.in);
    private final BoardEntity boardEntity;
    private final ConnectionStrategy connectionStrategy;
    private final BoardColumnService boardColumnService;
    private final BlockService blockService;
    private final CardDAO cardDAO;


    public BoardMenu(BoardEntity boardEntity, ConnectionStrategy connectionStrategy) throws SQLException {
        this.boardEntity = boardEntity;
        this.connectionStrategy = connectionStrategy;
        this.cardDAO = new CardDAOImpl(connectionStrategy.getConnection());

        this.blockService = new BlockServiceImpl(connectionStrategy,new BlockDAOImpl(connectionStrategy.getConnection()));

        this.boardColumnService = new BoardColumnServiceImpl(this.connectionStrategy);


    }


    public void execute() {
        System.out.printf("Bem vindo ao Board %s, selecione a operação desejada\n",boardEntity.getId());
        boolean running = true;
        while (running) {
            System.out.println("\n=====================================");
            System.out.println("        [ GERENCIADOR DE BOARDS ]    ");
            System.out.println("=====================================");
            System.out.println(" [1] Criar Um Card");
            System.out.println(" [2] Mover Um Card");
            System.out.println(" [3] Bloquear Um Card");
            System.out.println(" [4] Desbloquear Um Card");
            System.out.println(" [5] Cancelar Um Card");
            System.out.println(" [6] Visualizar Board");
            System.out.println(" [7] Visualizar Colunas Com Card ");
            System.out.println(" [8] Visualizar Card");
            System.out.println(" [9] Voltar Para O Menu Anterior");
            System.out.println(" [10] Sair");
            System.out.println("=====================================");
            System.out.print(" >> Escolha uma opcao: ");

            String input = scanner.nextLine();
            int option;

            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" [!] Entrada invalida, digite apenas numeros!");
                continue;
            }

            switch (option) {
                case 1 -> createCard();
                case 2 -> moveCardToNextColumn();
                case 3 -> blockCard();
                case 4 -> UnblockCard();
                case 5 -> cancelCard();
                case 6 -> showBoard();
                case 7 -> showColumn();
                case 8 -> showCard();
                case 9 ->  {
                    System.out.println("Voltando para o menu anterior");
                    running = false;
                }
                case 10 ->{
                    System.out.println(" Saindo do sistema. Ate logo!");

                    System.exit(0);
                }
                default -> System.out.println(" [!] Opcao invalida, informe uma opcao do menu!");
            }
        }

    }

    private void createCard() {
        CardEntity card =new CardEntity();
        System.out.println("Informe o titulo do Card");
        String title = scanner.nextLine();
        card.setTitle(title);

        System.out.println("Informe a descricao do Card");
        String description = scanner.nextLine();
        card.setDescription(description);

        // Regra de negócio diz que card tem comecar na coluna inicial
        var initialColumn = boardEntity.getInitialColumn();


        card.setBoardColumn(initialColumn);


        try {
            CardService cardService = new CardServiceImpl(connectionStrategy,boardColumnService,cardDAO,blockService);
            cardService.insert(card);
            System.out.printf("Card criado %s - %s: %d%n", card.getTitle(), card.getDescription(), card.getId());
        } catch (SQLException e) {
            System.out.println("Erro ao criar o card" + e.getMessage());
            e.printStackTrace();
        }


    }

    // Regra de negócio: O card  só pode ser movido pra proxima coluna
    private void moveCardToNextColumn() {
        System.out.println("Informe o id Card que deseja mover para proxima coluna");
        Long cardId = scanner.nextLong();
        scanner.nextLine();
        /*List<BoardColumnInfoDTO> boardColumnsInfo =  boardEntity.getBoardColumns().stream().
                map(bc -> new BoardColumnInfoDTO(
                        bc.getId(),
                        bc.getOrder(),
                        bc.getKind(),
                        bc.getBoard().getId()

                )).toList();*/

        try {

            //new CardServiceImpl(connectionStrategy,boardColumnService).moveToNextColumn(cardId,boardColumnsInfo);
            new CardServiceImpl(connectionStrategy,boardColumnService,cardDAO,blockService).moveToNextColumn(cardId);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    private void blockCard() {
        System.out.println("Informe o id do card que sera bloqueado:");
        var cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.nextLine();
        try {
            CardService cardService = new CardServiceImpl(connectionStrategy,boardColumnService,cardDAO,blockService);
            cardService.block(cardId,reason);
        } catch (Exception e) {
           System.out.println(e);
        }
    }

    private void UnblockCard() {
    }

    private void cancelCard() {
        System.out.println("Informe o id Card que deseja Cancelar");
        Long cardId = scanner.nextLong();
        scanner.nextLine();
        try {
            CardService cardService = new CardServiceImpl(connectionStrategy,boardColumnService,cardDAO,blockService);
            cardService.cancelCard(cardId);
        } catch (Exception e) {
           System.out.println(e);
        }

    }

    private void showBoard() {
        try {
            BoardQueryService boardQueryService = new BoardQueryService(connectionStrategy);

            var optional = boardQueryService.showBoardDetails(boardEntity.getId());

            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columnDTOS().forEach(c -> {

                    System.out.printf("Coluna [%s] tipo [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());

                });

            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void showColumn() {


        List<Long> columnsIdsList = boardEntity.getBoardColumns().stream()
                .map(entity -> entity.getId())
                .toList();

        var selectecdColumn = -1L;
        while (!columnsIdsList.contains(selectecdColumn)){ //Enquanto não ecolher uma Id da coluna
            System.out.printf("Escolha uma coluna do board %s\n", boardEntity.getId());
            boardEntity.getBoardColumns().forEach(c ->
                    System.out.printf("%s - %s [%s]\n",c.getId(),c.getName(),c.getKind()));
            selectecdColumn = scanner.nextLong();
            scanner.nextLine();
        }

        BoardColumnQueryService boardColumnQueryService = new BoardColumnQueryService(connectionStrategy);

        Optional<BoardColumnDetailsDTO> column = boardColumnQueryService.findBYId(selectecdColumn);


        column.ifPresent(columns -> {
            System.out.printf("Nome da Coluna: %s Tipo: %s\n\n", columns.name(), columns.kind());

            columns.cards().forEach(card -> System.out.printf(
                    "Card %s - %s\nDescricao: %s\n\n",
                    card.id(),
                    card.title(),
                    card.description()
            ));
        }


        );


    }

    private void showCard() {
        System.out.println("Informe o ID do card que deseja visualizar");
        var selectedCarId = scanner.nextLong();
        scanner.nextLine();
        try {
            CardQueryService cardQueryService = new CardQueryService(connectionStrategy);

            cardQueryService.findById(selectedCarId).ifPresentOrElse(
                          c -> {
                              System.out.printf("Card %s - %s.\n",c.id(),c.title());
                              System.out.printf("Descricao: %s\n",c.description());
                              System.out.println(c.blocked() ? "Esta bloqueado. Motivo: " + c.blockReason() :
                                      "Não está bloqueado");
                              System.out.printf("Ja foi bloqueado %s vezes\n", c.blocksAmount());
                              System.out.printf("Esta no momento na coluna %s - %s\n", c.columnId(),c.columnName());


                          },
                                      () -> System.out.printf("Nao existe um card co id %s\n",selectedCarId)
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
