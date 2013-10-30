package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.CookAgent;

import java.awt.*;
import java.util.*;
import java.util.List;

/* NOTE: This code is not used in Restaurant V2 */

public class CookGui implements Gui {
	
	private static final int XPOS = 700, YPOS = 350;

    private CookAgent agent = null;

    private int xPos = XPOS, yPos = YPOS;//default waiter position
    private int xDestination = XPOS, yDestination = YPOS;//default start position
    
    private List<MyOrder> orders = new ArrayList<MyOrder>();
    int cookingLocation = 0, waitingLocation = 0;
    
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
        
        for(MyOrder o : orders) {
        	if(o.cooking) {
        		drawOrderCooking(o, g);
        	} else {
        		drawOrderWaiting(o, g);
        	}
        }
    }

    public boolean isPresent() {
        return true;
    }
    
    public void drawOrderCooking(MyOrder o, Graphics2D g) {
    	int xLoc = 760;
    	int yLoc = 260 + o.location * 30;
    	
    	g.setColor(Color.WHITE);
    	g.fillRect(xLoc, yLoc, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        String choiceLetter = o.type.substring(0,2); // First two letters of current food
        g.drawString(choiceLetter, xLoc + 4, yLoc + 14);
    }
    
    public void drawOrderWaiting(MyOrder o, Graphics2D g) {
    	int xLoc = 655;
    	int yLoc = 260 + o.location * 30;
    	
    	g.setColor(Color.WHITE);
    	g.fillRect(xLoc, yLoc, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        String choiceLetter = o.type.substring(0,2); // First two letters of current food
        g.drawString(choiceLetter, xLoc + 4, yLoc + 14);
    }
    
    public void msgNewOrder(String type, int orderNumber) {
    	orders.add(new MyOrder(type, orderNumber, cookingLocation));
    	cookingLocation = (cookingLocation + 1) % 5;
    }
    
    public void msgOrderDoneCooking(int number) {
    	for(MyOrder o : orders) {
    		if(o.orderNumber == number) {
    			o.cooking = false;
    			o.setLoc(waitingLocation);
    			waitingLocation = (waitingLocation + 1) % 5;
    		}
    	}
    }
    
    public void msgOrderPickedUp(int number) {
    	for(MyOrder o : orders) {
    		if(o.orderNumber == number) {
    			orders.remove(o);
    		}
    	}
    }
    
    class MyOrder {
    	String type;
    	boolean cooking;
    	int orderNumber;
    	int location;
    	
    	MyOrder(String type, int orderNumber, int location) {
    		this.type = type;
    		this.orderNumber = orderNumber;
    		this.location = location;
    		cooking = true;
    	}
    	
    	void setLoc(int x) {
    		location = x;
    	}
    }
}
