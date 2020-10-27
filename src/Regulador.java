import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Regulador {
    public static Integer lastAuctionID = 0;

    public static Map<String, Utilizador> users = new HashMap<>();
    public static Map<Integer, Leilao> auctions = new HashMap<>();

    public static List<ClientHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        boolean online = true;
        int port = Integer.parseInt(args[0]);

        try {
            System.out.println("Recovering user data...");
            users = (HashMap<String, Utilizador>) Serializable.deserialize("UserData");
            System.out.println(users.values());
        } catch (Exception e) {
            System.out.println();
        }

        try {
            System.out.println("Recovering auction data...");
            auctions = (HashMap<Integer, Leilao>) Serializable.deserialize("AuctionData");
            System.out.println(auctions.values());

            Map.Entry<Integer, Leilao> firstEntry = auctions.entrySet().iterator().next();
            int largestKey = firstEntry.getKey();

            for (Map.Entry<Integer, Leilao> map : auctions.entrySet()) {
                int key = map.getKey();
                if (key > largestKey) {
                    largestKey = key;
                }
            }
            Regulador.lastAuctionID = largestKey;
        } catch (Exception e) {
            System.out.println();
        }

        System.out.println("Starting auction ender thread...");
        AuctionEnder auctionEnder = new AuctionEnder();
        auctionEnder.start();

        ServerSocket serverSocket = new ServerSocket(port);
        Socket socketTCP;

        System.out.println("Server.Client Handler started at port " + serverSocket.getLocalPort());

        while(online) {
            try {
                socketTCP = serverSocket.accept();

                System.out.println("A client is connected: " + socketTCP);

                DataInputStream dataInputStream = new DataInputStream(socketTCP.getInputStream());

                System.out.println("Getting port information:");
                String portNumber = dataInputStream.readUTF();

                System.out.println("Assigning new thread for this client.");

                ClientHandler handler = new ClientHandler(socketTCP, dataInputStream, portNumber);
                handlers.add(handler);
                handler.start();
            } catch (Exception exception) {
                exception.printStackTrace();
                online = false;
            }
        }
    }

    public static synchronized void addAuction(Leilao auction) throws IOException {
        Regulador.lastAuctionID = Regulador.lastAuctionID + 1;
        auction.setId(Regulador.lastAuctionID);
        Regulador.auctions.put(auction.getId(), auction);
        Serializable.serialize(Regulador.auctions, "AuctionData");
    }

    public static synchronized void updateUsers(Utilizador user) throws IOException {
        Regulador.users.put(user.getUsername(), user);
        Serializable.serialize(Regulador.users, "UserData");
    }

    public static synchronized void updateAuctions(Leilao auction) throws IOException {
        Regulador.auctions.put(auction.getId(), auction);
        Serializable.serialize(Regulador.auctions, "AuctionData");
    }
}
