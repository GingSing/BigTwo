import java.awt.*;

/**
 * Hearts drawer.
 */
public class HeartsDrawer {

    /**
     * Draws heart on card.
     *
     * @param g2   the g 2
     * @param xLoc the x loc
     * @param yLoc the y loc
     */
    public void draw(Graphics2D g2, int xLoc, int yLoc){
        
        int sizeFactor = 5;
        int diameter = 50/ sizeFactor;

        g2.setColor(Color.red);
        g2.fillOval(xLoc, yLoc, diameter, diameter);
        g2.fillOval(xLoc + 40/sizeFactor, yLoc, diameter, diameter);
        g2.fillPolygon(new int[]{xLoc + 1/sizeFactor, xLoc + 89/sizeFactor, xLoc + 45/sizeFactor}, new int[]{yLoc + 35/sizeFactor, yLoc + 35/sizeFactor, yLoc + 80/sizeFactor}, 3);

    }
}
