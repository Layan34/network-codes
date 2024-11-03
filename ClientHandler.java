/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package networkproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private String playerName;
    private ArrayList<ClientHandler> clients;
    private ArrayList<String> waitingRoom;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients, ArrayList<String> waitingRoom) {
        this.clientSocket = clientSocket;
        this.clients = clients;
        this.waitingRoom = waitingRoom;

        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
  public void run() {
    try {
        playerName = input.readLine();
        System.out.println(playerName + " has joined the server.");
        
        output.println("Welcome to the server, " + playerName + "!");
        GameServer.broadcastPlayers();

        String message;
        while ((message = input.readLine()) != null) {
            if (message.startsWith("DISCONNECT")) {
                System.out.println(playerName + " has disconnected.");
                GameServer.removeClient(this);
                break;
            } else if (message.equals("PLAY")) {
                if (!waitingRoom.contains(playerName) && waitingRoom.size() < 3) {
                    waitingRoom.add(playerName);
                    GameServer.broadcastInRoom(); // Broadcast updated list to playroom
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error handling client: " + e.getMessage());
    } finally {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing client socket: " + e.getMessage());
        }
    }
}
    public void sendMessage(String message) {
        output.println(message);
    }

    public String getPlayerName() {
        return playerName;
    }

   public void updateRoom(String[] names) {
    String playersList = "Players in the playroom: " + String.join(", ", names);
    output.println(playersList);
}
}
