/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package networkproject;

/**
 *
 * @author Layan saad
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer {
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ArrayList<String> waitingRoom = new ArrayList<>(); // Renamed for clarity

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);

        while (true) {
            System.out.println("Waiting for client connection");
            Socket client = serverSocket.accept();
            System.out.println("Connected to client");

            ClientHandler clientThread = new ClientHandler(client, clients, waitingRoom); // Create new client thread
            clients.add(clientThread); // Add the client to the list
            new Thread(clientThread).start(); // Start the client thread
        }
    }

    // Method to remove a client when they disconnect
    public static void removeClient(ClientHandler client) {
        clients.remove(client);  // Remove the client from the list of connected clients
        System.out.println(client.getPlayerName() + " has disconnected.");

        // Broadcast the updated list of connected players to all clients
        broadcastPlayers();
    }

    // Method to broadcast the list of connected players to all clients
    public static void broadcastPlayers() {
        StringBuilder playersList = new StringBuilder("Connected players: ");
        for (ClientHandler client : clients) {
            playersList.append(client.getPlayerName()).append(", ");
        }

        // Send the updated player list to all clients
        for (ClientHandler client : clients) {
            client.sendMessage(playersList.toString());
        }
    }
    
 public static void broadcastInRoom() {
    String[] room = waitingRoom.toArray(new String[0]);
    String playersListMessage = "Players in the playroom: " + String.join(", ", room);

    // Notify each client in the playroom of the updated player list
    for (ClientHandler player : clients) {
        if (waitingRoom.contains(player.getPlayerName())) {
            player.sendMessage(playersListMessage);
        }
    }
}}