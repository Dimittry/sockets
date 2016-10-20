package SocketTuts.DateServerClient;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DateClient {

    private static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) {
        String serverAddress
                = JOptionPane.showInputDialog("Enter IP address.");
        try {
            Socket s = new Socket(serverAddress, DEFAULT_PORT);
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            String answer = input.readLine();
            JOptionPane.showMessageDialog(null, answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
