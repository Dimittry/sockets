package SocketTuts.TicTacToe.controllers;


import SocketTuts.TicTacToe.model.Point;
import SocketTuts.TicTacToe.model.exeptions.InvalidPointException;
import SocketTuts.TicTacToe.view.reader.ConsoleCoordinateReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameSimple {
    private static final int MIN_SIZE = 3;

    private static final int MIN_COORDINATE = 0;

    private Player[][] board;

    private Player currentPlayer;

    private ConsoleCoordinateReader coordinateReader;

    public GameSimple() {
        board = new Player[MIN_SIZE][MIN_SIZE];
        coordinateReader = new ConsoleCoordinateReader();
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean hasWinner() {
        // check rows
        for(int i = 0; i < MIN_SIZE; i++) {
            if(check(new Point(i, 0), p -> new Point(p.getX(), p.getY() + 1))) {
                //return getFigure(new Point(i, 0));
                return true;
            }
        }
        // check columns
        for(int i = 0; i < MIN_SIZE; i++) {
            if(check(new Point(0, i), p -> new Point(p.getX()+1, p.getY()))) {
                //return getFigure(new Point(i, 0));
                return true;
            }
        }

        if(check(new Point(0, 0), p -> new Point(p.getX()+1, p.getY() + 1))) {
            //return getFigure(new Point(i, 0));
            return true;
        }

        if(check(new Point(0, MIN_SIZE - 1), p -> new Point(p.getX()+1, p.getY() - 1))) {
            //return getFigure(new Point(i, 0));
            return true;
        }

        return false;
    }

    private boolean check(final Point currentPoint,
                          final IPointGenerator pointGenerator) {
        final Player currentFigure;
        final Player nextFigure;
        final Point nextPoint = pointGenerator.next(currentPoint);
        try {

            currentFigure = getFigure(currentPoint);

            if(currentFigure == null) {
                return false;
            }

            nextFigure = getFigure(nextPoint);
        } catch (final InvalidPointException e) {
            return  true;
        }

        if(currentFigure != nextFigure)
            return false;

        return check(nextPoint, pointGenerator);
    }

    private Player getFigure(final Point point) throws InvalidPointException {
        if(!checkPoint(point)) {
            throw new InvalidPointException();
        }

        return board[point.getX()][point.getY()];
    }

    private boolean checkPoint(final Point point) {
        return checkCoordinate(point.getX(), board.length)
                && checkCoordinate(point.getY(), board[point.getX()].length);
    }

    private boolean checkCoordinate(final int coordinate,
                                    final int maxCoordinate) {
        return coordinate >= MIN_COORDINATE && coordinate < maxCoordinate;
    }

    private interface IPointGenerator {
        Point next(final Point point);
    }

    public boolean boardFilledUp() {
        int countFigures = 0;

        for(int i = 0; i < MIN_SIZE; i++) {
            countFigures += countFiguresInTheRow(i);
        }

        if(countFigures == MIN_SIZE * MIN_SIZE) {
            return true;
        }
        return false;
    }

    private int countFiguresInTheRow(int row) {
        int countFigure = 0;
        for(int x = 0; x < MIN_SIZE; x++) {
            try {
                if(getFigure(new Point(x, row)) != null)
                    countFigure++;
            } catch (final InvalidPointException e) {
                e.printStackTrace();
            }
        }
        return countFigure;
    }

    public synchronized boolean legalMove(Point point, Player player) throws InvalidPointException {
        if(!checkPoint(point)) {
            throw new InvalidPointException();
        }
        if(player == currentPlayer && board[point.getX()][point.getY()] == null) {
            board[point.getX()][point.getY()] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(point);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Player player = (Player) o;

            return mark == player.mark;

        }

        @Override
        public int hashCode() {
            return (int) mark;
        }

        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public void otherPlayerMoved(final Point point) {
            output.println("OPPONENT_MOVED " + point);
            output.println(
                    hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "YOUR MOVE");
        }

        public char getMark() {
            return mark;
        }

        public void run() {
            try {
                output.println("MESSAGE All players connected");

                if(mark == 'X') {
                    output.println("MESSAGE Your move");
                }

                while (true) {
                    Point point = new Point(coordinateReader.askCoordinate(input, output, "X"),
                            coordinateReader.askCoordinate(input, output, "Y"));
                    output.println("You point is " + point);

                    //String command = input.readLine().trim();
                    //output.println("MESSAGE command: " + command);
                    //if(command.startsWith("M")) {
                    if(checkPoint(point)) {
                        /*
                        try {
                            point = getPoint(command.substring(1));
                        } catch (InvalidPointException e) {
                            output.println("MESSAGE Wrong coordinates. Try again.");
                            continue;
                        }
                        */
                        // int location = Integer.parseInt(command.substring(4));
                        output.println("MESSAGE location: " + point);
                        try {
                            if (legalMove(point, this)) {
                                output.println("VALID_MOVE");
                                if(hasWinner()) {
                                    output.println("VICTORY");
                                    return;
                                } else if(boardFilledUp()) {
                                    output.println("TIE");
                                    return;
                                }
                            } else {
                                output.println("MESSAGE ?");
                            }
                        } catch (InvalidPointException e) {
                            output.println("MESSAGE Wrong coordinates. Try again.");
                            continue;
                        }
                    }
                    /* else if(command.startsWith("QUIT")) {
                        return;
                    }*/
                    else {
                        output.println("MESSAGE Input smth like MOVE2");
                    }

                }
            }
            /*
            catch (IOException e) {
                System.out.println("Player died: " + e);
            }
            */
            finally {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }

        private Point getPoint(String coords) throws InvalidPointException {
            String[] coordsArr = coords.split("_");
            Point point = null;
            try {
                point = new Point(Integer.parseInt(coordsArr[0]), Integer.parseInt(coordsArr[1]));
            } catch(NumberFormatException e) {
                throw new InvalidPointException();
            }
            System.out.println(point);
            return point;
        }
    }
}
