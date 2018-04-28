import java.awt.*;

/**
 * Spades drawer.
 */
public class SpadesDrawer {

    /**
     * Draws spade on card.
     *
     * @param g2   the g 2
     * @param xLoc the x loc
     * @param yLoc the y loc
     */
    public void draw(Graphics2D g2, int xLoc, int yLoc){

        int sizeFactor = 5;
        int diameter = 40/sizeFactor;

        g2.setColor(Color.black);
        g2.fillOval(xLoc + 4/sizeFactor, yLoc + 5, diameter, diameter);
        g2.fillOval(xLoc + 39/sizeFactor, yLoc + 5, diameter, diameter);
        g2.fillRect(xLoc + 35/sizeFactor, yLoc + 5, 15/sizeFactor, 60/sizeFactor);
        g2.fillPolygon(new int[]{xLoc + 5/sizeFactor, xLoc + 75/sizeFactor, xLoc + 44/sizeFactor}, new int[]{yLoc + 35/sizeFactor, yLoc + 35/sizeFactor, yLoc + 0/sizeFactor}, 3);



    }

}
