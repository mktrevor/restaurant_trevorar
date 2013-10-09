package restaurant;

public class Check {
	CustomerAgent cust;
	CashierAgent cashier;
	String choice;
	double amount;
	
	Check(CashierAgent cashier, CustomerAgent cust, String choice) {
		this.cashier = cashier;
		this.cust = cust;
		this.choice = choice;
	}
}
