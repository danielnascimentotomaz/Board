package br.com.dio.ui;

import br.com.dio.entity.BoardColumnEntity;
import br.com.dio.entity.BoardColumnKindEnum;
import br.com.dio.entity.BoardEntity;
import br.com.dio.persistence.config.ConnectionStrategy;
import br.com.dio.persistence.config.MySQLConnection;
import br.com.dio.service.board.BoardQueryService;
import br.com.dio.service.board.BoardService;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static br.com.dio.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.entity.BoardColumnKindEnum.FINAL;
import static br.com.dio.entity.BoardColumnKindEnum.INITIAL;
import static br.com.dio.entity.BoardColumnKindEnum.PENDING;

public class MainMenu  {
    private Scanner scanner = new Scanner(System.in);
    private ConnectionStrategy connectionStrategy;

    public MainMenu(ConnectionStrategy connectionStrategy) {
        // Singleton do pool é usado durante toda a execução
        this.connectionStrategy = connectionStrategy;
    }

    public  void  execute() throws SQLException {
        while (true) {
            System.out.println("\n=====================================");
            System.out.println("        [ GERENCIADOR DE BOARDS ]    ");
            System.out.println("=====================================");
            System.out.println(" [1] Criar um novo Board");
            System.out.println(" [2] Selecionar um board existente");
            System.out.println(" [3] Excluir um board");
            System.out.println(" [4] Sair");
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
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> {
                    System.out.println(" Saindo do sistema. Ate logo!");
                    // fecha pool só na finalização do programa
                    MySQLConnection.getInstance().close();
                    System.exit(0);
                }
                default -> System.out.println(" [!] Opcao invalida, informe uma opcao do menu!");
            }
        }

    }

    private void createBoard() throws SQLException{
        var entity = new BoardEntity();
        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome do seu board");
        String name = scanner.nextLine();
        entity.setName(name);

        System.out.println("Seu board tera colunas alem das 3 padroes? Se sim informe quantas, senao digite '0'");
        var addittionalCollumns = scanner.nextInt();
        scanner.nextLine(); // <-- consome a quebra de linha



        System.out.println("Informe o nome da coluna inicial do board");
        String initialColumnsName = scanner.nextLine();

        BoardColumnEntity initialColumn = createCollumns(initialColumnsName,INITIAL,0);
        columns.add(initialColumn);



       for (int i = 0 ; i < addittionalCollumns; i++){
           System.out.println("Informe o nome da coluna de tarefa pendente");
           String pendingColumnsName = scanner.nextLine();

           BoardColumnEntity pendingColumn = createCollumns(pendingColumnsName,PENDING,i + 1);
           columns.add(pendingColumn);

       }

        System.out.println("Informe o nome da coluna final do board");
        String finalColumnsName = scanner.nextLine();

        BoardColumnEntity finalColumn = createCollumns(finalColumnsName,FINAL,addittionalCollumns + 1);
        columns.add(finalColumn);



        System.out.println("Informe o nome da coluna cancelamento do board");
        String cancelColumnsName = scanner.nextLine();

        BoardColumnEntity cancelColumn = createCollumns(cancelColumnsName,CANCEL,addittionalCollumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);


        try {
           BoardService boardService = new BoardService(connectionStrategy);
           boardService.insert(entity);

        }catch (SQLException e) {
            System.out.println("Erro ao criar o board: " + e.getMessage());
            e.printStackTrace();
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selectBoard() throws SQLException {
        System.out.println("informe o id do board que deseja selecionar");
        var id  = scanner.nextLong();
        scanner.nextLine();// <-- consome a quebra de linha

        try{

            BoardQueryService boardQueryService = new BoardQueryService(connectionStrategy);

            Optional<BoardEntity> optional = boardQueryService.findById(id);

            optional.ifPresentOrElse(
                  b  -> new BoardMenu(b,connectionStrategy).execute(),
                    () -> System.out.printf("Nao foi encontrado um board com id %s\n", id)

            );

        }catch (SQLException e) {
            System.out.println("Erro ao consultar o board: " + e.getMessage());
            e.printStackTrace();
        }
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board que sera excluido");
        var id = scanner.nextLong();
        scanner.nextLine(); // <-- consome a quebra de linha
        try {
            BoardService boardService = new BoardService(connectionStrategy);

            if (boardService.delete(id)){
                System.out.printf("O Board %s foi excluido\n", id);
            }else {
                System.out.printf("Não foi encontrado um board com id %s\n", id);
            }

        }catch (SQLException e) {
            System.out.println("Erro ao deletar o board: " + e.getMessage());
            e.printStackTrace();
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private BoardColumnEntity createCollumns(final String name, final BoardColumnKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();

        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);

        return boardColumn;



    }





}
