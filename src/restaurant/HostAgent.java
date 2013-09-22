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
public class HostAgent extends Agent {
	static final int NTABLES = 4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> customers
	= new ArrayList<MyCustomer>();
	
	public List<MyWaiter> waiters
	= new ArrayList<MyWaiter>();
	
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;
	
	public HostGui hostGui = null;

	public HostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int i = 1; i <= NTABLES; i++) {
			tables.add(new Table(i));//how you add to a collections
		}
	}

	public String getName() {
		return name;
	}

	public List getCustomers() {
		return customers;
	}

	public Collection getTables() {
		return tables;
	}
	
	public List getWaiters() {
		return waiters;
	}
	
	public void addWaiter(WaiterAgent w) {
		waiters.add(new MyWaiter(w));
	}
	
	// Messages

	public void msgImHungry(CustomerAgent c) {
		customers.add(new MyCustomer(c));
		stateChanged();
	}
	
	public void msgTableIsFree(int table, WaiterAgent w) {
		for (Table t : tables) {
			if (t.tableNumber == table) {
				t.occupied = false;
			}
		}
		for (MyWaiter mw : waiters) {
			if(mw.w == w) {
				mw.numCustomers--;
			}
		}
	}

	/*public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}*/
	
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
			if (!table.occupied) {
				for(MyCustomer mc : customers) {
					if (mc.waiting) {
						seatCustomer(mc, table);
			
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
	
	private void seatCustomer(MyCustomer mc, Table table) {
		MyWaiter leastBusyWaiter = waiters.get(0);
		for(MyWaiter mw : waiters) {
			if(mw.numCustomers < leastBusyWaiter.numCustomers) {
				leastBusyWaiter = mw;
			}
		}

		leastBusyWaiter.w.msgPleaseSeatCustomer(this, mc.c, table.tableNumber);
		leastBusyWaiter.numCustomers++; //Assigned a new customer to the least busy waiter
		mc.waiting = false;
		table.occupied = true;
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

	private class Table {
		//CustomerAgent occupiedBy; (Waiter)
		int tableNumber;
		
		boolean occupied;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		//Waiter code!
		/*void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}*/

		public String toString() {
			return "table " + tableNumber;
		}
	}	
	
	private class MyWaiter {
		WaiterAgent w;
		int numCustomers;
		
		MyWaiter(WaiterAgent w) {
			this.w = w;
			numCustomers = 0;
		}
	}
	
	private class MyCustomer {
		CustomerAgent c;
		boolean waiting;
		
		MyCustomer(CustomerAgent c) {
			this.c = c;
			this.waiting = true;
		}
	}
}




