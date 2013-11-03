package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.interfaces.Market;

public class MockMarket extends Mock implements Market {
	CashierAgent cashier;
	
	EventLog log;

	public MockMarket(String name) {
		super(name);
	}
	
	public void msgHereIsPayment(CashierAgent c, double money) {
		log.add(new LoggedEvent("Received payment for order."));
	}
	
	public void msgCannotPayBill(CashierAgent c, double total) {
		log.add(new LoggedEvent("Cannot pay for order."));
	}
}
 