package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private boolean isEating = false;
	String choice;

	private static final int WIDTH = 30, HEIGHT = 30;
	private final int CASHIER_X = 80, CASHIER_Y = 300;

	private WaiterGui waiterGui;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private enum customerState { waiting, wantsToOrder, ordered, eating, doneEating };
	private customerState state = customerState.waiting;

	public CustomerGui(CustomerAgent c, RestaurantGui gui){
		agent = c;
		xPos = -2 * WIDTH;
		yPos = -2 * HEIGHT;
		xDestination = -2 * WIDTH;
		yDestination = -2 * HEIGHT;
		this.gui = gui;
		
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
			

		if (xPos == xDestination && yPos == yDestination) {			
			if (command==Command.GoToSeat) {
				agent.msgAnimationFinishedGoToSeat();
			} 
			else if(command==Command.GoToCashier) {
				agent.msgAtDestination();
			}
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationDoneEatingFood();
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillRect(xPos, yPos, WIDTH, HEIGHT); // Position/size of customer gui
        
        // This draws a "C" on the customer gui
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString("C", xPos + 8, yPos + 22);   
        
        // Switch statement to determine if food needs to be displayed on the table
        switch(state) {
        case wantsToOrder: // This case draws a white oval with a black border that contains a "!" meaning the customer wants to order
        	g.setColor(Color.BLACK);
        	g.fillOval(xPos + 8, yPos - 22, 26, 20); // This oval becomes a border
        	g.setColor(Color.WHITE);
        	g.fillOval(xPos + 10, yPos - 20, 22, 16);

            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(Color.BLACK);
            g.drawString("!", xPos + 20, yPos - 6);
            break;
        	
        case ordered: // This case draws a white square with the ordered food's first two letters and a question mark on the table
        	g.setColor(Color.WHITE);
        	g.fillRect(xPos + 10, yPos + 30, 20, 20);

            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.setColor(Color.BLACK);
            String choiceLetter = choice.substring(0,2); // First two letters of choice
            g.drawString(choiceLetter + "?", xPos + 11, yPos + 45);
            break;
        	
        case eating: // This case draws a white square with the delivered food's first two letters on the table
        	g.setColor(Color.WHITE);
        	g.fillRect(xPos + 10, yPos + 30, 20, 20);

            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.setColor(Color.BLACK);
            String choiceLetter2 = choice.substring(0,2); // First two letters of choice
            g.drawString(choiceLetter2, xPos + 14, yPos + 45);
        	break;
        }
    }

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setHungry() {
		isHungry = true;
		agent.msgGotHungry();
		setPresent(true);
	}
	
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat() {
		command = Command.GoToSeat;
	}
	
	public void DoGoToCashier() {
    	command = Command.GoToCashier;
    	xDestination = CASHIER_X;
    	yDestination = CASHIER_Y;
    }
	
	public void GivenTableNumber(int table) {
		xDestination = (int) tableLocations.get(table).getWidth(); // X coordinate of table
		yDestination = (int) tableLocations.get(table).getHeight(); // Y coordinate of table
	}
	
	public void wantToOrder() {
		state = customerState.wantsToOrder;
	}
	
	public void orderedFood(String choice) {
		this.choice = choice;
		state = customerState.ordered;
	}

	public void startedEating(String choice) {
		state = customerState.eating;
	}
	
	public void doneEating() {
		state = customerState.doneEating;
	}
	
	public void clearOrder() {
		state = customerState.waiting;
	}
	
	public void DoExitRestaurant() {
		// These coordinates are the initial off-screen coordinates
		xDestination = -2 * WIDTH;
		yDestination = -2 * HEIGHT;
		command = Command.LeaveRestaurant;
	}
}
