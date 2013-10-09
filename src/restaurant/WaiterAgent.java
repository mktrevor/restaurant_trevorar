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
		askedForOrder, ordered, orderSentToCook, orderOut, foodReady, served, finished, 
		leftRestaurant };
		
	private HostAgent host;
	private CookAgent cook;

	private String name;
	private Semaphore atDestination = new Semaphore(0, true);
	private Semaphore customerAtTable = new Semaphore(0, true);
	
	private enum waiterState { working, onBreak };
	private waiterState state;
	
	private enum waiterEvent { none, backToWork, takeABreak };
	private waiterEvent event = waiterEvent.none;
	
	public WaiterGui waiterGui = null;

	public WaiterAgent(String name) {
		super();

		this.state = waiterState.working;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setHost(HostAgent h) {
		this.host = h;
	}
	
	public void setCook(CookAgent c) {
		this.cook = c;
	}
	
	// Messages
	
	public void msgPleaseSeatCustomer(HostAgent h, CustomerAgent c, int table) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.s = customerState.waiting;
				mc.table = table;
				stateChanged();
				return;
			}
		}
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
	
	public void msgOutOf(String choice, int table) {
		for(MyCustomer mc : customers) {
			if(mc.table == table) {
				mc.s = customerState.orderOut;
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
	
	public void msgSorryNoBreakNow() {
		print("This is inhumane!!! I deserve a raise!");
	}
	
	public void msgFinishUpAndTakeABreak() {
		event = waiterEvent.takeABreak;
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
			if(mc.s == customerState.orderOut) {
				removeChoice(mc);
				askToReorder(mc);
				return true;
			}
		}
		for(MyCustomer mc : customers) {
			if(mc.s == customerState.ordered) {
				sendOrderToCook(mc);
				return true;
			}
		}
		
		if(event == waiterEvent.takeABreak && state == waiterState.working) {
			state = waiterState.onBreak;
			takeABreak();
			return true;
		}
		
		if(event == waiterEvent.backToWork && state == waiterState.onBreak) {
			state = waiterState.working;
			event = waiterEvent.none;
			return true;
		}

		return false;
	}

	// Actions

	private void seatCustomer(MyCustomer c) {
		
		waiterGui.DoGoToLobby();
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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

	private void removeChoice(MyCustomer c) {
		
		print("Sorry, we're out of " + c.choice + ".");
		c.c.msgRemoveFromMenu(c.choice);
	}
	
	private void askToReorder(MyCustomer c) {
		waiterGui.DoGoToTable(c.table);
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		c.s = customerState.askedForOrder;
		
		print("Taking order from: " + c.c.getName());
		c.c.msgPleaseReorder();

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
		
		print("Here is your " + c.choice + ".");
		c.s = customerState.served;
		waiterGui.foodDelivered();
		c.c.msgHereIsYourFood(c.choice);

		DoLeaveCustomer();
	}
	
	private void tellHostCustomerIsDone(MyCustomer c) {
		c.s = customerState.leftRestaurant;
		host.msgTableIsFree(c.table, this);
		print("Table " + c.table + " is free!");
		c.table = 0; // Customer is no longer at one of the 4 tables
		

		//HACK - always wants a break!
		if(name.equals("break") && state != waiterState.onBreak) {
			host.msgIWantABreak(this);
			print("Give me a break!");
		}
		//******//
	}
	
	private void takeABreak() {
		waiterGui.DoGoToBreakZone();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				 finishBreak();
			}
		}, 10000 );
	}
	
	private void finishBreak() {
		event = waiterEvent.backToWork;
		print("Alright, I finished my break!");
		host.msgImDoneWithMyBreak(this);
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, int table) {
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
		
		Check check;
		
		MyCustomer(CustomerAgent c, int table, customerState state) {
			this.c = c;
			this.table = table;
			this.s = state;
		}
	}
	public class Check {
		CustomerAgent cust;
		String choice;
		double amount;
	}
}

