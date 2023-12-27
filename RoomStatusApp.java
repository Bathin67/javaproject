import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomStatusApp extends JFrame {

    private static final int PORT = 5555;
    private ExecutorService executorService;

    public RoomStatusApp() {
        setTitle("Room Status App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JPanel mainPanel = new JPanel(new GridLayout(1, 5));

        for (int i = 1; i <= 5; i++) {
            RoomPanel roomPanel = new RoomPanel("Room " + i);
            mainPanel.add(roomPanel);
        }

        getContentPane().add(mainPanel);

        // Start TCP server
        executorService = Executors.newFixedThreadPool(5);
        startServer();
    }

    private void startServer() {
        try {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(new RoomStatusHandler(clientSocket));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RoomPanel extends JPanel {
        private String roomName;
        private JButton switchButton;
        private JLabel statusLabel;

        public RoomPanel(String roomName) {
            this.roomName = roomName;
            setLayout(new BorderLayout());

            switchButton = new JButton("Switch");
            switchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateStatus();
                }
            });

            statusLabel = new JLabel("Status: Off", SwingConstants.CENTER);

            add(switchButton, BorderLayout.NORTH);
            add(statusLabel, BorderLayout.CENTER);
        }

        private void updateStatus() {
            // Update status logic
            String status = (statusLabel.getText().equals("Status: Off")) ? "Status: On" : "Status: Off";
            statusLabel.setText(status);

            // Log to file logic
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", true))) {
                writer.write(roomName + " - " + status + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class RoomStatusHandler implements Runnable {
        public RoomStatusHandler(Socket clientSocket) {
        }

        @Override
        public void run() {
            // Handle incoming messages from clients
            // You can implement this based on your communication protocol
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RoomStatusApp app = new RoomStatusApp();
            app.setVisible(true);
        });
    }
}

