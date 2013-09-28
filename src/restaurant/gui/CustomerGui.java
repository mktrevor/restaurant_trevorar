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

	private WaiterGui waiterGui;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;

	private final int CUSTOMER_WIDTH = 20, CUSTOMER_LENGTH = 20;

	public CustomerGui(CustomerAgent c, RestaurantGui gui){ //HostAgent m) {
		agent = c;
		xPos = -60;
		yPos = -60;
		xDestination = -60;
		yDestination = -60;
		//maitreD = m;
		this.gui = gui;
		
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
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationDoneEatingFood();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(xPos, xPos, 30, 30);
        
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("C", xPos + 8, yPos + 22);
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
	
	public void GivenTableNumber(int table) {//later you will map seatnumber to table coordinates.
		xDestination = (int) tableLocations.get(table).getWidth(); // X coordinate of table
		yDestination = (int) tableLocations.get(table).getHeight(); // Y coordinate of table
	}

	public void startedEating(String choice) {
		isEating = true;
	}
	
	public void doneEating() {
		isEating = false;
	}
	
	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
	
	public boolean getEating() {
		return isEating;
	}
	
	public Image getFoodImage() {
		return foodIcon.getImage();
	}
	
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public Image getImage() {
    	return customerIcon.getImage();
    }
}
