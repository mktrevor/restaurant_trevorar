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
	
	private Map<String, Food> foods = new HashMap<String, Food>();
	
	//public cookGui cookGui = null;

	public CookAgent(String name) {
		super();

		foods.put("steak", new Food("steak", 8, 5, 3, 10));
		foods.put("fish", new Food("fish", 6, 5, 3, 10));
		foods.put("chicken", new Food("chicken", 4, 5, 3, 10));
		/*foods.put("pizza", new Food("pizza", 7, 5, 3, 10));
		foods.put("salad", new Food("salad", 4, 5, 3, 10));*/
		
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
		int cookTime = foods.get(o.choice).cookingTime * 1000;
		
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
	
	private class Food {
		String type;
		int cookingTime;
		int amount;
		int low;
		int capacity;
		orderingState state;
		
		Food(String type, int cookingTime, int amount, int low, int capacity) {
			this.type = type;
			this.cookingTime = cookingTime;
			this.amount = amount;
			this.low = low;
			this.capacity = capacity;
			state = orderingState.notYetOrdered;
		}
	}
	
	private enum orderingState {notYetOrdered, ordered};
}

