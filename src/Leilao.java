import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Leilao implements Serializable {
    private int id;
    private int highestBid;
    private String description;
    private Utilizador author;
    private List<Licitacao> bids = new ArrayList<>();
    private Date endDate;
    private boolean fechado;

    Leilao(Utilizador author, Date endDate, String description, int startingBid) {
        this.author = author;
        this.endDate = endDate;
        this.description = description;
        this.highestBid = startingBid;
    }

    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addLicitacao(Licitacao licitacao) {
        this.bids.add(licitacao);
        this.highestBid = licitacao.getBidValue();
    }

    public boolean isClosed() {
        return this.fechado;
    }

    public void closeAuction() {
        this.fechado = true;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateToString() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(this.endDate);
    }

    public Utilizador getAuthor() {
        return this.author;
    }

    public int getHighestBid() {
        return this.highestBid;
    }

    public Licitacao getLastBid() {
        if (this.bids.size() != 0) {
            return this.bids.get(bids.size() - 1);
        }
        return null;
    }

    public List<Licitacao> getBids() {
        return this.bids;
    }

    @Override
    public String toString() {
        String bidderStatus = "";
        String dateStatus = "";

        if (this.bids.size() == 0) {
            bidderStatus += this.getAuthor().getUsername() + " [No bids]";
        } else {
            bidderStatus += this.bids.get(bids.size() - 1).getBidder().getUsername();
        }

        if (isClosed()) {
            dateStatus = "[EXPIRED] ";
        }

        return dateStatus + this.getId() + " " + this.getDescription() + " " +
                this.getEndDateToString() + " €" + this.getHighestBid() + " " + bidderStatus;
    }
}

