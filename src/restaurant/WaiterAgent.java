package restaurant;

import agent.Agent;
import agent.Constants.Food;
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
		askedForOrder, ordered, foodReady, served, finished, 
		leftRestaurant };
		
	private HostAgent host;

	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	/* CHECK THIS LINE OUT */
	private enum waiterState {free, doingStuff};
	private waiterState state = waiterState.free;
	
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
	
	// Messages
	
	public void msgPleaseSeatCustomer(CustomerAgent c, int table) {
		customers.add(new MyCustomer(c, table, customerState.waiting));
		stateChanged();
	}

	public void msgImReadyToOrder(CustomerAgent c) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.s = customerState.readyToOrder;
			}
		}
		stateChanged();
	}

	public void msgAtTable() {//from animation
		print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgHereIsMyChoice(CustomerAgent c, String choice) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.s = customerState.ordered;
				mc.choice = choice;
			}
		}
		stateChanged();
	}
	
	public void msgOrderDone(String choice, int table) {
		for(MyCustomer mc : customers) {
			if(mc.table == table) {
				mc.s = customerState.foodReady;
				if(choice != mc.choice) {
					print("WHOOPS, THIS IS THE WRONG FOOD!");
				}
			}
		}
		stateChanged();
	}
	
	public void msgImDoneEating(CustomerAgent c) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.s = customerState.finished;
			}
		}
		stateChanged();
	}
	
	public void msgInLobby() {
		if(state == waiterState.doingStuff) {
			state = waiterState.free;
			stateChanged();
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for(MyCustomer mc : customers) {
			if(mc.s == customerState.waiting) {
				seatCustomer(mc);
			}
			else if(mc.s == customerState.readyToOrder) {
				takeOrder(mc);
			}
			else if(mc.s == customerState.ordered) {
				sendOrderToCook(mc.choice);
			}
			else if(mc.s == customerState.foodReady) {
				bringFoodToCustomer(mc);
			}
			else if(mc.s == customerState.finished) {
				tellHostCustomerIsDone(mc);
			}
			else {
				//STUB - walk around!
				//DoLeaveCustomer();
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(MyCustomer c) {
		state = waiterState.doingStuff;
		
		c.c.msgFollowMe(this, new Menu());
		//(host code)
		/*customer.msgSitAtTable(table.tableNumber);
		DoSeatCustomer(customer, table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		hostGui.DoLeaveCustomer();*/
		
		//STUB
	}
	
	private void takeOrder(MyCustomer c) {
		//STUB
	}
	
	private void sendOrderToCooke(MyCustomer c) {
		
	}
	
	private void bringFoodToCustomer(MyCustomer c) {
		//STUB
	}
	
	private void tellHostCustomerIsDone(MyCustomer c) {
		//STUB
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, int table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		waiterGui.DoBringToTable(customer, table);

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
		
		// Getters
		public CustomerAgent getC() {
			return c;
		}
		
		public String getChoice() {
			return choice;
		}
		
		public customerState getS() {
			return s;
		}
		
		public int getTable() {
			return table;
		}
		
		// Setters
		public void setChoice(String choice) {
			this.choice = choice;
		}
		
		public void setS(CustomerState s) {
			this.s = s;
		}
		
		public void setTable(int table) {
			this.table = table;
		}
	}
}

