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
	private static final int XTABLE = 200, YTABLE = 200;

    private WaiterAgent agent = null;
    
    private ImageIcon waiterIcon = new ImageIcon("images/wait.png");

    private int xPos = XPOS, yPos = YPOS;//default waiter position
    private int xDestination = XPOS, yDestination = YPOS;//default start position

    public static final int xTable = XTABLE;
    public static final int yTable = YTABLE;

    public WaiterGui(WaiterAgent agent) {
        this.agent = agent;
        
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
        
        if(xPos == XPOS && yPos == YPOS) {
        	agent.msgInLobby();
        }

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == 2 * xTable + 20) & (yDestination == yTable - 20)) {
            agent.msgAtTable();
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == 2 * yTable - 20)) {
            agent.msgAtTable();
        } else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == 2 * xTable + 20) & (yDestination == 2 * yTable - 20)) {
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

    public void DoBringToTable(CustomerAgent customer, int tableNumber) {
        switch(tableNumber) {
        case 1: 
        	xDestination = xTable + 20;
            yDestination = yTable - 20;
            break;
            
        case 2:
        	xDestination = 3 * xTable + 20;
            yDestination = yTable - 20;
            break;
        	
        case 3:
        	xDestination = xTable + 20;
            yDestination = 2 * yTable - 20;
            break;
        	
        case 4:
        	xDestination = 2 * xTable + 20;
            yDestination = 2 * yTable - 20;
            break;
        }
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
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
