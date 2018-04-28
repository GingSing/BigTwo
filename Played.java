import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Played.
 */
public class Played implements Serializable{

    private ArrayList<ArrayList<Card>> playedPile;

    /**
     * Instantiates a new Played.
     */
    public Played(){
        this.playedPile = new ArrayList<ArrayList<Card>>();
        ArrayList<Card> hand = new ArrayList<Card>();
        playedPile.add(hand);
    }

    /**
     * Get last played array list.
     *
     * @return the array list
     */
    public ArrayList<Card> getLastPlayed(){

        return this.playedPile.get(this.playedPile.size() - 1);

    }

    /**
     * Adds Played card to Arraylist.
     *
     * @param uncastedPlayedHand the played hand
     */
    public void playedCard(ArrayList<?> uncastedPlayedHand){

        ArrayList<Card> playedHand = new ArrayList<Card>();
        for(Object card : uncastedPlayedHand){
            playedHand.add((Card) card);
        }

        this.playedPile.add(playedHand);

    }

    /**
     * Print all played string.
     *
     * @return the string
     */
    public String printPlayed(){

        int count;
        String text = "";
        for(ArrayList<Card> hand: this.playedPile){
            count = 0;
            text += "[" ;
            for(Card card: hand){
                if(count != 0){
                    text+= ", ";
                }else{
                    count++;
                }
                text += card.getCardValue() + " of " + card.getSuit();
            }
            text += "] \n";
        }

        return text;
    }

    /**
     * Print last played string.
     *
     * @return the string
     */
    public String printLastPlayed(){

        int count = 0;
        String text = "";
        ArrayList<Card> hand = this.playedPile.get(this.playedPile.size() - 1);

        text += "Last Played: [";
        for(Card card: hand){

            if(count != 0){
                text += ", ";
            }else{
                count++;
            }

            if (card.getCardValue() == 11) {
                text += "J";
            } else if (card.getCardValue() == 12) {
                text += "Q";
            } else if (card.getCardValue() == 13) {
                text += "K";
            } else if (card.getCardValue() == 14) {
                text += "A";
            } else if (card.getCardValue() == 15) {
                text += "2";
            }else{
                text += card.getCardValue();
            }
            text += " of " + card.getSuit();
        }

        text += "] \n";

        return text;
    }

}
