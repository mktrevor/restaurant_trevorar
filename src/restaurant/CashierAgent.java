package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;

import java.util.*;
import java.util.concurrent.Semaphore;

public class CashierAgent extends Agent {
	
	public EventLog log; //Log for keeping track of events while unit testing cashier
	
	public List<MyCheck> checks = Collections.synchronizedList(new ArrayList<MyCheck>());
	
	public List<MyCustomer> customersWhoOweMoney = Collections.synchronizedList(new ArrayList<MyCustomer>());
	
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	
	private Menu menu = new Menu();

	private enum checkState { requested, givenToWaiter, fullyPaid, partiallyPaid, finished };

	private String name;
	
	private double money = 1000.0;
	
	Timer timer = new Timer();

	public CashierAgent(String name) {
		super();
		
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// Messages
	public void msgProduceCheck(Waiter w, Customer c, String choice) {
		Check check = new Check(this, c, choice);
		check.amount = menu.getPrice(choice);
		
		synchronized(customersWhoOweMoney) {
			for(MyCustomer mc : customersWhoOweMoney) {
				if(mc.c == c) {
					print("Well, look who's back! This customer will have to repay their previous bill of " + mc.amountOwed + " as well.");
					check.amount += mc.amountOwed;
				}
			}
		}
		checks.add(new MyCheck(w, check));
		
		stateChanged();
	}
	
	public void msgPayBill(Check check, double money) {
		synchronized(checks) {
			for(MyCheck c : checks) {
				if(c.c == check) {
					if(money == c.c.amount) {
						c.state = checkState.fullyPaid;
					}
					else if(money < c.c.amount) {
						c.c.amount -= money;
						c.state = checkState.partiallyPaid;
					}
				}
			}
		}
		stateChanged();
	}
	
	public void msgYouOwe(MarketAgent m, double amount) {
		marketBills.add(new MarketBill(m, amount));
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		synchronized(checks) {
			for(MyCheck c : checks) {
				if(c.state == checkState.fullyPaid) {
					thankCustomer(c);
				}
			}
		}
		
		synchronized(checks) {
			for(MyCheck c : checks) {
				if(c.state == checkState.partiallyPaid) {
					addCustomerToOweList(c);
				}
			}
		}
		
		synchronized(checks) {
			for(MyCheck c : checks) {
				if(c.state == checkState.requested) {
					giveCheckToWaiter(c);
				}
			}
		}

		if(!marketBills.isEmpty()) {
			payBill(marketBills.get(0));
		}

		return false;
	}

	// Actions

	private void giveCheckToWaiter(MyCheck c) {
		print("The check for " + c.c.cust + " is ready!");
		c.w.msgHereIsCheck(c.c);
		c.state = checkState.givenToWaiter;
	}
	
	private void thankCustomer(MyCheck c) {
		print("Thank you! Please come again!");
		c.state = checkState.finished;
	}
	
	private void addCustomerToOweList(MyCheck c) {
		print("You still owe $" + c.c.amount + "! You'll have to pay it back next time!");
		synchronized(customersWhoOweMoney) {
			for(MyCustomer mc : customersWhoOweMoney) { //If customer is already on the "owe money" list, add the money to the amount they owe
				if(mc.c == c.c.cust) {
					c.state = checkState.finished;
					return;
				}
			}
		}
		
		customersWhoOweMoney.add(new MyCustomer(c.c.cust, c.c.amount));
		c.state = checkState.finished;
	}
	
	private void payBill(MarketBill mb) {
		if(money > mb.amountOwed) {
			print("Here is my payment for the recent shipment!");
			mb.m.msgHereIsPayment(this, mb.amountOwed);
			money -= mb.amountOwed;
		} else {
			print("Thanks for the food, but I can't pay for it!");
			mb.m.msgCannotPayBill(this, mb.amountOwed);
		}
		
		marketBills.remove(mb);
	}

	// The animation DoXYZ() routines
	

	//utilities
	
	public class MyCheck {
		Waiter w;
		public Check c;
		checkState state = checkState.requested;
		
		MyCheck(Waiter w, Check c) {
			this.w = w;
			this.c = c;
		}
	}
	
	public class MyCustomer {
		Customer c;
		double amountOwed;
	
		MyCustomer(Customer c, double amount) {
			this.c = c;
			this.amountOwed = amount;
		}
	}
	
	public class MarketBill {
		Market m;
		double amountOwed;
		
		MarketBill(Market m, double amount) {
			this.m = m;
			this.amountOwed = amount;
		}
	}
}

