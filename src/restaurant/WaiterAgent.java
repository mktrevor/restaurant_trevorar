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
		askedForOrder, ordered, orderSentToCook, orderOut, foodReady, served, checkReady, checkGiven, finished, 
		leftRestaurant };
		
	private HostAgent host;
	private CookAgent cook;
	
	private CashierAgent cashier;
	
	private enum breakState { none, wantABreak, askedForBreak, onBreak, doneWithBreak };
	breakState breakStatus = breakState.none;

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
	
	public void setCashier(CashierAgent c) {
		this.cashier = c;
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
	
	public void msgHereIsCheck(Check c) {
		for(MyCustomer mc : customers) {
			if(mc.c == c.cust) {
				mc.check = c;
				mc.s = customerState.checkReady;
			}
		}
		stateChanged();
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
	
	public void msgIWantABreak() {
		breakStatus = breakState.wantABreak;
		stateChanged();
	}
	
	public void msgSorryNoBreakNow() {
		print("No break?! This is inhumane!");
		breakStatus = breakState.none;
		stateChanged();
	}
	
	public void msgFinishUpAndTakeABreak() {
		event = waiterEvent.takeABreak;
		stateChanged();
	}
	
	public void msgBreakIsFinished() {
		breakStatus = breakState.doneWithBreak;
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
		
		if(breakStatus == breakState.doneWithBreak) {
			finishBreak();
		}
		
		if(breakStatus == breakState.wantABreak) {
			askForBreak();
		}
	
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
			if(mc.s == customerState.checkReady) {
				getCheckFromCashier(mc);
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
		
		if(event == waiterEvent.takeABreak && state == waiterState.working && allCustomersDone()) {
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
		
		print("Welcome to Restaurant V2.1! Here is your seat.");
		
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

		cashier.msgProduceCheck(this, c.c, c.choice);
		print("Cashier, can you prepare a check for this customer?");

		DoLeaveCustomer();
	}
	
	private void tellHostCustomerIsDone(MyCustomer c) {
		c.s = customerState.leftRestaurant;
		host.msgTableIsFree(c.table, this);
		print("Table " + c.table + " is free!");
		c.table = 0; // Customer is no longer at one of the 4 tables
	}
	
	private void askForBreak() {
		print("Could I please have a break?!");
		host.msgIWantABreak(this);
		breakStatus = breakState.askedForBreak;
	}
	
	private void takeABreak() {
		waiterGui.DoGoToBreakZone();
		breakStatus = breakState.onBreak;
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.msgBreakStarted();
	}
	
	private void finishBreak() {
		waiterGui.msgBreakFinished();
		event = waiterEvent.backToWork;
		breakStatus = breakState.none;
		print("Alright, I finished my break!");
		host.msgImDoneWithMyBreak(this);
	}
	
	private void getCheckFromCashier(MyCustomer c) {
		waiterGui.DoGoToCashier();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		giveCustomerCheck(c);
	}
	
	private void giveCustomerCheck(MyCustomer c) {
		waiterGui.DoGoToTable(c.table);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
		print("Here is your check!");
		c.c.msgHereIsYourBill(c.check);
		c.s = customerState.checkGiven;
		
		DoLeaveCustomer();
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

	private boolean allCustomersDone() {
		for(MyCustomer c : customers) {
			if(c.s != customerState.finished && c.s != customerState.leftRestaurant) {
				return false;
			}
		}
		return true;
	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}

	public boolean wantsToTakeBreak() {
		return event == waiterEvent.takeABreak || breakStatus == breakState.wantABreak;
	}
	
	public boolean isOnBreak() {
		return state == waiterState.onBreak;
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
}

