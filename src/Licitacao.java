import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Licitacao implements Serializable {
    private int bidValue;
    private Utilizador bidder;
    private Leilao auction;
    private Date bidDate;

    Licitacao(Leilao auction, Utilizador bidder, int bidValue, Date bidDate) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidValue = bidValue;
        this.bidDate = bidDate;
    }

    public int getBidValue() {
        return bidValue;
    }

    public Leilao getAuction() {
        return auction;
    }

    public Date getBidDate() {
        return this.bidDate;
    }

    public String getBidDateToString() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        return formatter.format(this.bidDate);
    }

    public Utilizador getBidder() {
        return bidder;
    }

    @Override
    public String toString() {
        return this.getBidDateToString() + " €" + this.bidValue + " " + this.bidder.getUsername();
    }
}
