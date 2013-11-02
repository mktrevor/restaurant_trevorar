package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;
import restaurant.interfaces.Market;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class MarketAgent extends Agent implements Market {
	
	public List<Order> orders = new ArrayList<Order>();
	
	public enum orderState { none, received, processed, readyForDelivery, delivered, paidFor };
	
	private CashierAgent cashier;

	private String name;
	
	private double money = 1000.0;
	
	Timer timer = new Timer();
	
	private Map<String, Food> foods = new HashMap<String, Food>();

	public MarketAgent(String name, int steakAmount, int fishAmount, int chickenAmount) {
		super();

		foods.put("steak", new Food("steak", steakAmount, 5.60));
		foods.put("fish", new Food("fish", fishAmount, 4.40));
		foods.put("chicken", new Food("chicken", chickenAmount, 3.05));
		
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List getOrders() {
		return orders;
	}

	// Messages
	
	public void msgFoodOrder(CookAgent c, List<FoodOrder> order) {
		orders.add( new Order(c, order));
		stateChanged();
	}
	
	public void msgHereIsPayment(CashierAgent c, double money) {
		this.money += money;
		stateChanged();
	}
	
	public void msgCannotPayBill(CashierAgent c, double amount) { //Change for extra credit.
		print("I never should have trusted you with my business!");
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
				prepareAndSendOrder(o);
				return true;
			}
		}

		return false;
	}

	// Actions

	private void processOrder(Order o) {
		print("Received order from cook. Now processing to see what we can fulfill.");
		
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
		
		boolean cannotFulfillOrder = true;

		for(int i = 0; i < o.orders.size(); i++) {
			FoodOrder tempOrder = o.orders.get(i);
			if(tempOrder.amount != 0) {
				cannotFulfillOrder = false;
			}
		}
		
		if(cannotFulfillOrder) {
			print("Sorry, we are unable to fulfill any part of your order.");
			o.c.msgCannotFulfillOrder(o.orders);
			o.s = orderState.none;
			return;
		}
		
		print("Order processed; we will soon deliver a full or partial shipment!");
		
		o.c.msgWeWillDeliver(this, o.orders);
		o.s = orderState.processed;
	}
	
	private void prepareAndSendOrder(final Order o) { //Delivers the food from this order after 10 seconds have passed.
		o.s = orderState.readyForDelivery;
		timer.schedule(new TimerTask() {
			public void run() {
				deliverFood(o);
				sendBill(o);
			}
		}, 10000 );
	}
	
	private void deliverFood(Order o) {
		print("Sending food delivery");
		o.c.msgFoodDelivery(this, o.orders);
		o.s = orderState.delivered;
	}

	private void sendBill(Order o) {
		double total = 0.0;
		for(FoodOrder fo : o.orders) {
			Food tempFood = foods.get(fo.foodType);
			total += fo.amount * tempFood.price;
		}
		print("Here is a bill for your recent order. The total is " + total + ".");
		cashier.msgYouOwe(this, total);
	}
	
	// The animation DoXYZ() routines
	

	//utilities
	
	public void addCashier(CashierAgent c) {
		this.cashier = c;
	}
	
	public void clearInventory() {
		foods.get("steak").inventory = 0;
		foods.get("fish").inventory = 0;
		foods.get("chicken").inventory = 0;
	}

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
		double price;
		
		Food(String type, int inventory, double price) {
			this.type = type;
			this.inventory = inventory;
			this.price = price;
		}
	}
}

