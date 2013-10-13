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
	
	private int numberOfWorkingWaiters = 0; //To make sure there's always at least 1 waiter not on break.
	
	public HostGui hostGui = null;

	public HostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int i = 1; i <= NTABLES; i++) {
			tables.add(new Table(i));
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
		numberOfWorkingWaiters++;
		stateChanged();
	}
	
	// Messages

	public void msgImHungry(CustomerAgent c) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.waiting = true;
				mc.toldRestaurantIsFull = false;
				print("Welcome to Restaurant V2.1, " + c.getName() + "!");
				stateChanged();
				return;
			}
		}
		customers.add(new MyCustomer(c));
		print("Welcome to Restaurant V2.1, " + c.getName() + "!");
		stateChanged();
	}
	
	public void msgIWantABreak(WaiterAgent w) {
		for(MyWaiter mw : waiters) {
			if(mw.w == w) {
				mw.iWantABreak = true;
			}
		}
		stateChanged();
	}
	
	public void msgImDoneWithMyBreak(WaiterAgent w) {
		for(MyWaiter mw : waiters) {
			if(mw.w == w) {
				mw.iWantABreak = false;
				mw.state = waiterState.working;
				numberOfWorkingWaiters++;
			}
		}
		stateChanged();
	}
	
	public void msgImLeaving(CustomerAgent c) {
		for(MyCustomer mc : customers) {
			if(mc.c == c) {
				mc.waiting = false;
			}
		}
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
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for(MyWaiter w : waiters) {
			if (w.iWantABreak) {
				giveWaiterABreak(w);
			}
		}		
		for (Table table : tables) {
			if (!table.occupied) {
				for(MyCustomer mc : customers) {
					if (mc.waiting) {
						if(!waiters.isEmpty()) {
							seatCustomer(mc, table);
			
							return true;//return true to the abstract agent to reinvoke the scheduler.
						}
					}
				}
			}
		}
		
		for(MyCustomer mc : customers) {
			if(mc.waiting && !mc.toldRestaurantIsFull && !waiters.isEmpty()) {
				tellCustomerRestaurantIsFull(mc);
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
		for(int i = 0; i < waiters.size(); i++) {
			if(waiters.get(i).state == waiterState.onBreak) {
				leastBusyWaiter = waiters.get(i+1);
			}
			else { break; }
		}
		
		// This loop finds the waiter that is currently dealing with the least number of customers
		for(MyWaiter mw : waiters) {
			if(mw.numCustomers < leastBusyWaiter.numCustomers && mw.state != waiterState.onBreak) {
				leastBusyWaiter = mw;
			}
		}

		leastBusyWaiter.w.msgPleaseSeatCustomer(this, mc.c, table.tableNumber);
		print(leastBusyWaiter.w.getName() + ", could you please seat customer " + mc.c.getName() + "?");
		leastBusyWaiter.numCustomers++; // Assigned a new customer to the least busy waiter
		mc.waiting = false;
		table.occupied = true;
	}
	
	private void giveWaiterABreak(MyWaiter w) {
		w.iWantABreak = false;
		if(waiters.size() == 1 || numberOfWorkingWaiters == 1) {
			print("Sorry, there's no one else to cover for you!");
			w.w.msgSorryNoBreakNow();
			return;
		}
				
		w.w.msgFinishUpAndTakeABreak();
		print("Alright, " + w.w.getName() + ", finish up and take a break.");
		w.state = waiterState.onBreak;
		numberOfWorkingWaiters--;
	}
	
	private void tellCustomerRestaurantIsFull(MyCustomer c) {
		print("The restaurant is currently full. Feel free to wait for an opening or leave!");
		c.toldRestaurantIsFull = true;
		c.c.msgRestaurantFull();
	}

	// The animation DoXYZ() routines

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	private class Table {
		int tableNumber;
		boolean occupied;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
		
		public String toString() {
			return "table " + tableNumber;
		}
	}	
	
	private class MyWaiter {
		WaiterAgent w;
		int numCustomers;
		boolean iWantABreak;
		waiterState state;
		
		MyWaiter(WaiterAgent w) {
			this.w = w;
			numCustomers = 0; // Waiters start out with no customers
			state = waiterState.working;
		}
	}
	
	private enum waiterState { working, onBreak };
	
	private class MyCustomer {
		CustomerAgent c;
		boolean waiting;
		boolean toldRestaurantIsFull; 
		
		MyCustomer(CustomerAgent c) {
			this.c = c;
			this.waiting = true;
			this.toldRestaurantIsFull = false;
		}
	}
}