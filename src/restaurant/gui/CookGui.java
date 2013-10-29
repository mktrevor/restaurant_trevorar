package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.CookAgent;

import java.awt.*;

/* NOTE: This code is not used in Restaurant V2 */

public class CookGui implements Gui {
	
	private static final int XPOS = 700, YPOS = 350;

    private CookAgent agent = null;

    private int xPos = XPOS, yPos = YPOS;//default waiter position
    private int xDestination = XPOS, yDestination = YPOS;//default start position
    
    List<MyOrder> orders = new ArrayList<MyOrder>();

    public CookGui(CookAgent agent) {
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

        Font font = new Font("Arial", Font.BOLD, 12);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString("Cook", xPos, yPos + 20);   
        
        g.setColor(Color.GRAY);
        g.fillRect(750, 250, 40, 200);
        
        g.setColor(Color.BLACK);
        g.fillRect(755, 255, 30, 190);
        
        g.setColor(Color.DARK_GRAY);
        g.fillRect(650, 250, 40, 200);
    }

    public boolean isPresent() {
        return true;
    }
}
