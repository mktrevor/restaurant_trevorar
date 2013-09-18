package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;

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
		askedForOrder, oredered, foodReady, served, finished, 
		leftRestaurant };
		
	public HostAgent host;

	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	private enum waiterState {free, seatingCustomer};
	private waiterState state = waiterState.free;
	
	public HostGui hostGui = null;

	public WaiterAgent(String name) {
		super();

		this.name = name;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	// Messages
	
	public void msgPleaseSeatCustomer(CustomerAgent c, int table) {
		//Stub
	}

	public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgInLobby() {
		if(state == hostState.seatingCustomer) {
			state = hostState.free;
			stateChanged();
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		for (Table table : tables) {
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()) {
					if(state == hostState.free) {
						seatCustomer(waitingCustomers.get(0), table);//the action
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(CustomerAgent customer, Table table) {
		state = hostState.seatingCustomer;
		customer.msgSitAtTable(table.tableNumber);
		DoSeatCustomer(customer, table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		hostGui.DoLeaveCustomer();
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		hostGui.DoBringToTable(customer, table.tableNumber);

	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
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

