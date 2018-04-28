import java.awt.*;

/**
 * Clubs drawer.
 */
public class ClubsDrawer {

    /**
     * Draws club on card.
     *
     * @param g2   the g 2
     * @param xLoc the x loc
     * @param yLoc the y loc
     */
    public void draw(Graphics2D g2, int xLoc, int yLoc){

        int sizeFactor = 5;
        int diameter = 40/sizeFactor;

        g2.setColor(Color.black);
        g2.fillOval(xLoc, yLoc + 5, diameter, diameter);
        g2.fillOval(xLoc + 40/sizeFactor, yLoc + 5, diameter, diameter);
        g2.fillOval(xLoc + 20/sizeFactor, yLoc, diameter, diameter);
        g2.fillRect(xLoc + 35/sizeFactor, yLoc + 5, 15/sizeFactor, 60/sizeFactor);


    }
}
