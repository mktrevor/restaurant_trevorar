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
	
	boolean foodInventoryChecked = false;
	
	public List<Order> orders = new ArrayList<Order>();
	
	private List<MarketAgent> markets = new ArrayList<MarketAgent>();
	int marketChooser = 0; //This will allow the cook to try a different market if one market runs out of a food
	
	public enum orderState { pending, cooking, cooked, finished };
	
	private enum foodOrderingState { notYetOrdered, ordered };

	private String name;
	
	Timer timer = new Timer();
	
	private Map<String, Food> foods = new HashMap<String, Food>();
	
	//public cookGui cookGui = null;

	public CookAgent(String name) {
		super();

		foods.put("steak", new Food("steak", 8, 1, 3, 10));
		foods.put("fish", new Food("fish", 6, 1, 3, 10));
		foods.put("chicken", new Food("chicken", 4, 1, 3, 10));
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
	
	public void msgWeWillDeliver(String food, int amount) { //Market will notify the cook how much they are able to deliver
		Food thisFood = foods.get(food);
		if(amount < (thisFood.capacity - thisFood.amount)) {
			marketChooser = (marketChooser + 1) % markets.size(); //Try a different market!
			orderFood(thisFood);
		}
		else {
			thisFood.state = foodOrderingState.ordered;
		}
	}
	
	public void msgFoodDelivery(String food, int amount) { //Actual delivery of food
		Food thisFood = foods.get(food);
		thisFood.state = foodOrderingState.notYetOrdered;
		
		thisFood.amount += amount;
	}
	
	public void msgSorryWeAreOutOf(String food) { //Market sends this message if they're out of a food.
		marketChooser = (marketChooser + 1) % markets.size(); //Try a different market!
		orderFood(foods.get(food));
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
		
		Food thisFood = foods.get(o.choice);
		
		if(thisFood.amount == 0) {
			print("We're all out of " + o.choice + "!");
			
			o.w.msgOutOf(o.choice, o.table);
			
			o.s = orderState.finished;
			
			if(thisFood.state != foodOrderingState.ordered) {
				orderFood(thisFood);
			}

			return;
		}
		
		if(thisFood.amount <= thisFood.low) {
			//Order more of this food!
		}
		
		print("Cooking up an order of " + o.choice + "!");
		
		o.s = orderState.cooking;
		thisFood.amount--;
		int cookTime = thisFood.cookingTime * 1000;
				
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
	
	private void orderFood(Food food) {
		markets.get(marketChooser).msgINeedMoreFood(this, food.type, food.capacity - food.amount);
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
	
	public void addMarket(MarketAgent m) {
		markets.add(m);
	}

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
		foodOrderingState state;
		
		Food(String type, int cookingTime, int amount, int low, int capacity) {
			this.type = type;
			this.cookingTime = cookingTime;
			this.amount = amount;
			this.low = low;
			this.capacity = capacity;
			state = foodOrderingState.notYetOrdered;
		}
	}
}

