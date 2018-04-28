import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * PlayedComponent for drawing played screen.
 */
public class PlayedComponent extends JComponent {

    private ArrayList<Card> hand;
    private boolean passed = false;
    private int SPACE_BETWEEN = 95;
    private boolean winner = false;
    private boolean trigger = false;
    private int playerNum = 0;

    /**
     * Instantiates a new Played component.
     *
     */
    public PlayedComponent(){
        this.hand = new ArrayList<Card>();
    }

    /** Tells the component that there is a winner or loser.
     *
     * @param value
     */
    public void triggerWinner(boolean value){
        trigger = true;
        if(value){
            winner = true;
        }
        repaint();
    }

    /** Resets the image that is drawn.
     *
     */
    public void reset(){
        hand = new ArrayList<>();
        playerNum = 0;
        winner = false;
        trigger = false;
    }

    /**
     * Repaint screen.
     */
    public void updateLastPlayed(ArrayList<?> uncastedHand){

        ArrayList<Card> hand = new ArrayList<Card>();
        for(Object object : uncastedHand){
            hand.add((Card) object);
        }

        passed = false;
        if(hand.size() != 0) {
            this.hand = hand;
            repaint();
        }else{
            passed = true;
            repaint();
        }
    }

    /** Paints the screen.
     *
     * @param g
     */
    @Override
    public void paint(Graphics g){

        Graphics2D g2 = (Graphics2D) g;

        if(trigger && winner){
            g2.setColor(Color.black);
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 32));
            g2.drawString("YOU WIN!", 165, 160);
        }else if( trigger && !winner){
            g2.setColor(Color.black);
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 32));
            g2.drawString("YOU LOSE!", 160, 160);
        }else {
            if (hand.size() != 0) {
                CardDrawer cd = new CardDrawer();
                cd.changeYLoc(100);

                for (int i = 0; i < hand.size(); i++) {

                    if (hand.get(i) != null) {

                        String value = "" + hand.get(i).getCardValue();

                        boolean border = false;

                        if (i != 0) {
                            cd.draw(g2, value, ("" + hand.get(i).getSuit()).toLowerCase(), SPACE_BETWEEN, border);
                        } else {
                            int tempSpaceBtwn = 15;
                            cd.draw(g2, value, ("" + hand.get(i).getSuit()).toLowerCase(), tempSpaceBtwn, border);
                        }
                    }

                }
                if (passed) {
                    g2.setColor(Color.black);
                    g2.setFont(new Font("TimesRoman", Font.PLAIN, 20));
                    g2.drawString("Player passed.", 170, 280);
                }
            }
        }

    }
}
