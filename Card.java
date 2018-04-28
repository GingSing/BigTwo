import java.io.Serializable;

/**
 * The type Card.
 */
public class Card implements Comparable<Card>, Serializable{

    private Suit suit;
    private CardValue number;

    /**
     * Instantiates a new Card.
     *
     * @param number the number
     * @param suit   the suit
     */
    public Card(CardValue number, Suit suit){

        this.number = number;
        this.suit = suit;

    }

    /**
     * Instantiates a new Card.
     *
     * @param anotherCard the another card
     */
    public Card(Card anotherCard){
        this.suit = anotherCard.getSuit();
        this.number = anotherCard.getnumber();

    }

    /**
     * Get suit suit.
     *
     * @return the suit
     */
    public Suit getSuit(){

        return suit;

    }

    /**
     * Get number card value.
     *
     * @return the card value
     */
    public CardValue getnumber(){

        return number;

    }

    /**
     * Get card value int.
     *
     * @return the int
     */
    public int getCardValue(){

        return number.getCardValue();

    }

    /**
     * Get card suit int.
     *
     * @return the int
     */
    public int getCardSuit(){

        return suit.getSuitValue();

    }

    /**
     *  Compares cards.
     *
     * @param o
     * @return
     */

    @Override
    public int compareTo(Card o) {
        if(this.getCardValue() > o.getCardValue()){
            return 1;
        }else if(this.getCardValue() < o.getCardValue()){
            return -1;
        }else{
            if(this.getCardSuit() > o.getCardSuit()){
                return 1;
            }else if(this.getCardSuit() < o.getCardSuit()){
                return -1;
            }else{
                return 0;
            }
        }
    }
}
