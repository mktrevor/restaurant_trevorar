package restaurant.test;

import restaurant.CashierAgent;
import restaurant.CashierAgent.MyCheck;
import restaurant.CashierAgent.checkState;
import restaurant.Check;
import restaurant.CashierAgent.MarketBill;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;

import junit.framework.*;

/* A class to test a simple normative scenario between cashier and waiter/customer */

public class CashierCustomerTest1 extends TestCase
{
	//instantiated in setUp()
	CashierAgent cashier;
	MockCustomer customer;
	MockWaiter waiter;
	String orderFood;
	
	public void setUp() throws Exception {
		super.setUp();		
		cashier = new CashierAgent("cashier");
		waiter = new MockWaiter("waiter");
		customer = new MockCustomer("customer");
		orderFood = "steak";
	}	
	
	/* This tests the cashier receiving a single check request from a waiter which is then paid off by a customer */
	public void testOneCustomerCheck() {		
		
		//Preconditions
		assertTrue(cashier.checks.isEmpty()); //Cashier should have no bills
		assertEquals(cashier.money, 1000.0); //Cashier starts with $1000
		assertEquals(customer.money, 100.0); //Customer starts with $100
		assertEquals(cashier.stateChange.availablePermits(), 1); //stateChange semaphore should have 1 permit to start with
		
		//Send message
		cashier.msgProduceCheck(waiter, customer, orderFood); //A request for the cashier to produce a check
		
		//Postconditions of first message
		assertTrue(cashier.checks.size() == 1); //Cashier should now have a single check
		Check check = cashier.checks.get(0).c; 
		assertEquals(check.cust, customer); //Check should have the correct customer
		assertEquals(check.choice, orderFood); //Check should have correct choice
		assertEquals(check.amount, 15.99); //Check should be created with correct amount of money owed
		assertEquals(cashier.stateChange.availablePermits(), 2); //stateChange semaphore should have 2 permits
		
		//Scheduler call
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler should call an action to give bill to waiter
		
		//Postconditions of scheduler call
		assertEquals(cashier.stateChange.availablePermits(), 2); //Still should have 2 permits
		assertTrue(waiter.log.containsString("Received check from cashier.")); //Correct message call in waiter
		assertTrue(cashier.checks.size() == 1); //No more checks added
		
		//Message from customer to pay bill
		cashier.msgPayBill(check, customer.money);
		
		//Postconditions of message
		assertEquals(cashier.stateChange.availablePermits(), 3); //An extra permit from message call
		assertTrue(cashier.checks.size() == 1); //No checks added
		MyCheck mc = cashier.checks.get(0);
		assertEquals(mc.amountPaid, customer.money); //Amount paid is all of customer's money
		assertTrue(mc.state == checkState.fullyPaid); //Check has been paid
		
		//Scheduler
		assertTrue(cashier.pickAndExecuteAnAction());
		
		//Postconditions of scheduler
		assertTrue(mc.state == checkState.finished); //Check exchange is finished
		assertTrue(customer.log.containsString("Received change from cashier. Leaving restaurant.")); //Change was given to customer
		assertEquals(cashier.money, 1000.0 + check.amount); //Cashier received correct amount of money from check payment
		
	}
}
