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
	
	public enum OrderState { pending, cooking, cooked, finished };
	
	//Implement this map
	//map(String, Food) foods;

	private String name;
	private enum cookState {free, seatingCustomer};
	private cookState state = cookState.free;
	
	public HostGui hostGui = null;

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
		//STUB
	}
	
	public void msgFoodDone(Order o) {
		//STUB
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/*if(cooked orders) {
			//STUB
		}
		if(pending orders) {
			//STUB
		}*/

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void cookIt(Order o) {
		//STUB
	}
	
	private void plateIt(Order o) {
		//STUB
	}

	// The animation DoXYZ() routines
	

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	private class Order {
		//STUB
	}
	
	private class Food {
		//STUB
	}
	
}

