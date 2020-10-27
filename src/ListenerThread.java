import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenerThread extends Thread {
    boolean running;
    private DatagramSocket UDPsocket;
    private byte[] buf = new byte[256];

    ListenerThread(int port) throws IOException {
        this.UDPsocket = new DatagramSocket(port);
        this.running = true;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while(running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                UDPsocket.receive(packet);
                String resposta = new String(packet.getData());
                System.out.println(resposta);
                if (resposta.equals("Até breve!")) {
                    UDPsocket.close();
                    running = false;
                }
                buf = new byte[buf.length];
            } catch (Exception e) {
                UDPsocket.close();
                e.printStackTrace();
            }
        }
    }
}
