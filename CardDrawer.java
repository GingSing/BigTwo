import java.awt.*;

/**
 * Card drawer.
 */
public class CardDrawer {

    private HeartsDrawer hd;
    private ClubsDrawer cd;
    private DiamondsDrawer dd;
    private SpadesDrawer sd;
    private int xLoc = 0;
    private int yLoc = 425;
    public final static int CARD_HEIGHT = 100;
    public final static int CARD_WIDTH = 75;
    private int FONT_SIZE = 30;

    /**
     * Instantiates a new Card drawer.
     */
    public CardDrawer(){

        this.hd = new HeartsDrawer();
        this.cd = new ClubsDrawer();
        this.dd = new DiamondsDrawer();
        this.sd = new SpadesDrawer();

    }

    /**
     * Change y location.
     *
     * @param yLoc the y loc
     */
    public void changeYLoc(int yLoc){

        this.yLoc = yLoc;

    }

    /**
     * Get x location.
     *
     * @return the int
     */
    public int getXLoc(){
        return this.xLoc;
    }

    /**
     * Get y location.
     *
     * @return the int
     */
    public int getYLoc(){
        return this.yLoc;
    }

    /**
     * Draw card.
     *
     * @param g2           the g 2
     * @param value        the value
     * @param suit         the suit
     * @param spaceBetween the space between
     * @param border       the border
     */
    public void draw(Graphics2D g2, String value, String suit, int spaceBetween, boolean border){

        if (value.equals("11")) {
            value = "J";
        } else if (value.equals("12")) {
            value = "Q";
        } else if (value.equals("13")) {
            value = "K";
        } else if (value.equals("14")) {
            value = "A";
        } else if (value.equals("15")) {
            value = "2";
        }

        this.xLoc += spaceBetween;

        Rectangle rect = new Rectangle(xLoc, yLoc, CARD_WIDTH, CARD_HEIGHT);

        if(border == true){
            g2.setColor(new Color(43, 111, 174));
            int thickness = 10;
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(rect);
            g2.setStroke(oldStroke);
        }

        g2.setColor(Color.white);
        g2.fill(rect);
        g2.setColor(Color.black);
        g2.draw(rect);

        g2.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
        if(value.equals("10")) {
            g2.drawString(value, xLoc + 20, yLoc + 60);
        }else {
            g2.drawString(value, xLoc + 29, yLoc + 60);
        }

        int suitX = xLoc + 2;
        int suitY = yLoc + 2;

        if(suit.equals("hearts")){
            hd.draw(g2, suitX, suitY);
            hd.draw(g2, suitX + CARD_WIDTH - 22, suitY + CARD_HEIGHT - 20);
        }else if(suit.equals("clubs")){
            cd.draw(g2, suitX, suitY);
            cd.draw(g2, suitX + CARD_WIDTH - 20, suitY + CARD_HEIGHT - 22);
        }else if(suit.equals("diamonds")){
            dd.draw(g2, suitX, suitY);
            dd.draw(g2, suitX + CARD_WIDTH - 18, suitY + CARD_HEIGHT - 21);
        }else if(suit.equals("spades")){
            sd.draw(g2, suitX, suitY);
            sd.draw(g2, suitX + CARD_WIDTH - 19, suitY + CARD_HEIGHT - 22);
        }

    }

}
