package br.com.dio.ui;

import br.com.dio.entity.BoardEntity;


import java.util.Scanner;

public class BoardMenu {
    Scanner scanner = new Scanner(System.in);
    private final BoardEntity boardEntity;
    public BoardMenu(BoardEntity boardEntity) {
        this.boardEntity = boardEntity;
    }


    public void execute() {
        System.out.printf("Bem vindo ao Board %s, selecione a operação desejada",boardEntity.getId());
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
    }

    private void moveCardToNextColumn() {
    }

    private void blockCard() {
    }

    private void UnblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() {
    }

    private void showColumn() {
    }

    private void showCard() {
    }
}
