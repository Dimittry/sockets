package SocketTuts.Capitalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CapitalizeServer {

    private static final int DEFAULT_PORT = 9898;

    public static void main(String[] args) {
        System.out.println("The capitalization server is running.");
        int clientNumber = 0;
        try {
            ServerSocket listener = new ServerSocket(DEFAULT_PORT);
            while(true) {
                new Capitalizer(listener.accept(), clientNumber++).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Capitalizer extends  Thread {
        private Socket socket;
        private int clientNumber;

        public Capitalizer(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true);
                printWelcomeMessage(out);

                while (true) {
                    String input = in.readLine();
                    if(input == null || input.equals(".")) {
                        break;
                    }
                    out.println(input.toUpperCase());
                }
            } catch (IOException e) {
                log("Error handling client#" + clientNumber +": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket.");
                }
            }
        }

        private void printWelcomeMessage(PrintWriter pw) {
            pw.println("Hello, you are client #" + clientNumber);
            pw.println("Enter a line with only a period to quit.");
        }

        private void log(String message) {
            System.out.println(message);
        }
    }

}
