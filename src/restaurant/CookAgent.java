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
public class CookAgent extends Agent {
	
	public List<Order> orders = new ArrayList<Order>();
	
	public enum orderState { pending, cooking, cooked, finished };

	private String name;
	
	Timer timer = new Timer();
	
	//public cookGui cookGui = null;

	public CookAgent(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List getOrders() {
		return orders;
	}

	// Messages

	public void msgHereIsOrder(WaiterAgent w, String choice, int table) {
		orders.add(new Order(w, choice, table, orderState.pending));
		stateChanged();
	}
	
	public void msgFoodDoneCooking(Order o) {
		o.s = orderState.cooked;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for(Order o : orders) {
			if(o.s == orderState.cooked) {
				plateIt(o);
				return true;
			}
		}

		for(Order o : orders) {
			if(o.s == orderState.pending) {
				cookIt(o);
				return true;
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void cookIt(final Order o) {
		//Animation
		//DoCooking(o) 
		
		print("Cooking up an order of " + o.choice + "!");
		
		o.s = orderState.cooking;
		int cookTime = 0;
		for(Food f : Menu.getFoods()) {
			if(f.getType() == o.choice) {
				cookTime = f.getTime() * 1000;
			}
		}
		
		timer.schedule(new TimerTask() {
							public void run() {
								 msgFoodDoneCooking(o);
							}
						}, cookTime	);
	}
	
	private void plateIt(Order o) {
		o.s = orderState.finished;
		//Animation
		//DoPlating(o);
		print(o.choice + " done cooking, time to plate it!");
		o.w.msgOrderDone(o.choice, o.table);
	}

	// The animation DoXYZ() routines
	

	//utilities

	//Stuff for cook GUi
	/*public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}*/

	private class Order {
		WaiterAgent w;
		String choice;
		int table;
		orderState s;
		
		Order(WaiterAgent w, String choice, int table, orderState s) {
			this.w = w;
			this.choice = choice;
			this.table = table;
			this.s = s;
		}
	}	
}

