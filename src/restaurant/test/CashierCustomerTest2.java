package restaurant.test;

import restaurant.CashierAgent;
import restaurant.CashierAgent.MyCheck;
import restaurant.CashierAgent.MyCustomer;
import restaurant.CashierAgent.checkState;
import restaurant.Check;
import restaurant.CashierAgent.MarketBill;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;

import junit.framework.*;

/* A class to test a simple normative scenario between cashier and waiter/customer */

public class CashierCustomerTest2 extends TestCase
{
	//instantiated in setUp()
	CashierAgent cashier;
	MockCustomer customer1;
	MockCustomer customer2;
	MockCustomer customer3;
	MockWaiter waiter;
	String orderFood1;
	String orderFood2;
	String orderFood3;
	
	public void setUp() throws Exception {
		super.setUp();		
		cashier = new CashierAgent("cashier");
		waiter = new MockWaiter("waiter");
		customer1 = new MockCustomer("customer1");
		customer2 = new MockCustomer("customer2");
		customer3 = new MockCustomer("flake"); //Created with 0 money
		orderFood1 = "chicken";
		orderFood2 = "fish";
		orderFood3 = "steak";
	}	
	
	/* This tests the cashier receiving three check requests from one waiter. Two of the customers pay their bills and one cannot afford it */
	public void testOneCustomerCheck() {		
		
		//Preconditions
		assertTrue(cashier.checks.isEmpty()); //Cashier should have no bills
		assertEquals(cashier.money, 1000.0); //Cashier starts with $1000
		assertEquals(customer1.money, 100.0); //Customer starts with $100
		assertEquals(customer2.money, 100.0); //Customer starts with $100
		assertEquals(customer3.money, 0.0); //Customer starts with $0
		assertEquals(cashier.stateChange.availablePermits(), 1); //stateChange semaphore should have 1 permit to start with
		assertEquals(cashier.customersWhoOweMoney.size(), 0);
		
		//Send message
		cashier.msgProduceCheck(waiter, customer1, orderFood1); //A request for the cashier to produce a check
		cashier.msgProduceCheck(waiter, customer2, orderFood2); //A request for the cashier to produce a check
		cashier.msgProduceCheck(waiter, customer3, orderFood3); //A request for the cashier to produce a check
		
		//Postconditions of first message
		assertTrue(cashier.checks.size() == 3); //Cashier should now have a single check
		Check check1 = cashier.checks.get(0).c; 
		Check check2 = cashier.checks.get(1).c;
		Check check3 = cashier.checks.get(2).c;
		assertEquals(check1.cust, customer1); //Check should have the correct customer
		assertEquals(check2.cust, customer2); //Check should have the correct customer
		assertEquals(check3.cust, customer3); //Check should have the correct customer
		assertEquals(check1.choice, orderFood1); //Check should have correct choice
		assertEquals(check2.choice, orderFood2); //Check should have correct choice
		assertEquals(check3.choice, orderFood3); //Check should have correct choice
		assertEquals(check1.amount, 10.99); //Check should be created with correct amount of money owed
		assertEquals(check2.amount, 13.99); //Check should be created with correct amount of money owed
		assertEquals(check3.amount, 15.99); //Check should be created with correct amount of money owed
		assertEquals(cashier.stateChange.availablePermits(), 4); //stateChange semaphore should have 4 permits due to the message calls
		
		//Scheduler call
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler should call an action to give bill to waiter
		
		//Postconditions of scheduler call
		assertEquals(cashier.stateChange.availablePermits(), 4); //Still should have 4 permits
		assertTrue(waiter.log.containsString("Received check from cashier.")); //Correct message call in waiter
		assertTrue(cashier.checks.size() == 3); //No more checks added
		
		//Two more scheduler calls
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler call to invoke an action.
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler call to invoke an action.
		
		//Postconditions
		assertEquals(waiter.log.size(), 3); //Message has been called 3 times for waiter
		
		//Messages from customers to pay bill
		cashier.msgPayBill(check1, customer1.money);
		cashier.msgPayBill(check2, customer2.money);
		cashier.msgPayBill(check3, customer3.money);
		
		//Postconditions of message
		assertEquals(cashier.stateChange.availablePermits(), 7); //Extra permits due to message calls
		assertTrue(cashier.checks.size() == 3); //No checks added
		MyCheck mc1 = cashier.checks.get(0);
		MyCheck mc2 = cashier.checks.get(1);
		MyCheck mc3 = cashier.checks.get(2);
		assertEquals(mc1.amountPaid, customer1.money); //Amount paid is all of customer's money
		assertTrue(mc1.state == checkState.fullyPaid); //Check has been paid
		assertEquals(mc2.amountPaid, customer2.money); //Amount paid is all of customer's money
		assertTrue(mc2.state == checkState.fullyPaid); //Check has been paid
		assertEquals(mc3.amountPaid, 0.0); //Amount paid 0
		assertTrue(mc3.state == checkState.partiallyPaid); //Check has not been fully paid!
		
		//Scheduler
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.pickAndExecuteAnAction());
		
		//Postconditions of scheduler
		assertTrue(mc1.state == checkState.finished); //Check exchange is finished
		assertTrue(customer1.log.containsString("Received change from cashier. Leaving restaurant.")); //Change was given to customer1
		assertTrue(mc2.state == checkState.finished); //Check exchange is finished
		assertTrue(customer2.log.containsString("Received change from cashier. Leaving restaurant.")); //Change was given to customer2
		assertEquals(cashier.money, 1000.0 + check1.amount + check2.amount); //Cashier received correct amount of money from check payments
		
		assertEquals(cashier.customersWhoOweMoney.size(), 1); //Flake should have been added to this list.
		MyCustomer mc = cashier.customersWhoOweMoney.get(0);
		assertEquals(mc.c, customer3); //Flake is only member of list - He'll have to pay back his debt next time he comes to the restaurant

		assertFalse(cashier.pickAndExecuteAnAction()); //No more actions to be called by cashier's scheduler!
	}
}
