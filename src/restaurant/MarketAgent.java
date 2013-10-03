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
public class MarketAgent extends Agent {
	
	public List<Order> orders = new ArrayList<Order>();
	
	public enum orderState { ordered, delivered };

	private String name;
	
	Timer timer = new Timer();
	
	private Map<String, Food> foods = new HashMap<String, Food>();
	
	//public cookGui cookGui = null;

	public MarketAgent(String name) {
		super();

		foods.put("steak", new Food("steak", 100, 5.50));
		foods.put("fish", new Food("fish", 100, 4.50));
		foods.put("chicken", new Food("chicken", 100, 3.50));
		/*foods.put("pizza", new Food("pizza", 100, 6.00));
		foods.put("salad", new Food("salad", 100, 3.50));*/
		
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List getOrders() {
		return orders;
	}

	// Messages

	public void msgINeedMoreFood(CookAgent c, String food, int amount) {
		orders.add( new Order(c, food, amount, orderState.ordered));
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for(Order o : orders) {
			if(o.s == orderState.ordered) {
				sendShipment(o);
				return true;
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void sendShipment(Order o) {
		/*DoGoToRestaurant()*/
		/* try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
		Food orderedFood = foods.get(o.foodType);
			
		if(orderedFood.inventory == 0) {
			print("Sorry, we're all out of " + o.foodType + "!");
			
			o.c.msgSorryWeAreOutOf(o.foodType);
			o.s = orderState.delivered;
		} 
		
		else if(orderedFood.inventory > o.amount) {
			print("Here is your delivery of " + o.foodType + "!");
			
			o.c.msgFoodDelivery(o.foodType, o.amount);
			
			orderedFood.inventory -= o.amount;
			o.s = orderState.delivered;
		}
		
		else if(orderedFood.inventory < o.amount) {
			print("We don't have that much " + o.foodType + ". We'll send you all that we have!");
			
			o.c.msgFoodDelivery(o.foodType, orderedFood.inventory);
			
			orderedFood.inventory = 0;
			o.s = orderState.delivered;
		}		
	}

	// The animation DoXYZ() routines
	

	//utilities

	private class Order {
		CookAgent c;
		String foodType;
		int amount;
		orderState s;
		
		Order(CookAgent c, String foodType, int amount, orderState s) {
			this.c = c;
			this.foodType = foodType;
			this.amount = amount;
			this.s = s;
		}
	}	
	
	private class Food {
		String type;
		int inventory;
		double unitPrice;
		
		Food(String type, int inventory, double unitPrice) {
			this.type = type;
			this.inventory = inventory;
			this.unitPrice = unitPrice;
		}
	}
}

