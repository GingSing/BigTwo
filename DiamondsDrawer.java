import java.awt.*;

/**
 * Diamonds drawer.
 */
public class DiamondsDrawer {

    /**
     * Draws diamond on card.
     *
     * @param g2   the g 2
     * @param xLoc the x loc
     * @param yLoc the y loc
     */
    public void draw(Graphics2D g2, int xLoc, int yLoc){

        int sizeFactor = 5;

        g2.setColor(Color.red);

        g2.fillPolygon(new int[]{xLoc + 5/sizeFactor, xLoc + 37/sizeFactor, xLoc + 69/sizeFactor, xLoc + 37/sizeFactor},
                new int[]{yLoc + 45/sizeFactor, yLoc + 5/sizeFactor, yLoc + 45/sizeFactor, yLoc + 85/sizeFactor}, 4);

    }

}
