package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.interfaces.Market;

public class MockMarket extends Mock implements Market {
	public CashierAgent cashier;
	
	public double money = 1000.0;
	
	public EventLog log;

	public MockMarket(String name) {
		super(name);
		log = new EventLog();
	}
	
	public void msgHereIsPayment(CashierAgent c, double money) {
		log.add(new LoggedEvent("Received payment for order."));
		this.money += money;
	}
	
	public void msgCannotPayBill(CashierAgent c, double total) {
		log.add(new LoggedEvent("Cannot pay for order."));
	}
}
 