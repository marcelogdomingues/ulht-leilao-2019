import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class Licitador {

    public static void main(String[] args) throws IOException {
        boolean connected;

        String serverIn = args[0];
        int port = Integer.parseInt(args[1]);

        Socket socketTCP = new Socket(serverIn, port);
        ListenerThread listenerThread = new ListenerThread(port);

        DataOutputStream dataOutputStream = new DataOutputStream(socketTCP.getOutputStream());

        System.out.println("Starting thread.");
        listenerThread.start();

        System.out.println("Sending UDP port information:");
        dataOutputStream.writeUTF("" + port);

        System.out.println("Ready to send messages.");
        connected = true;

        while(connected) {
            try {
                if (listenerThread.isRunning()) {
                    Scanner userInput = new Scanner(System.in);

                    String mensagem = userInput.nextLine();

                    dataOutputStream.writeUTF(mensagem);
                } else {
                    socketTCP.close();
                    dataOutputStream.close();
                    connected = false;
                }
            } catch (Exception exception) {
                socketTCP.close();
                dataOutputStream.close();
                exception.printStackTrace();
                connected = false;
            }
        }
    }
}

