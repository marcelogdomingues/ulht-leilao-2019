import java.io.IOException;
import java.util.Date;

public class AuctionEnder extends Thread {
    private MessageSender sender;

    @Override
    public void run() {
        try {
            sender = new MessageSender();
            while (true) {
                Date currentDate;
                for (Leilao auction : Regulador.auctions.values()) {
                    currentDate = new Date();
                    if ((!auction.isClosed()) && auction.getEndDate().before(currentDate)) {
                        System.out.println("[INFO] Auction with ID " + auction.getId() + " expired.");
                        endAuction(auction);
                    }
                }
                sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endAuction(Leilao auction) throws IOException {
        String auctionAuthorUsername = auction.getAuthor().getUsername();
        String auctionWinnerUsername = "";
        if (auction.getLastBid() != null) {
            auctionWinnerUsername = auction.getLastBid().getBidder().getUsername();
        }

        auction.closeAuction();
        Regulador.updateAuctions(auction);

        for (ClientHandler licitador : Regulador.handlers) {
            if (licitador.getUser().getUsername().equals(auctionAuthorUsername)) {
                if (auction.getBids().size() == 0) {
                    sender.sendEcho("Lamentamos, mas o seu leilão com o ID " +
                                    auction.getId() + " fechou sem qualquer licitacão.",
                            licitador.getInetAddress(), licitador.getPortNumber());
                } else {
                    sender.sendEcho("O bem presente no leilão com o ID " + auction.getId() +
                                    " foi vendido à pessoa " + auction.getLastBid().getBidder().getUsername() +
                                    " com o valor de " + auction.getHighestBid() + " euros.",
                            licitador.getInetAddress(), licitador.getPortNumber());
                }
            }

            if ((!auctionWinnerUsername.equals("")) && licitador.getUser().getUsername().equals(auctionWinnerUsername)) {
                sender.sendEcho("Parabéns! Foi o vencedor do leilão com o ID " +
                                auction.getId() + " no valor de " + auction.getHighestBid() + " euros.",
                        licitador.getInetAddress(), licitador.getPortNumber());
            }

            if ((!licitador.getUser().getUsername().equals(auctionAuthorUsername)) && (!licitador.getUser().getUsername().equals(auctionWinnerUsername))) {
                sender.sendEcho("O leilão com o ID " + auction.getId() +
                                " no qual realizou licitações já fechou, infelizmente você não foi o vencedor.",
                        licitador.getInetAddress(), licitador.getPortNumber());
            }
        }
    }
}
