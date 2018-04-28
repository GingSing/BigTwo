/**
 * The enum Suit.
 */
public enum Suit {

    /**
     * Diamonds suit.
     */
    DIAMONDS(1),
    /**
     * Clubs suit.
     */
    CLUBS(2),
    /**
     * Hearts suit.
     */
    HEARTS(3),
    /**
     * Spades suit.
     */
    SPADES(4);

    private int suitValue;

    /**
     *  Instantiates suit.
     * @param value
     */
    Suit(int value){

        this.suitValue = value;

    }

    /**
     * Get suit value int.
     *
     * @return the int
     */
    public int getSuitValue(){

        return suitValue;

    }

}
