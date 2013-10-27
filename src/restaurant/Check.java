package restaurant;

import restaurant.interfaces.Customer;

public class Check {
	public Customer cust;
	public CashierAgent cashier;
	public String choice;
	public double amount;
	
	Check(CashierAgent cashier, Customer cust, String choice) {
		this.cashier = cashier;
		this.cust = cust;
		this.choice = choice;
	}
	
	public Customer getCust() {
		return cust;
	}
}
