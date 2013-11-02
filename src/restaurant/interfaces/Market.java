package restaurant.interfaces;

import restaurant.CashierAgent;

public interface Market {

	public abstract void msgHereIsPayment(CashierAgent c, double money);
	
	public abstract void msgCannotPayBill(CashierAgent c, double total);
	
}
