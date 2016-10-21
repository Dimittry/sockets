package SocketTuts.TicTacToe.view.reader;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleCoordinateReader implements ICoordinateReader {

    public int askCoordinate(String coordinateName) {
        System.out.format("Please input %s", coordinateName);
        final Scanner in = new Scanner(System.in);
        try {
            return in.nextInt();
        } catch (final InputMismatchException e) {
            System.out.println("0_0 ?");
            return askCoordinate(coordinateName);
        }
    }
}
