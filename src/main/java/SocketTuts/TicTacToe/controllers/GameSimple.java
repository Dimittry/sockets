package SocketTuts.TicTacToe.controllers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameSimple {

    private Player[] board = {
            null, null, null,
            null, null, null,
            null, null, null};

    private Player currentPlayer;

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean hasWinner() {
        return
                (board[0] != null && board[0] == board[1] && board[0] == board[2])
                        ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
                        ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
                        ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
                        ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
                        ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
                        ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
                        ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    public boolean boardFilledUp() {
        for(int i=0; i < board.length; i++) {
            if(board[i] != null) {
                return  false;
            }
        }
        return true;
    }

    public synchronized boolean legalMove(int location, Player player) {
        if(player == currentPlayer && board[location] == null) {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }

    public class Player extends Thread {
        private final char mark;
        private final Socket socket;
        private Player opponent;
        private BufferedReader input;
        private PrintWriter output;

        public Player(final Socket socket,
                      final char mark) {
            this.socket = socket;
            this.mark = mark;
            try {
                input = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("Welcome " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            }
        }

        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public void otherPlayerMoved(final int location) {
            output.println("OPPONENT_MOVED " + location);
            output.println(
                    hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "YOUR MOVE");
        }

        public void run() {
            try {
                output.println("MESSAGE All players connected");

                if(mark == 'X') {
                    output.println("MESSAGE Your move");
                }

                while (true) {
                    String command = input.readLine().trim();
                    output.println("MESSAGE command: " + command);
                    if(command.startsWith("MOVE")) {
                        int location = Integer.parseInt(command.substring(4));
                        output.println("MESSAGE location: " + location);
                        if (legalMove(location, this)) {
                            output.println("VALID_MOVE");
                            output.println(hasWinner() ? "VICTORY"
                                : boardFilledUp() ? "TIE"
                                : "");
                        } else {
                            output.println("MESSAGE ?");
                        }
                    } else if(command.startsWith("QUIT")) {
                        return;
                    } else {
                        output.println("MESSAGE Input smth like MOVE2");
                    }
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }
}
