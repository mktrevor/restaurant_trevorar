package restaurant.test;

import restaurant.CashierAgent;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockWaiter;

import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest1 extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");
		customer = new MockCustomer("mockcustomer");
		waiter = new MockWaiter("mockwaiter");
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	
	public void test() {
		
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Preconditions
		assertTrue(cashier.checks.isEmpty());
		assertTrue(cashier.customersWhoOweMoney.isEmpty());
		
		//Send message
		cashier.msgProduceCheck(waiter, customer, "steak");
		
		
		//Postconditions
		assertEquals(cashier.checks.size(), 1);
		assertEquals(cashier.checks.get(0).c.cust, customer);
		
	}
}
