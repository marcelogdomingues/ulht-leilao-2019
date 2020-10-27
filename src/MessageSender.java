//import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class MessageSender {
    private DatagramSocket socketUDP;

    MessageSender() {
        try {
            this.socketUDP = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEcho(String msg, InetAddress address, int port) throws IOException {
        byte[] buf;
        buf = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        this.socketUDP.send(packet);
    }

    /*public void sendBroadcast(String msg, List<ClientHandler> targets, List<String> exclude) throws IOException {
        byte[] buf;
        buf = msg.getBytes();

        for (ClientHandler handler : targets) {
            if (!exclude.equals(handler.getUser().getUsername())) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length, handler.getInetAddress(), handler.getPortNumber());
                this.socketUDP.send(packet);
            }
        }
    }*/
}
