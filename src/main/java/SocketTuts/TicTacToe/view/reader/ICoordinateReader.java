package SocketTuts.TicTacToe.view.reader;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public interface ICoordinateReader {
    int askCoordinate(final BufferedReader in, final PrintWriter out, final String coordinateName);
}
