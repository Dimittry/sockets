package SocketTuts.TicTacToe.server;

import SocketTuts.TicTacToe.controllers.GameSimple;

import java.io.IOException;
import java.net.ServerSocket;

public class TicTacToeServer {

    private static final int DEFAULT_PORT = 9899;

    public static void main(String[] args) {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(DEFAULT_PORT);
            System.out.println("Tic Tac Toe Server is Running");

            while (true) {
                GameSimple game = new GameSimple();
                GameSimple.Player playerX = game.new Player(listener.accept(), 'X');
                GameSimple.Player playerO = game.new Player(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.setCurrentPlayer(playerX);
                playerX.start();
                playerO.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(listener != null) {
                try {
                    listener.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
