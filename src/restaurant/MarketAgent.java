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
	
	public enum orderState { received, processed, delivered };

	private String name;
	
	Timer timer = new Timer();
	
	private Map<String, Food> foods = new HashMap<String, Food>();

	public MarketAgent(String name, int steakAmount, int fishAmount, int chickenAmount) {
		super();

		foods.put("steak", new Food("steak", steakAmount));
		foods.put("fish", new Food("fish", fishAmount));
		foods.put("chicken", new Food("chicken", chickenAmount));
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

	/*public void msgINeedMoreFood(CookAgent c, String food, int amount) {
		orders.add( new Order(c, food, amount, orderState.ordered));
		print("Received order for " + food + " from the cook.");
		stateChanged();
	}*/
	
	public void msgFoodOrder(CookAgent c, List<FoodOrder> order) {
		orders.add( new Order(c, order));
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for(Order o : orders) {
			if(o.s == orderState.received) {
				processOrder(o);
				return true;
			}
		}
		
		for(Order o : orders) {
			if(o.s == orderState.processed) {
				deliverFood(o);
				return true;
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void processOrder(Order o) {
		print("Received order from cook. Now processing to see what we can fulfill.");
		/*DoGoToRestaurant()*/
		/* try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
		for(int i = 0; i < o.orders.size(); i++) {
			FoodOrder tempOrder = o.orders.get(i);
			Food tempFood = foods.get(tempOrder.foodType);

			if(tempFood.inventory == 0) {
				tempOrder.amount = 0;
			} 

			else if(tempFood.inventory > tempOrder.amount) {				
				tempFood.inventory -= tempOrder.amount;
			}

			else if(tempFood.inventory < tempOrder.amount) {
				tempOrder.amount = tempFood.inventory;
				tempFood.inventory = 0;
			}	
		}
		o.c.msgWeWillDeliver(this, o.orders);
		o.s = orderState.processed;
	}
	
	deliverFood(Order o) {
		
	}

	// The animation DoXYZ() routines
	

	//utilities

	private class Order {
		CookAgent c;
		List<FoodOrder> orders;
		orderState s;
		
		Order(CookAgent c, List<FoodOrder> orders) {
			this.c = c;
			this.orders = orders;
			this.s = orderState.received;
		}
	}	
	
	private class Food {
		String type;
		int inventory;
		//double unitPrice;
		
		Food(String type, int inventory) {
			this.type = type;
			this.inventory = inventory;
		}
	}
}

