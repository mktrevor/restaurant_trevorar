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
	private static final int WIDTH = 30, HEIGHT = 30;
	private final int COOK_X = 620, COOK_Y = 320;
	private final int CASHIER_X = 80, CASHIER_Y = 300;
	private final int BREAK_X = 50, BREAK_Y = 150;
	
	private boolean moving = false;
	private boolean carryingFood = false;
	private boolean onBreak = false;
	
	private int cycleCount = 0;

    private WaiterAgent agent = null;
    
    String currentFood;

    private int xPos = XPOS, yPos = YPOS;//default waiter position
    private int xDestination = XPOS, yDestination = YPOS;//default start position

    public WaiterGui(WaiterAgent agent) {
        this.agent = agent;

        // Initial mapping of table locations!
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
        g.setColor(Color.BLACK); // Waiter gui is a black 30x30 square
        g.fillRect(xPos, yPos, WIDTH, HEIGHT);

        // This draws a "W" on the waiter gui
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("W", xPos + 5, yPos + 22);
        
        if(carryingFood) {
        	// If the waiter is delivering food, this code adds the food to the waiter's animation
        	g.setColor(Color.WHITE);
        	g.fillRect(xPos - 20, yPos + 10, 20, 20);

            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.setColor(Color.BLACK);
            String choiceLetter = currentFood.substring(0,2); // First two letters of current food
            g.drawString(choiceLetter, xPos -16, yPos + 25);
        }
        
        if(onBreak) {
        	g.setColor(Color.WHITE);
        	g.fillOval(xPos - 10, yPos - 15, 50, 20);
        	
        	g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(Color.BLACK);
            
            if(cycleCount <= 25) {
            	g.drawString("zzz", xPos, yPos);
            } else if(cycleCount > 25 && cycleCount <= 50) {
            	g.drawString("zzz.", xPos, yPos);
            } else if(cycleCount > 50 && cycleCount <= 75) {
            	g.drawString("zzz..", xPos, yPos);
            } else if(cycleCount < 100) {
            	g.drawString("zzz...", xPos, yPos);
            }
            
            cycleCount = (cycleCount + 1) % 100;
        }
    }

    public boolean isPresent() {
        return true;
    }
    
    public void takeBreak() {
    	
    }

    public void DoBringToTable(CustomerAgent c, int table) {
    	xDestination = (int) tableLocations.get(table).getWidth() + WIDTH;
		yDestination = (int) tableLocations.get(table).getHeight() - HEIGHT;
		moving = true;
        
        GiveTableNumberToCustomerGui(c, table);
    }
    
    public void GiveTableNumberToCustomerGui(CustomerAgent c, int table) {
    	c.getGui().GivenTableNumber(table);
    }
    
    public void DoGoToTable(int table) {
    	xDestination = (int) tableLocations.get(table).getWidth() + WIDTH;
		yDestination = (int) tableLocations.get(table).getHeight() - HEIGHT;
		moving = true;
    }
    
    public void DoGoToLobby() {
    	xDestination = XPOS;
    	yDestination = YPOS;
    	moving = true;
    }
    
    public void DoGoToBreakZone() {
    	xDestination = BREAK_X;
    	yDestination = BREAK_Y;
    	moving = true;
    }
    
    public void DoGoToCook() {
    	xDestination = COOK_X;
    	yDestination = COOK_Y;
    	moving = true;
    }
    
    public void DoGoToCashier() {
    	xDestination = CASHIER_X;
    	yDestination = CASHIER_Y;
    	moving = true;
    }

    public void DoLeaveCustomer() {
        xDestination += 2 * WIDTH;
        yDestination -= 2 * HEIGHT;
    }
    
    public void msgBreakStarted() {
    	onBreak = true;
    }
    
    public void msgBreakFinished() {
    	onBreak = false;
    }
    
    public void deliveringFood(String food) {
    	currentFood = food;
    	carryingFood = true;
    }
    
    public void foodDelivered() {
    	carryingFood = false;
    }
}
