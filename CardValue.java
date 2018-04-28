/**
 * The enum Card value.
 */
public enum CardValue {

    /**
     * Three card value.
     */
    THREE(3),
    /**
     * Four card value.
     */
    FOUR(4),
    /**
     * Five card value.
     */
    FIVE(5),
    /**
     * Six card value.
     */
    SIX(6),
    /**
     * Seven card value.
     */
    SEVEN(7),
    /**
     * Eight card value.
     */
    EIGHT(8),
    /**
     * Nine card value.
     */
    NINE(9),
    /**
     * Ten card value.
     */
    TEN(10),
    /**
     * Jack card value.
     */
    JACK(11),
    /**
     * Queen card value.
     */
    QUEEN(12),
    /**
     * King card value.
     */
    KING(13),
    /**
     * Ace card value.
     */
    ACE(14),
    /**
     * Two card value.
     */
    TWO(15);

    private int cardValue;

    /**
     * Instantiates cardValue.
     *
     * @param value
     */
    CardValue(int value){

        this.cardValue = value;

    }

    /**
     * Get card value int.
     *
     * @return the int
     */
    public int getCardValue(){

        return cardValue;

    }
}
