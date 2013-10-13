package restaurant;

import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import agent.Agent;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name;
	private String choice;
	private double money;
	private Check check;
	
	private int hungerLevel = 8; // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	
	private Semaphore atDestination = new Semaphore(0, true);

	// agent correspondents
	private WaiterAgent waiter;
	private HostAgent host;
	
	private Menu menu;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{doingNothing, waitingInRestaurant, beingSeated, seated, ordering, reordering, eating, doneEating, payingBill, leaving};
	private AgentState state = AgentState.doingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, askedToOrder, ordered, reordering, startedEating, doneEating, payBill, leaving};
	AgentEvent event = AgentEvent.none;

	Random ranGenerator = new Random(); //Random number generator for money and choice
	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name){
		super();
		
		this.name = name;
		
		money = (double) ranGenerator.nextInt(50);
		
		if(name.equals("broke")) {
			money = 0.0;
		}
		if(name.equals("moneybags")) {
			money = 1000.0;
		}
		
		int randNum = ranGenerator.nextInt(3);
		
		switch(randNum) {
		case 0:
			choice = "steak";
			break;
			
		case 1:
			choice = "fish";
			break;
			
		case 2:
			choice = "chicken";
			break;
		}
		
		if(name.equals("steak")) {
			choice = "steak";
		}
		if(name.equals("fish")) {
			choice = "fish";
		}
		if(name.equals("chicken")) {
			choice = "chicken";
		}
	}

	/**
	 * hack to establish connection to Host agent.
	 */	
	public void setHost(HostAgent host) {
		this.host = host;
	}

	public String getCustomerName() {
		return name;
	}
	
	// Messages
	
	public void msgRestaurantFull() {
		int randomChoice = ranGenerator.nextInt(2);
		if(randomChoice == 0) {
			print("I don't have time to wait around!");
			event = AgentEvent.leaving;
		}
		else if(randomChoice == 1) {
			print("I'm in no hurry, I'll wait around.");
		}
		stateChanged();
	}

	public void msgGotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgFollowMe(WaiterAgent w, Menu m) {
		waiter = w;
		menu = m;
		event = AgentEvent.followWaiter;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgWhatDoYouWant() {
		event = AgentEvent.askedToOrder;
		stateChanged();
	}
	
	public void msgPleaseReorder() {
		event = AgentEvent.reordering;
		stateChanged();
	}
	
	public void msgRemoveFromMenu(String choice) {
		menu.removeChoice(choice);
	}
	
	public void msgHereIsYourBill(Check c) {
		check = c;
		stateChanged();
	}
	
	/*public void msgHereIsYourChange(double money) {
		this.money += money;
	}*/
	
	public void msgHereIsYourFood(String choice) {
		event = AgentEvent.startedEating;
		stateChanged();
	}
	
	public void msgAnimationDoneEatingFood() {
		event = AgentEvent.doneEating;
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
		//	CustomerAgent is a finite state machine

		if (state == AgentState.doingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.waitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if(state == AgentState.waitingInRestaurant && event == AgentEvent.leaving) {
			state = AgentState.leaving;
			impatientLeaveRestaurant();
			return true;
		}
		if (state == AgentState.waitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.beingSeated;
			sitDown();
			return true;
		}
		if (state == AgentState.beingSeated && event == AgentEvent.seated){
			state = AgentState.seated;
			readyToOrder();
			return true;
		}
		if (state == AgentState.seated && event == AgentEvent.askedToOrder){
			state = AgentState.ordering;
			orderFood();
			return true;
		}
		if (state == AgentState.ordering && event == AgentEvent.reordering) {
			state = AgentState.reordering;
			readyToOrder();
			return true;
		}
		if (state == AgentState.reordering && event == AgentEvent.askedToOrder) {
			state = AgentState.ordering;
			reorderFood();
			return true;
		}
		if(state == AgentState.ordering && event == AgentEvent.leaving) {
			state = AgentState.leaving;
			leaveRestaurant();
			return true;
		}
		if (state == AgentState.ordering && event == AgentEvent.startedEating){
			state = AgentState.eating;
			eatFood();
			return true;
		}
		if (state == AgentState.eating && event == AgentEvent.doneEating){
			state = AgentState.doneEating;
			tellWaiterImDone();
			return true;
		}
		if (state == AgentState.doneEating && event == AgentEvent.payBill && check != null) {
			state = AgentState.payingBill;
			payBill();
			return true;
		}
		if (state == AgentState.payingBill && event == AgentEvent.leaving){
			state = AgentState.leaving;
			leaveRestaurant();
			return true;
		}
		if (state == AgentState.leaving && event == AgentEvent.leaving){
			state = AgentState.doingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		print("Going to restaurant");
		host.msgImHungry(this);//send our instance, so he can respond to us
	}

	private void sitDown() {
		print("Being seated. Going to table");
		customerGui.DoGoToSeat();
	}
	
	private void readyToOrder() {
		waiter.msgCustomerSatDown();
		customerGui.clearOrder();
		
		timer.schedule(new TimerTask() {
			public void run() {
				print("I'm ready to order!");
				callWaiter();
			}
		}, 3000	);
	}
	
	private void callWaiter() {
		waiter.msgImReadyToOrder(this);
		customerGui.wantToOrder();
	}
	
	private void orderFood() {
		print("I would like an order of " + choice + " please!");
		
		customerGui.orderedFood(choice);
		print(choice + " please!");
		
		waiter.msgHereIsMyChoice(this, choice);
	}
	
	private void reorderFood() {
		if(menu.getMenuSize() > 0) {			
			// Convert 1st letter of customer's name to a number in order to choose a food.
			int choiceNum = ranGenerator.nextInt(menu.getMenuSize());
			choice = menu.getChoice(choiceNum).getType();

			print("I would like an order of " + choice + " please!");
			customerGui.orderedFood(choice);
			
			waiter.msgHereIsMyChoice(this, choice);
		} else {
			print("There's nothing left for me to order! I'm leaving!");

			customerGui.doneEating();
			waiter.msgImDoneEating(this);
			event = AgentEvent.leaving;
		}
	}

	private void eatFood() {
		print("Eating Food");
		customerGui.startedEating(choice);
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating!");
				event = AgentEvent.doneEating;
				GuiDoneEating();
				//isHungry = false;
				stateChanged();
			}
		}, hungerLevel * 1000);//how long to wait before running task
	}
	
	private void GuiDoneEating() {
		customerGui.doneEating();
	}
	
	//Combine last two???
	private void tellWaiterImDone() {
		waiter.msgImDoneEating(this);
		event = AgentEvent.payBill;
	}

	private void leaveRestaurant() {
		print("I'm leaving! Goodbye!");
		customerGui.DoExitRestaurant();
	}
	
	private void impatientLeaveRestaurant() {
		print("I'm leaving! Goodbye!");
		host.msgImLeaving(this);
		customerGui.DoExitRestaurant();
	}
	
	private void payBill() {
		customerGui.DoGoToCashier();
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(money > check.amount) {
			print("Here is my payment of $" + check.amount + ".");
			check.cashier.msgPayBill(check, check.amount);
			money -= check.amount;
		}
		else {
			check.cashier.msgPayBill(check, money);
			print("This is all I have. Take $" + money + ".");
			money = 0;
		}
		event = AgentEvent.leaving;
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public double getMoney() {
		return money;
	}
	
	public void addTenDollars() {
		money += 10.0;
	}
	
	public void removeTenDollars() {
		if(money < 10) {
			money = 0.0;
			return;
		}
		money -= 10.0;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
}

