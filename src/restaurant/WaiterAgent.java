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
	static final int NTABLES = 4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> customers
	= new ArrayList<MyCustomer>();
	
	public static enum CustomerState { waiting, seated, readyToOrder, 
		askedForOrder, ordered, foodReady, served, finished, 
		leftRestaurant };
		
	public HostAgent host;

	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	private enum waiterState {free, seatingCustomer};
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
		//Stub
	}

	public void msgIWantFood(CustomerAgent c) {
		customers.add(new MyCustomer(c));
		stateChanged();
	}

	public void msgImReadyToOrder(CustomerAgent c) {
		//STUB
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgHereIsMyChoice(CustomerAgent c, String choice) {
		//STUB
	}
	
	public void msgOrderDone(String choice, int table) {
		//STUB
	}
	
	public void msgImDoneEating(CustomerAgent c) {
		//STUB
	}
	
	
	
	
	public void msgInLobby() {
		if(state == waiterState.seatingCustomer) {
			state = waiterState.free;
			stateChanged();
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for(MyCustomer mc : customers) {
			if(mc.getS() == CustomerState.waiting) {
				//STUB
			}
			else if(mc.getS() == CustomerState.readyToOrder) {
				//STUB
			}
			else if(mc.getS() == CustomerState.ordered) {
				//STUB
			}
			else if(mc.getS() == CustomerState.foodReady) {
				//STUB
			}
			else if(mc.getS() == CustomerState.finished) {
				//STUB
			}
			else {
				//STUB - walk around!
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(CustomerAgent customer) {
		state = waiterState.seatingCustomer; // Check state!
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
		CustomerState s;
		String choice;
		
		MyCustomer(CustomerAgent c) {
			this.c = c;
			s = CustomerState.waiting;
		}
		
		// Getters
		public CustomerAgent getC() {
			return c;
		}
		
		public String getChoice() {
			return choice;
		}
		
		public CustomerState getS() {
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

