package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;
import restaurant.gui.WaiterGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class WaiterAgent extends Agent {
	public List<MyCustomer> customers
	= new ArrayList<MyCustomer>();
	
	private static enum customerState { waiting, seated, readyToOrder, 
		askedForOrder, ordered, orderSentToCook, foodReady, served, finished, 
		leftRestaurant };
		
	private HostAgent host;
	private CookAgent cook;

	private String name;
	private Semaphore atDestination = new Semaphore(0, true);
	private Semaphore customerAtTable = new Semaphore(0, true);
	
	public WaiterGui waiterGui = null;

	public WaiterAgent(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List getCustomers() {
		return customers;
	}
	
	public void setHost(HostAgent h) {
		this.host = h;
	}
	
	public void setCook(CookAgent c) {
		this.cook = c;
	}
	
	// Messages
	
	public void msgPleaseSeatCustomer(HostAgent h, CustomerAgent c, int table) {
		customers.add(new MyCustomer(c, table, customerState.waiting));
		stateChanged();
	}

	public void msgImReadyToOrder(CustomerAgent c) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.s = customerState.readyToOrder;
			}
		stateChanged();
		}
	}
	
	public void msgHereIsMyChoice(CustomerAgent c, String choice) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.s = customerState.ordered;
				mc.choice = choice;
				print("One " + choice + ", coming right up!");
			}
			stateChanged();
		}
	}
	
	public void msgOrderDone(String choice, int table) {
		for(MyCustomer mc : customers) {
			if(mc.table == table) {
				mc.s = customerState.foodReady;
			}
			stateChanged();
		}
	}
	
	public void msgImDoneEating(CustomerAgent c) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.s = customerState.finished;
			}
		}
		stateChanged();
	}
	
	public void msgAtDestination() {
		atDestination.release();
		stateChanged();
	}
	
	public void msgCustomerSatDown() {
		customerAtTable.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {		
	
		for(MyCustomer mc : customers) {
			if(mc.s == customerState.finished) {
				tellHostCustomerIsDone(mc);
				return true;
			}
		}
		for(MyCustomer mc : customers) {
			if(mc.s == customerState.foodReady) {
				bringFoodToCustomer(mc);
				return true;
			}
		}
		for(MyCustomer mc : customers) {
			if(mc.s == customerState.waiting) {
				seatCustomer(mc);
				return true;
			}
		}
		for(MyCustomer mc : customers) {
			if(mc.s == customerState.readyToOrder) {
				takeOrder(mc);
				return true;
			}
		}
		for(MyCustomer mc : customers) {
			if(mc.s == customerState.ordered) {
				sendOrderToCook(mc);
				return true;
			}
		}

		return false;
	}

	// Actions

	private void seatCustomer(MyCustomer c) {
		
		c.c.msgFollowMe(this, new Menu());
		
		DoSeatCustomer(c.c, c.table);
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		print("Welcome to Restaurant V2! Here is your seat.");
		
		try {
			customerAtTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.s = customerState.seated;

		DoLeaveCustomer();
	}
	
	private void takeOrder(MyCustomer c) {
		waiterGui.DoGoToTable(c.table);
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.s = customerState.askedForOrder;
		print("Taking order from: " + c.c.getName());
		c.c.msgWhatDoYouWant();

		DoLeaveCustomer();
	}
	
	private void sendOrderToCook(MyCustomer c) {
		print("Sending " + c.c.getName() + "'s order of " + c.choice + " to cook wirelessly. Isn't technology great?");
		
		c.s = customerState.orderSentToCook;
		cook.msgHereIsOrder(this, c.choice, c.table);
	}
	
	private void bringFoodToCustomer(MyCustomer c) {
		print("Getting food from cook.");
		waiterGui.DoGoToCook();
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		waiterGui.deliveringFood(c.choice);
		waiterGui.DoGoToTable(c.table);
		
		print("Bringing food to table " + c.table);
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.s = customerState.served;
		print("Here is your " + c.choice + ".");
		waiterGui.foodDelivered();
		c.c.msgHereIsYourFood(c.choice);

		DoLeaveCustomer();
	}
	
	private void tellHostCustomerIsDone(MyCustomer c) {
		c.s = customerState.leftRestaurant;
		host.msgTableIsFree(c.table, this);
		print("Table " + c.table + " is free!");
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, int table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		waiterGui.DoBringToTable(customer, table);
	}
	
	private void DoLeaveCustomer() {
		waiterGui.DoLeaveCustomer();
	}

	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}

	private class MyCustomer {
		CustomerAgent c;
		int table;
		customerState s;
		String choice;
		
		MyCustomer(CustomerAgent c, int table, customerState state) {
			this.c = c;
			this.table = table;
			this.s = state;
		}
	}
	
}

