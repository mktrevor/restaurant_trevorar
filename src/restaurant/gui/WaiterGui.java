package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class WaiterGui implements Gui {
	
	private static final int XPOS = -30, YPOS = -30;
	
	private boolean moving = false;

    private WaiterAgent agent = null;

    private int xPos = XPOS, yPos = YPOS;//default waiter position
    private int xDestination = XPOS, yDestination = YPOS;//default start position

    public WaiterGui(WaiterAgent agent) {
        this.agent = agent;
        
        //Map for table locations
        tableLocations.put(new Integer(1), new Dimension(200, 200));
        tableLocations.put(new Integer(2), new Dimension(450, 200));
        tableLocations.put(new Integer(3), new Dimension(200, 400));
        tableLocations.put(new Integer(4), new Dimension(450, 400));
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
        
        if(xPos == xDestination && yPos == yDestination && moving == true) {
        	moving = false;
        	agent.msgAtDestination();
        }
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, 30, 30);

        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("W", xPos + 5, yPos + 22);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent c, int table) {
    	xDestination = (int) tableLocations.get(table).getWidth() + 30;
		yDestination = (int) tableLocations.get(table).getHeight() - 30;
		moving = true;
        
        GiveTableNumberToCustomerGui(c, table);
    }
    
    public void GiveTableNumberToCustomerGui(CustomerAgent c, int table) {
    	c.getGui().GivenTableNumber(table);
    }
    
    public void DoGoToTable(int table) {
    	xDestination = (int) tableLocations.get(table).getWidth() + 30;
		yDestination = (int) tableLocations.get(table).getHeight() - 30;
		moving = true;
    }
    
    public void DoGoToCook() {
    	xDestination = 630;
    	yDestination = 630;
    	moving = true;
    }

    public void DoLeaveCustomer() {
        xDestination += 60;
        yDestination -= 60;
    }
}
