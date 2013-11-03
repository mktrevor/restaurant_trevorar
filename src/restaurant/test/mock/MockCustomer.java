package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.interfaces.Customer;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {	
	EventLog log;
	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public CashierAgent cashier;

	public MockCustomer(String name) {
		super(name);
	}

	public void msgHereIsChange(double change) {
		log.add(new LoggedEvent("Received change from cashier. Leaving restaurant."));
	}
	
}
