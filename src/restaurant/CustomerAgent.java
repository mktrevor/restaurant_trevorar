package restaurant;

import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import agent.Agent;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name;
	private String choice;
	private int hungerLevel = 5; // determines length of meal
	private int tableNumber; // Variable to hold number of table to eat at
	Timer timer = new Timer();
	private CustomerGui customerGui;

	// agent correspondents
	private WaiterAgent waiter;
	private HostAgent host;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{doingNothing, waitingInRestaurant, beingSeated, seated, ordering, eating, doneEating, leaving};
	private AgentState state = AgentState.doingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, askedToOrder, ordered, startedEating, doneEating, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name){
		super();
		this.name = name;
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

	public void msgGotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgFollowMe(WaiterAgent w, Menu m) {
		waiter = w;
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
	
	public void msgHereIsYourFood(String choice) {
		event = AgentEvent.startedEating;
		stateChanged();
	}
	
	public void msgAnimationDoneEatingFood() {
		event = AgentEvent.doneEating;
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
		if (state == AgentState.doneEating && event == AgentEvent.doneLeaving){
			state = AgentState.leaving;
			leaveRestaurant();
			return true;
		}
		if (state == AgentState.leaving && event == AgentEvent.doneLeaving){
			state = AgentState.doingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgImHungry(this);//send our instance, so he can respond to us
	}

	private void sitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat();
	}
	
	private void readyToOrder() {
		Do("I'm ready to order!");
		waiter.msgImReadyToOrder(this);
	}
	
	private void orderFood() {
		Do("I'm ordering!");
		int randomNum = ((int) name.charAt(0)) % 3;
		switch(randomNum) {
			case 0:
				choice = "steak";
				break;
	
			case 1:
				choice = "chicken";
				break;
	
			case 2:
				choice = "fish";
				break;
		}
		
		print(choice);
		
		waiter.msgHereIsMyChoice(this, choice);
	}

	private void eatFood() {
		Do("Eating Food");
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
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		}, hungerLevel * 1000);//how long to wait before running task
	}
	
	//Combine last two???
	private void tellWaiterImDone() {
		waiter.msgImDoneEating(this);
		event = AgentEvent.doneLeaving;
	}

	private void leaveRestaurant() {
		Do("Leaving.");
		customerGui.DoExitRestaurant();
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
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

