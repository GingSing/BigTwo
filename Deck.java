import java.util.ArrayList;
import java.util.Collections;

/**
 * The type Deck.
 */
public class Deck {

    private ArrayList<Card> cards;

    /**
     * Instantiates a new Deck.
     */
    public Deck(){

        this.cards = new ArrayList<Card>();
        for(int i = 0; i < 13; i++){
            CardValue value = CardValue.values()[i];
            for(int j = 0; j < 4; j++){
                Card card = new Card(value, Suit.values()[j]);
                this.cards.add(card);
            }
        }
    }

    public ArrayList<Card> getAllCards(){
        return cards;
    }

    /**
     * Shuffle deck.
     */
    public void shuffleDeck(){
        Collections.shuffle(cards);
    }

    /**
     * Get card value int.
     *
     * @param number the number
     * @return the int
     */
    public int getCardValue(int number){
        return cards.get(number).getCardValue();
    }

    /**
     * Get card card.
     *
     * @param number the number
     * @return the card
     */
    public Card getCard(int number){
        return cards.get(number);
    }
}
