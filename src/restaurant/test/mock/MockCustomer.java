package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.interfaces.Customer;

public class MockCustomer extends Mock implements Customer {	
	public EventLog log;
	
	public CashierAgent cashier;
	public double money;

	public MockCustomer(String name) {
		super(name);
		log = new EventLog();
		
		if(name.equals("flake")) {
			money = 0.0;
		} else {
			money = 100.0;
		}
	}

	public void msgHereIsChange(double change) {
		log.add(new LoggedEvent("Received change from cashier. Leaving restaurant."));
	}
	
}
