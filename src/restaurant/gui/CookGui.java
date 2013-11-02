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
	private final int FRIDGE_Y = 440;
	private final int GRILL_X = 720, GRILL_Y = 320;
	private final int COUNTER_X = 680, COUNTER_Y = 340;
	
	private boolean moving;
	
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
        
        if(xPos == xDestination && yPos == yDestination && moving == true) {
        	moving = false;
        	agent.msgAtDestination();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(xPos, yPos, 30, 30); // Size/position of gui
        g.setColor(Color.BLACK);
        g.drawRect(xPos - 1, yPos - 1, 31, 31);
        g.drawRect(xPos - 2, yPos - 2, 33, 33);

        Font font = new Font("Arial", Font.BOLD, 12);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString("Cook", xPos + 1, yPos + 20);   
        
        // Drawing the cooking area
        g.setColor(Color.GRAY);
        g.fillRect(750, 250, 40, 200);
        
        g.setColor(Color.BLACK);
        g.fillRect(755, 255, 30, 190);
        
        // Drawing the waiting area
        g.setColor(Color.DARK_GRAY);
        g.fillRect(650, 250, 40, 200);
        
        // Drawing the refrigerator
        g.setColor(Color.WHITE);
        g.fillRect(690, 470, 60, 50);
        g.setColor(Color.BLACK);
        g.drawRect(689, 469, 61, 51);
        g.drawString("Fridge", 700, 495);
        
        for(MyOrder o : orders) {
        	if(o.s == orderState.cooking) {
        		drawOrderCooking(o, g);
        	} else if (o.s == orderState.carried) {
        		drawOrderCarried(o, g);
        	} else if (o.s == orderState.waiting) {
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
    
    public void drawOrderCarried(MyOrder o, Graphics2D g) {
    	int xLoc = xPos + 5;
    	int yLoc = yPos + 25;
    	
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
    
    public void msgOrderCooking(int number) {
    	for(MyOrder o : orders) {
    		if(o.orderNumber == number) {
    			o.s = orderState.cooking;
    		}
    	}
    }
    
    public void msgOrderWaiting(int number) {
    	for(MyOrder o : orders) {
    		if(o.orderNumber == number) {
    			o.s = orderState.waiting;
    			o.setLoc(waitingLocation);
    			waitingLocation = (waitingLocation + 1) % 5;
    		}
    	}
    }
    
    public void msgOrderBeingCarried(int number) {
    	for(MyOrder o : orders) {
    		if(o.orderNumber == number) {
    			o.s = orderState.carried;
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
    
    public void DoGoToHome() {
    	xDestination = XPOS;
    	yDestination = YPOS;
    }
    
    public void DoGoToFridge() {
    	xDestination = XPOS;
    	yDestination = FRIDGE_Y;
    	moving = true;
    }
    
    public void DoGoToGrill() {
    	xDestination = GRILL_X;
    	yDestination = GRILL_Y;
    	moving = true;
    }
    
    public void DoGoToCounter() {
    	xDestination = COUNTER_X;
    	yDestination = COUNTER_Y;    	
    	moving = true;
    }
    
    class MyOrder {
    	String type;
    	orderState s = orderState.carried;
    	int orderNumber;
    	int location;
    	
    	MyOrder(String type, int orderNumber, int location) {
    		this.type = type;
    		this.orderNumber = orderNumber;
    		this.location = location;
    	}
    	
    	void setLoc(int x) {
    		location = x;
    	}
    }
    
    private enum orderState { carried, cooking, waiting };
}
