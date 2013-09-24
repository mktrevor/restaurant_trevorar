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
	
	private boolean moving;

    private WaiterAgent agent = null;
    
    private ImageIcon waiterIcon = new ImageIcon("images/wait.png");

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
        
        if(xPos == 630 && yPos == 630) {
        	agent.msgAtCook();
        }
        
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == (int) tableLocations.get(1).getWidth() + 30) & (yDestination == (int) tableLocations.get(1).getHeight() - 30)) {
           agent.msgAtTable();
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == (int) tableLocations.get(2).getWidth() + 30) & (yDestination == (int) tableLocations.get(2).getHeight() - 30)) {
            agent.msgAtTable();
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == (int) tableLocations.get(3).getWidth() + 30) & (yDestination == (int) tableLocations.get(3).getHeight() - 30)) {
            agent.msgAtTable();
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == (int) tableLocations.get(4).getWidth() + 30) & (yDestination == (int) tableLocations.get(4).getHeight() - 30)) {
            agent.msgAtTable();
        }
    }

    /*public void draw(Graphics2D g) {
        g.drawImage(waiterIcon.getImage(), xPos, yPos, 30, 30, (ImageObserver) g);
    }*/
    
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent c, int table) {
    	xDestination = (int) tableLocations.get(table).getWidth() + 30;
		yDestination = (int) tableLocations.get(table).getHeight() - 30;
        
        GiveTableNumberToCustomerGui(c, table);
    }
    
    public void GiveTableNumberToCustomerGui(CustomerAgent c, int table) {
    	c.getGui().GivenTableNumber(table);
    }
    
    public void DoGoToTable(int table) {
    	xDestination = (int) tableLocations.get(table).getWidth() + 30;
		yDestination = (int) tableLocations.get(table).getHeight() - 30;
    }
    
    public void DoGoToCook() {
    	xDestination = 630;
    	yDestination = 630;
    }

    public void DoLeaveCustomer() {
        xDestination = -30;
        yDestination = -30;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public Image getImage() {
    	return waiterIcon.getImage();
    }
}
