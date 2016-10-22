package SocketTuts.TicTacToe.view.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleCoordinateReader implements ICoordinateReader {

    public int askCoordinate(BufferedReader in, PrintWriter out, String coordinateName) {
        //final Scanner in = new Scanner(System.in);
        try {
            out.format("Please input %s \n", coordinateName);
            return Integer.parseInt(in.readLine().trim());
            //return in.nextInt();
        } catch (final InputMismatchException | IOException | NumberFormatException e) {
            if(out != null)
                out.println("0_0 ?");
            return askCoordinate(in, out, coordinateName);
        }
    }
}
