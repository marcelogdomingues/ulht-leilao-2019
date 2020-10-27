import sun.awt.image.PixelConverter;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClientHandler extends Thread {
    private Utilizador user;
    private Socket socketTCP;
    private DataInputStream dataInputStream;
    private int portNumber;
    private MessageSender sender;

    private boolean conectado;

    ClientHandler(Socket socketTCP, DataInputStream dataInputStream, String UDPportNumber) {
        this.socketTCP = socketTCP;
        this.dataInputStream = dataInputStream;
        this.portNumber = Integer.parseInt(UDPportNumber);
        this.conectado = true;
    }

    @Override
    public void run() {
        try {
            sender = new MessageSender();
            while (conectado) {
                if (this.user == null) {
                    MenuLoginRegistar();
                } else {
                    showMenu();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /*public void sendEcho(String msg) throws IOException {
        //this.socketUDP = new DatagramSocket();
        this.buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length, this.socketTCP.getInetAddress(), this.portNumber);
        this.socketUDP.send(packet);
        this.buf = new byte[buf.length];
    }

    public void sendBroadcast(String msg) throws IOException {
        //this.socketUDP = new DatagramSocket();
        this.buf = msg.getBytes();

        for (ClientHandler handler : Regulador.licitadores) {
            if (!handler.getUser().getUsername().equals(this.getUser().getUsername())) {
                DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length, handler.getSocketTCP().getInetAddress(), handler.getPortNumber());
                this.socketUDP.send(packet);
            }
        }

        this.buf = new byte[buf.length];
    }*/

    public void MenuLoginRegistar() throws Exception {
        sender.sendEcho("Selecione uma opção:\n" + "1- Login\n"+ "2- Registar\n", getInetAddress(), getPortNumber());
        String opcaoLoginRegistar = dataInputStream.readUTF();

        if(opcaoLoginRegistar.equals("1")){ //fazer login
            Utilizador utilizador;

            sender.sendEcho("***Login de Utilizador***", getInetAddress(), getPortNumber());

            sender.sendEcho("Username:", getInetAddress(), getPortNumber());
            String username = dataInputStream.readUTF();
            sender.sendEcho("Password", getInetAddress(), getPortNumber());

            String password = dataInputStream.readUTF();

            String hashedPassword = Hash.hashWith256(password);
            utilizador = Regulador.users.get(username);
            if (utilizador != null && utilizador.getPassword().equals(hashedPassword)) {
                sender.sendEcho("Autenticação realizada com sucesso.", getInetAddress(), getPortNumber());
                this.user = utilizador;
                System.out.println("[INFO] Connected user: " + this.user.toString());
                showBalance();
                //showUserAuctions();
            } else {
                sender.sendEcho("Credenciais inválidas ou utilizador inexistente.", getInetAddress(), getPortNumber());
            }
        }

        if(opcaoLoginRegistar.equals("2")) { //fazer register
            Utilizador utilizador;

            sender.sendEcho("***Registo de Utilizador***", getInetAddress(), getPortNumber());
            sender.sendEcho("Escolha um username:", getInetAddress(), getPortNumber());
            String username = dataInputStream.readUTF();

            utilizador = Regulador.users.get(username);
            if (utilizador != null) {
                sender.sendEcho("Este username já está em uso. Por favor, tente novamente.", getInetAddress(), getPortNumber());
                return;
            }

            if (username.equals("")) {
                sender.sendEcho("Username inválido. Por favor, tente novamente.", getInetAddress(), getPortNumber());
                return;
            }

            sender.sendEcho("Insira uma password:", getInetAddress(), getPortNumber());
            String password = dataInputStream.readUTF();

            String hashedPassword = Hash.hashWith256(password);

            sender.sendEcho("[TEST] Insira a quantidade de dinheiro que deseja ter em sua conta inicialmente:", getInetAddress(), getPortNumber());
            String userInput = dataInputStream.readUTF();

            int saldoInicial = Integer.parseInt(userInput);

            if (saldoInicial < 0) {
                sender.sendEcho("Valor inválido. Por favor, tente novamente.", getInetAddress(), getPortNumber());
                return;
            }

            utilizador = new Utilizador(username, hashedPassword);
            utilizador.addCurrentBalance(saldoInicial);
            Regulador.updateUsers(utilizador);

            sender.sendEcho("Registou-se com sucesso! Será automaticamente conectado, para sua conveniência.", getInetAddress(), getPortNumber());
            this.user = Regulador.users.get(username);

            System.out.println("[INFO] New user: " + this.user.toString());
        }
    }

    public void showMenu() throws Exception {
        String menu =
                "Select an option:\n" +
                        "---------------------------\n" +
                        "1- Create auction\n" +
                        "2- Bid in an auction\n" +
                        "3- List available auctions\n" +
                        "4- Search auction\n" +
                        //"5- My auctions\n" +
                        "---------------------------\n" +
                        "5- Show balance\n" +
                        "6- Add balance\n" +
                        "---------------------------\n" +
                        "7- Quit";

        sender.sendEcho(menu, getInetAddress(), getPortNumber());

        String opcao;
        opcao = dataInputStream.readUTF();

        switch (opcao) {
            case "1":
                createAuction();
                break;
            case "2":
                bid();
                break;
            case "3":
                consultAuctions();
                break;
            case "4":
                searchAuction();
                break;
            /*case "5":
                showUserAuctions();
                break;*/
            case "5":
                showBalance();
                break;
            case "6":
                addBalance();
                break;
            case "7":
                closeConnection();
                break;
            default:
                sender.sendEcho("Comando inválido. Por favor, tente novamente.", getInetAddress(), getPortNumber());
                break;
        }
    }

    public void createAuction()  throws Exception { //Function to create an auction
        Leilao leilao;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        sender.sendEcho("Insira uma breve descrição do item a ser leiloado.", getInetAddress(), getPortNumber());
        String description = dataInputStream.readUTF();

        if (description.equals("")) {
            sender.sendEcho("Descrição insuficiente. Por favor, tente novamente.", getInetAddress(), getPortNumber());
            return;
        }

        sender.sendEcho("Insira um preço inicial para seu leilão.", getInetAddress(), getPortNumber());
        String value = dataInputStream.readUTF();
        int startingValue = Integer.parseInt(value);

        if (startingValue < 0) {
            sender.sendEcho("Valor inválido. Por favor, tente novamente.", getInetAddress(), getPortNumber());
            return;
        }

        sender.sendEcho("Insira uma data de término para seu leilão. Deve seguir o seguinte formato: dd-MM-yyyy HH:mm:ss", getInetAddress(), getPortNumber());
        String date = dataInputStream.readUTF();
        Date endDate = formatter.parse(date);

        Date currentDate = new Date();

        if (endDate.before(currentDate)) {
            sender.sendEcho("Valor inválido. Por favor, tente novamente.", getInetAddress(), getPortNumber());
            return;
        }

        leilao = new Leilao(this.user, endDate, description, startingValue);
        Regulador.addAuction(leilao);

        System.out.println("[INFO] User \"" + this.user.getUsername() + "\" created auction @" +
                formatter.format(currentDate) + " with ID " + leilao.getId() + "!\n" +
                "Description: " + description + " | Starting value: " + startingValue +
                " | End date: " + formatter.format(endDate));

        sender.sendEcho("O seu leilão foi criado com sucesso com ID " + leilao.getId() + ".", getInetAddress(), getPortNumber());
        for (ClientHandler handler : Regulador.handlers) {
            if (!handler.getUser().getUsername().equals(this.getUser().getUsername())) {
                sender.sendEcho("Há um novo leilão disponível, queira consultar os leilões disponíveis.", handler.getInetAddress(), handler.getPortNumber());
            }
        }
    }

    public void bid()  throws IOException { //Function to bid on an existing auction.
        String userInput;
        Leilao auction;
        Licitacao newBid;
        Licitacao lastBid;
        Utilizador lastBidder;

        sender.sendEcho("Insira o ID do leilão para o qual deseja licitar.", getInetAddress(), getPortNumber());
        userInput = dataInputStream.readUTF();

        int id = Integer.parseInt(userInput);
        if (!Regulador.auctions.containsKey(id)) {
            sender.sendEcho("O leilão com o ID " + id + " não existe ou já não está disponível.", getInetAddress(), getPortNumber());
            return;
        }

        auction = Regulador.auctions.get(id);
        if (auction.isClosed()) {
            sender.sendEcho("O leilão com o ID " + id + " não existe ou já não está disponível.", getInetAddress(), getPortNumber());
            return;
        }

        sender.sendEcho("Licitando para o leilão " + auction.getId() + "\n" +
                "Insira o valor da licitação.", getInetAddress(), getPortNumber());
        userInput = dataInputStream.readUTF();

        int value = Integer.parseInt(userInput);
        if (this.user.getCurrentBalance() < value) {
            sender.sendEcho("A sua solicitação não foi aceite, o valor da sua proposta é superior ao seu plafond.", getInetAddress(), getPortNumber());
            return;
        }

        if (value <= auction.getHighestBid()) {
            sender.sendEcho("A sua licitação não foi aceite, o valor proposto não é superior ao máximo atual.", getInetAddress(), getPortNumber());
            return;
        }

        newBid = new Licitacao(auction, this.user, value, new Date());

        lastBid = auction.getLastBid();
        if (lastBid != null) {
            lastBidder = lastBid.getBidder();
            lastBidder.addCurrentBalance(auction.getHighestBid());
            Regulador.updateUsers(lastBidder);
        }

        auction.addLicitacao(newBid);
        Regulador.updateAuctions(auction);

        this.user.subtractCurrentBalance(value);
        Regulador.updateUsers(this.user);

        System.out.println("[INFO] User \"" + this.user.getUsername() + "\" successfully bid on auction with ID " + auction.getId());
        sender.sendEcho("A sua licitação foi aceite.", getInetAddress(), getPortNumber());

        for (ClientHandler handler : Regulador.handlers) {
            if (!handler.getUser().getUsername().equals(this.getUser().getUsername())) {
                sender.sendEcho("Foi recebida uma nova licitação no leilão com ID " + auction.getId() + ".", handler.getInetAddress(), handler.getPortNumber());
            }
        }

    }

    public void consultAuctions() throws IOException  { //Function to list this user's auctions, as well as ones he has bid on.
        sender.sendEcho("Plafond disponível: " + this.user.getCurrentBalance() +
                "\n------------------------------------------", getInetAddress(), getPortNumber());
        for(Leilao leilao : Regulador.auctions.values()) {
            if (!leilao.isClosed()) {
                sender.sendEcho(leilao.toString(), getInetAddress(), getPortNumber());
            }
        }
    }

    public void searchAuction() throws IOException  { //Function to search for a specific auction given its ID and display it to user.
        sender.sendEcho("Insira o ID do leilão a procurar.", getInetAddress(), getPortNumber());
        String userInput = dataInputStream.readUTF();
        int id = Integer.parseInt(userInput);

        if(!Regulador.auctions.containsKey(id)) {
            sender.sendEcho("O leilão com o ID " + id + " não existe ou já não está disponível.", getInetAddress(), getPortNumber());
            return;
        }

        Leilao leilao = Regulador.auctions.get(id);

        sender.sendEcho(Regulador.auctions.get(id).toString() +
                "\n------------------------------------------", getInetAddress(), getPortNumber());
        for (Licitacao licitacao : leilao.getBids()) {
            sender.sendEcho(licitacao.toString(), getInetAddress(), getPortNumber());
        }
    }

    public void addBalance()  throws IOException  { //Function to add money to user's balance.
        sender.sendEcho("Insira o valor a depositar.", getInetAddress(), getPortNumber());
        String userInput = dataInputStream.readUTF();

        int amount = Integer.parseInt(userInput);
        if (amount < 0) {
            sender.sendEcho("Valor inválido. Por favor, tente novamente.", getInetAddress(), getPortNumber());
            return;
        }

        this.user.addCurrentBalance(amount);
        Regulador.updateUsers(this.user);

        sender.sendEcho("Plafond atualizado.", getInetAddress(), getPortNumber());
        showBalance();
    }

    public void showBalance() throws IOException  { //Function to show user's balance.
        int userBalance = this.user.getCurrentBalance();
        sender.sendEcho("O seu plafond atual é de " + userBalance + " euros.", getInetAddress(), getPortNumber());
    }

    /*public void showUserAuctions() throws IOException  { //Function to show auctions in which the user participates.
        sendEcho("Meus leilões (" + this.user.getAuctions().size() + "):" +
                "\n------------------------------------------");
        for (Leilao leilao : this.user.getAuctions()) {
            sendEcho(leilao.toString());
        }
    }*/

    public void closeConnection() throws IOException {
        sender.sendEcho("Até breve!", getInetAddress(), getPortNumber());
        System.out.println("[INFO] User \"" + this.user.getUsername() + "\" has disconnected.");
        this.socketTCP.close();
        this.conectado = false;
        Regulador.handlers.remove(this);
    }

    public Utilizador getUser() {
        return this.user;
    }

    public InetAddress getInetAddress() {
        return this.socketTCP.getInetAddress();
    }

    public int getPortNumber() {
        return portNumber;
    }

    public boolean isConectado() {
        return this.conectado;
    }

}
