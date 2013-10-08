package restaurant.gui;


import restaurant.CashierAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.CookAgent;

import java.awt.*;

/* NOTE: This code is not used in Restaurant V2 */

public class CashierGui implements Gui {
	
	private static final int XPOS = -30, YPOS = -30;
	private static final int XTABLE = 200, YTABLE = 250;

    private CashierAgent agent = null;

    private int xPos = XPOS, yPos = YPOS;//default waiter position
    private int xDestination = XPOS, yDestination = YPOS;//default start position

    public static final int xTable = XTABLE;
    public static final int yTable = YTABLE;

    public CashierGui(CashierAgent agent) {
        this.agent = agent;
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(xPos, yPos, 30, 30); // Size/position of gui
    }

    public boolean isPresent() {
        return true;
    }
}
