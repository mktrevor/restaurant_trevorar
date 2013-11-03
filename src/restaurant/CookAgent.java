package restaurant;

import agent.Agent;
import restaurant.gui.CookGui;
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
	
	private List<MarketAgent> markets = Collections.synchronizedList(new ArrayList<MarketAgent>());
	int marketChooser = 0; //This will allow the cook to try a different market if one market runs out of a food
	
	public enum orderState { pending, cooking, cooked, pickedUp, finished };
	
	int orderCount = 0;
	
	private enum foodOrderingState { notYetOrdered, ordered };
	
	boolean restaurantOpening = true; // A bool to deal with the initial inventory check when restaurant opens.
	
	private String name;
	
	Timer timer = new Timer();
	
	private Map<String, Food> foods = Collections.synchronizedMap(new HashMap<String, Food>());

	private Semaphore atDestination = new Semaphore(0, true); // For gui movements
	
	public CookGui cookGui = null;

	public CookAgent(String name) {
		super();
		
				//usage: new Food(String type, int cookTime, int amount, int low, int capacity);
		foods.put("steak", new Food("steak", 8, 6, 5, 8));
		foods.put("fish", new Food("fish", 6, 6, 5, 8));
		foods.put("chicken", new Food("chicken", 4, 6, 5, 8));
		
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setGui(CookGui gui) {
		cookGui = gui;
	}

	public List getOrders() {
		return orders;
	}

	// Messages
	public void msgHereIsOrder(WaiterAgent w, String choice, int table) {
		orders.add(new Order(w, choice, table, orderState.pending, orderCount++));
		stateChanged();
	}
	
	public void msgFoodDoneCooking(Order o) {
		o.s = orderState.cooked;
		stateChanged();
	}
	
	public void msgPickedUpOrder(int orderNumber) {
		for(Order o : orders) {
			if(o.orderNumber == orderNumber) {
				o.s = orderState.pickedUp;
			}
		}
		stateChanged();
	}
	
	public void msgWeWillDeliver(MarketAgent m, List<FoodOrder> orders) { //Market will notify the cook how much they are able to deliver
		for(int i = 0; i < orders.size(); i++) {
			FoodOrder tempOrder = orders.get(i);
			Food thisFood = foods.get(tempOrder.foodType);
			if(tempOrder.amount < (thisFood.capacity - thisFood.amount)) {	
				thisFood.state = foodOrderingState.notYetOrdered;
			}
			else {
				thisFood.state = foodOrderingState.ordered;
			}
		}
		stateChanged();
	}
	
	public void msgCannotFulfillOrder(List<FoodOrder> orders) {
		marketChooser = (marketChooser + 1) % markets.size(); //Start ordering from a different market.
		for(int i = 0; i < orders.size(); i++) {
			FoodOrder tempOrder = orders.get(i);
			Food tempFood = foods.get(tempOrder.foodType);
			tempFood.state = foodOrderingState.notYetOrdered;
		}
		stateChanged();
	}
	
	public void msgFoodDelivery(MarketAgent m, List<FoodOrder> orders) { //Actual delivery of food
		for(int i = 0; i < orders.size(); i++) {
			FoodOrder tempOrder = orders.get(i);
			Food tempFood = foods.get(tempOrder.foodType);
			tempFood.state = foodOrderingState.notYetOrdered;
			print("Received delivery of " + tempOrder.amount + " units of " + tempOrder.foodType);
			tempFood.amount += tempOrder.amount;
		}
		stateChanged();
	}
	
	public void msgRecheckInventory() {
		Food temp = foods.get("steak");
		temp.amount = temp.low - 1;
		
		temp = foods.get("fish");
		temp.amount = temp.low - 1;
		
		temp = foods.get("chicken");
		temp.amount = temp.low - 1;
		
		restaurantOpening = true;
		stateChanged();
	}
	
	public void msgAtDestination() {
		atDestination.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if(restaurantOpening) {
			initialInventoryCheck();
			return true;
		}
		
		for(Order o : orders) {
			if(o.s == orderState.pickedUp) {
				finishIt(o);
				return true;
			}
		}
		
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
		
		DoGoToHome();

		return false;
	}

	// Actions

	private void cookIt(final Order o) {
		//Animation
		//DoCooking(o) 

		
		Food thisFood = foods.get(o.choice);
		
		DoGoToFridge();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(thisFood.amount == 0) {
			print("We're all out of " + o.choice + "!");
			
			o.w.msgOutOf(o.choice, o.table);
			
			o.s = orderState.finished;
			
			if(thisFood.state != foodOrderingState.ordered) {
				orderMoreFood();
			}

			return;
		}
		
		if(thisFood.amount <= thisFood.low) {
			orderMoreFood();
		}
		

		cookGui.msgNewOrder(o.choice, o.orderNumber);
		
		print("Cooking up an order of " + o.choice + "!");
		
		DoGoToGrill();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		o.s = orderState.cooking;
		thisFood.amount--;
		cookGui.msgOrderCooking(o.orderNumber);
		
		int cookTime = thisFood.cookingTime * 1000;
				
		timer.schedule(new TimerTask() {
							public void run() {
								 msgFoodDoneCooking(o);
							}
						}, cookTime	);
	}

	private void plateIt(Order o) {
		DoGoToGrill();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cookGui.msgOrderBeingCarried(o.orderNumber);
		
		o.s = orderState.finished;
		print(o.choice + " done cooking, time to plate it!");
		
		DoGoToCounter();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		cookGui.msgOrderWaiting(o.orderNumber);
		o.w.msgOrderDone(o.choice, o.table, o.orderNumber);
	}
	
	private void finishIt(Order o) {
		cookGui.msgOrderPickedUp(o.orderNumber);
		o.s = orderState.finished;
	}
	
	private void initialInventoryCheck() {
		print("Checking initial inventory levels.");
		Food steak = foods.get("steak");
		Food chicken = foods.get("chicken");
		Food fish = foods.get("fish");
		
		if((steak.amount < steak.low) || (chicken.amount < chicken.low) || (fish.amount < fish.low)) {
			orderMoreFood();
		} else {
			print("All foods are in stock! We're ready to go!");
		}
		restaurantOpening = false;
	}
	
	private void orderMoreFood() {
		List<FoodOrder> orderList = new ArrayList<FoodOrder>();
		
		Food temp = foods.get("steak");
		if(temp.amount < temp.low && temp.state == foodOrderingState.notYetOrdered) {
			orderList.add(new FoodOrder(temp.type, temp.capacity - temp.amount));
			temp.state = foodOrderingState.ordered;
		}
		
		temp = foods.get("chicken");
		if(temp.amount < temp.low && temp.state == foodOrderingState.notYetOrdered) {
			orderList.add(new FoodOrder(temp.type, temp.capacity - temp.amount));
			temp.state = foodOrderingState.ordered;
		}
		
		temp = foods.get("fish");
		if(temp.amount < temp.low && temp.state == foodOrderingState.notYetOrdered) {
			orderList.add(new FoodOrder(temp.type, temp.capacity - temp.amount));
			temp.state = foodOrderingState.ordered;
		}
		
		if(orderList.isEmpty()) {
			return;
		}
		
		print("Sending order for more food to the market!");
		markets.get(marketChooser).msgFoodOrder(this, orderList);
	}
	
	private void DoGoToHome() {
		cookGui.DoGoToHome();
	}
	
	private void DoGoToFridge() {
		cookGui.DoGoToFridge();
	}
	
	private void DoGoToGrill() {
		cookGui.DoGoToGrill();
	}
	
	private void DoGoToCounter() {
		cookGui.DoGoToCounter();
	}
	
	public void clearSteak() {
		foods.get("steak").amount = 0;
	}
	
	public void clearFish() {
		foods.get("fish").amount = 0;
	}
	
	public void clearChicken() {
		foods.get("chicken").amount = 0;
	}
	
	public void addMarket(MarketAgent m) {
		markets.add(m);
	}

	private class Order {
		WaiterAgent w;
		String choice;
		int table;
		orderState s;
		int orderNumber;
		
		Order(WaiterAgent w, String choice, int table, orderState s, int number) {
			this.w = w;
			this.choice = choice;
			this.table = table;
			this.s = s;
			this.orderNumber = number;
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

