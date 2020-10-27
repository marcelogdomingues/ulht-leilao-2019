import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utilizador implements Serializable {
    private String username;
    private String password;
    private int currentBalance; //This user's current balance.
    //private List<Leilao> auctions = new ArrayList<>(); //List of auctions this user is participating in.

    Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword(){
        return  this.password;
    }

    public int getCurrentBalance() {
        return this.currentBalance;
    }

    /*public void addAuction(Leilao auction) {
        this.auctions.add(auction);
    }

    public void removeAuction(Leilao auction) {
        for (Leilao leilao : this.auctions) {
            if(leilao.getId() == auction.getId()) {
                this.auctions.remove(leilao);
            }
        }
    }

    public List<Leilao> getAuctions() {
        return auctions;
    }*/

    public void addCurrentBalance(int value) {
        this.currentBalance += value;
    }

    public void subtractCurrentBalance(int value){
        this.currentBalance -= value;
    }

    @Override
    public String toString() {
        return "Username: " + this.username + " | Password: " + this.password
                + " | Balance: " + this.currentBalance /*+ " | Auctions: " + this.auctions.toString()*/;
    }
}
