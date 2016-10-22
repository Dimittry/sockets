package SocketTuts.TicTacToe.model;

import java.net.Socket;

public class Player extends Thread {

    private final String name;

    private final Figure figure;

    private final Socket socket;

    private Player opponent;

    public Player(Socket socket, String name, Figure figure) {
        this.socket = socket;
        this.name  = name;
        this.figure = figure;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public Player getOpponent() {
        return opponent;
    }

    public String getPlayerName() {
        return name;
    }

    public Figure getFigure() {
        return figure;
    }
}
