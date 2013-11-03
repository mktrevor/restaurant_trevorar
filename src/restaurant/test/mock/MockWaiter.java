package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.Check;
import restaurant.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter {
	CashierAgent cashier;
	
	public EventLog log;

	public MockWaiter(String name) {
		super(name);
		log = new EventLog();
	}
	
	public void msgHereIsCheck(Check c) {
        log.add(new LoggedEvent("Received check from cashier."));
	}
}
 