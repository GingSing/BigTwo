import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/** Controls the cards and the plays that the player can make.
 *
 */
public class CardGame extends JComponent{

    private ArrayList<Card> hand;
    private ArrayList<Integer> selectedCards;
    private ArrayList<Card> myLastPlayed;
    private ArrayList<Card> lastPlayed;
    private int[] xPos;

    private int SPACE_BETWEEN = 90;

    public CardGame(){
        this.hand = new ArrayList<Card>();
        this.xPos = new int[this.hand.size()];
        this.selectedCards = new ArrayList<Integer>();
        this.myLastPlayed = new ArrayList<Card>();
    }

    /** Gets current hand.
     *
     * @return arraylist of cards
     */
    public ArrayList<Card> getHand(){
        return hand;
    }

    /** Updates the hand and repaints it.
     *
     * @param uncastedHand arraylist of cards
     */
    public void updateHand(ArrayList<?> uncastedHand){

        ArrayList<Card> hand = new ArrayList<Card>();
        for(Object object : uncastedHand){
            hand.add((Card) object);
        }

        this.hand = hand;
        this.xPos = new int[this.hand.size()];
        Collections.sort(this.hand);
        repaint();
    }

    /** Resets the instance of lastPlayed.
     *
     */
    public void resetMyLastPlayed(){
        this.myLastPlayed = new ArrayList<Card>();
    }

    /** Returns cards last played.
     *
     * @return arraylist of cards
     */
    public ArrayList<Card> getMyLastPlayed(){
        return this.myLastPlayed;
    }

    /** Checks the size of the player's hand.
     *
     * @return size of hand (int)
     */
    public int getHandSize(){
        return this.hand.size();
    }

    /** Highlights cards based on selection.
     *
     * @param num position of card
     */
    public void selectCard(int num){
        if(!this.selectedCards.contains(num)) {
            if (this.selectedCards.size() < 5) {

                this.selectedCards.add(num);
                repaint();

            } else {
                System.out.println("Max Number of Selected Cards.");
            }
        }else{
            this.selectedCards.remove(new Integer(num));
            repaint();
        }

        Collections.sort(this.selectedCards);
        Collections.reverse(this.selectedCards);
    }

    public ArrayList<Integer> getSelectedCards(){
        return this.selectedCards;
    }

    public int getXPos(int num) throws Exception{
        return this.xPos[num];
    }

    /** Paints the frame.
     *
     * @param g graphics
     */
    public void paint(Graphics g) {
        if (!hand.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g;

            CardDrawer cd = new CardDrawer();

            for (int i = 0; i < this.hand.size(); i++) {

                if (hand.get(i) != null) {

                    String value = "" + hand.get(i).getCardValue();

                    boolean border = false;

                    if (this.selectedCards.contains(i)) {
                        border = true;
                    }

                    if (i != 0) {
                        cd.draw(g2, value, ("" + this.hand.get(i).getSuit()).toLowerCase(), SPACE_BETWEEN, border);
                        this.xPos[i] = SPACE_BETWEEN + this.xPos[i - 1];
                    } else {
                        int tempSpaceBtwn = 10;
                        cd.draw(g2, value, ("" + this.hand.get(i).getSuit()).toLowerCase(), tempSpaceBtwn, border);
                        this.xPos[i] = tempSpaceBtwn;
                    }
                }

            }
        }
    }

}
