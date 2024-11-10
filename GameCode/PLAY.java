package networkproject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class PLAY extends JFrame {

    private Timer gameTimer;
    private Timer waitingRoomTimer;
    private int gameTime = 10;  // Game time in seconds
    private int waitingTime = 30; // Waiting time in seconds
    private HashMap<String, Integer> playerClicks = new HashMap<>();
    private ArrayList<String> playersInRoom = new ArrayList<>();
    private String currentPlayer;

    // GUI components
    private JLabel timeLabel;
    private JLabel waitingRoomLabel;
    private JLabel clickCountLabel;
    private JTextArea playersArea;
    private JButton startButton;

    public PLAY() {
        initComponents();
    }

    // Method to set the player name after instantiation
    public void setPlayerName(String playerName) {
        this.currentPlayer = playerName;
        DisplayPlayersInRoom(new String[]{playerName});
    }

    // Method to display players in the room without duplicating names
    public void DisplayPlayersInRoom(String[] players) {
        playersArea.setText("Current Players:\n");
        playersInRoom.clear();
        playerClicks.clear(); // Clear previous data to prevent duplication in the click count

        for (String player : players) {
            if (!playersInRoom.contains(player)) {  // Only add if the player is not already in the list
                playersInRoom.add(player);
                playerClicks.put(player, 0);  // Initialize the click count for the player
                playersArea.append(player + "\n");  // Display the player in the UI
            }
        }
        checkPlayerCount();
    }

    private void initComponents() {
        // Initialize GUI components
        startButton = new JButton("Start Game");
        startButton.setVisible(false);
        playersArea = new JTextArea();
        JScrollPane jScrollPane1 = new JScrollPane(playersArea);
        timeLabel = new JLabel("Game Time Left: " + gameTime + "s");
        waitingRoomLabel = new JLabel("Waiting for players... Time Left: --");
        clickCountLabel = new JLabel("Click Count: 0");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        startButton.addActionListener(evt -> startGame());

        playersArea.setColumns(20);
        playersArea.setRows(5);
        playersArea.setEditable(false);
        jScrollPane1.setViewportView(playersArea);

        // Key listener to count spacebar presses
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameTimer != null && gameTimer.isRunning()) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        playerClicks.put(currentPlayer, playerClicks.getOrDefault(currentPlayer, 0) + 1);
                        updateClickCount();
                    }
                }
            }
        });

        // Set focusable to true to receive key events
        setFocusable(true);
        requestFocusInWindow();

        // Layout setup
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(timeLabel)
                    .addComponent(waitingRoomLabel)
                    .addComponent(clickCountLabel))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(waitingRoomLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(timeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(clickCountLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(startButton)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }

    private void checkPlayerCount() {
        if (playersInRoom.size() == 2) {
            startWaitingRoomTimer(); // Start 10-second timer if there are exactly 2 players
        } else if (playersInRoom.size() == 3) {
            if (waitingRoomTimer != null && waitingRoomTimer.isRunning()) {
                waitingRoomTimer.stop(); // Stop the waiting timer if the third player joins
            }
            startGame(); // Start the game immediately if 3 players join
        }
    }

    private void startWaitingRoomTimer() {
        waitingRoomLabel.setText("Waiting for players... Time Left: " + waitingTime + "s");
        waitingRoomTimer = new Timer(1000, new ActionListener() {
            int countdown = waitingTime;

            @Override
            public void actionPerformed(ActionEvent e) {
                countdown--;
                waitingRoomLabel.setText("Waiting for players... Time Left: " + countdown + "s");

                if (countdown <= 0) {
                    waitingRoomTimer.stop();
                    if (playersInRoom.size() >= 2) {
                        startGame();
                    } else {
                        JOptionPane.showMessageDialog(PLAY.this, "Not enough players. Waiting time expired.");
                        waitingRoomLabel.setText("Waiting for players... Time Left: --");
                    }
                }
            }
        });
        waitingRoomTimer.start();
    }

    private void startGame() {
        if (playersInRoom.size() < 2) {
            JOptionPane.showMessageDialog(this, "Not enough players to start the game.");
            return;
        }

        requestFocusInWindow(); // Ensure the frame is focused for key events
        startButton.setVisible(false); // Hide the start button once the game starts

        // Start the game countdown timer
        gameTimer = new Timer(1000, new ActionListener() {
            int countdown = gameTime;

            @Override
            public void actionPerformed(ActionEvent e) {
                countdown--;
                timeLabel.setText("Game Time Left: " + countdown + "s");

                if (countdown <= 0) {
                    gameTimer.stop();
                    announceWinner();
                }
            }
        });
        gameTimer.start();
    }

    private void updateClickCount() {
        clickCountLabel.setText("Click Count: " + playerClicks.getOrDefault(currentPlayer, 0));
    }

    private void announceWinner() {
        String winner = playerClicks.entrySet()
            .stream()
            .max((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
            .map(entry -> entry.getKey() + " with " + entry.getValue() + " clicks!")
            .orElse("No winner");

        JOptionPane.showMessageDialog(this, "Game Over! Winner: " + winner);
        playerClicks.replaceAll((k, v) -> 0); // Reset clicks for next game
        clickCountLabel.setText("Click Count: 0");
        waitingRoomLabel.setText("Waiting for players... Time Left: --");
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PLAY().setVisible(true));
    }
}
