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

/* A class to test a complex scenario involving 1 cashier, 2 waiters, 1 markets, and 4 customers */

public class CashierMixedTest2 extends TestCase
{
	//instantiated in setUp()
	CashierAgent cashier;
	MockCustomer customer1;
	MockCustomer customer2;
	MockCustomer customer3;
	MockCustomer customer4;
	MockWaiter waiter1;
	MockWaiter waiter2;
	String orderFood1;
	String orderFood2;
	String orderFood3;
	MockMarket market1;
	double billAmount1;
	
	public void setUp() throws Exception {
		super.setUp();		
		cashier = new CashierAgent("cashier");
		waiter1 = new MockWaiter("waiter1");
		waiter2 = new MockWaiter("waiter2");
		
		customer1 = new MockCustomer("customer1");
		customer2 = new MockCustomer("customer2");
		customer3 = new MockCustomer("customer3");
		customer4 = new MockCustomer("customer4");
		orderFood1 = "chicken";
		orderFood2 = "fish";
		orderFood3 = "steak";		
		
		market1 = new MockMarket("market1");
		
		billAmount1 = 135.40;
	}	
	
	/* This tests the cashier receiving three check requests from one waiter. Two of the customers pay their bills and one cannot afford it 
	 * while also testing two different markets sending bills for recent orders. 
	 */
	public void testOneCustomerCheck() {		
		
		//Preconditions
		assertTrue(cashier.checks.isEmpty()); //Cashier should have no bills
		
		assertEquals(cashier.money, 1000.0); //Cashier starts with $1000
		assertEquals(customer1.money, 100.0); //Customer starts with $100
		assertEquals(customer2.money, 100.0); //Customer starts with $100
		assertEquals(customer3.money, 100.0); //Customer starts with $100
		assertEquals(customer4.money, 100.0); //Customer starts with $100
		
		assertEquals(cashier.stateChange.availablePermits(), 1); //stateChange semaphore should have 1 permit to start with
		assertEquals(cashier.customersWhoOweMoney.size(), 0);
		assertTrue(cashier.marketBills.isEmpty()); //Cashier should have no bills
		assertEquals(market1.money, 1000.0); //Market starts with $1000
		
		//Send 1 message from each waiter
		cashier.msgProduceCheck(waiter1, customer1, orderFood1); //A request for the cashier to produce a check
		cashier.msgProduceCheck(waiter2, customer2, orderFood2); //A request for the cashier to produce a check
		
		//Postconditions of waiter message calls
		assertTrue(cashier.checks.size() == 2); //Cashier should now have two checks
		Check check1 = cashier.checks.get(0).c; 
		Check check2 = cashier.checks.get(1).c;
		assertEquals(check1.cust, customer1); //Check should have the correct customer
		assertEquals(check2.cust, customer2); //Check should have the correct customer
		assertEquals(check1.choice, orderFood1); //Check should have correct choice
		assertEquals(check2.choice, orderFood2); //Check should have correct choice
		assertEquals(check1.amount, 10.99); //Check should be created with correct amount of money owed
		assertEquals(check2.amount, 13.99); //Check should be created with correct amount of money owed
		assertEquals(cashier.stateChange.availablePermits(), 3); //stateChange semaphore should have 3 permits due to the message calls
		
		//Message from market about a new bill
		cashier.msgYouOwe(market1, billAmount1); //The first bill for the cashier from market1
		
		//Postconditions of market message		
		assertTrue(cashier.marketBills.size() == 1); //Cashier should now have one bill
		MarketBill bill = cashier.marketBills.get(0); 
		assertEquals(bill.m, market1); //Bill should be associated with the market
		assertEquals(bill.amountOwed, billAmount1);		

		//Scheduler calls - Waiter has higher priority
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler should call an action to give check to waiter1 for customer1
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler should give check to 2nd waiter for customer2
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler call to pay bill from market
		
		//Postconditions of scheduler call
		assertEquals(cashier.stateChange.availablePermits(), 4); //Should now have 4 permits
		assertTrue(waiter1.log.containsString("Received check from cashier.")); //Correct message call in waiter
		assertEquals(waiter1.log.size(), 1); //3 messages called in waiter
		assertTrue(waiter2.log.containsString("Received check from cashier.")); //Correct message call in waiter
		assertEquals(waiter2.log.size(), 1); //3 messages called in waiter
		assertTrue(cashier.checks.size() == 2); //No more checks added
		assertTrue(market1.log.containsString("Received payment for order.")); //Market received payment message
		assertEquals(market1.log.size(), 1); //Market has only received 1 message
		assertEquals(waiter1.log.size(), 1); //1 message
		assertEquals(waiter2.log.size(), 1); //1 message
		assertTrue(cashier.marketBills.size() == 0); //All bills from market are now paid off.
		assertEquals(cashier.money, 1000.0 - billAmount1); //Cashier's money goes down
		assertEquals(market1.money, 1000.0 + billAmount1); //Market's money goes up 
		
		//More messages to create more checks for customers
		cashier.msgProduceCheck(waiter1, customer3, orderFood3); //A request for the cashier to produce a check for customer3
		cashier.msgProduceCheck(waiter2, customer4, orderFood1); //A request for the cashier to produce a check for customer4
		
		//Postconditions
		assertTrue(cashier.checks.size() == 4); //Cashier should now have two checks
		Check check3 = cashier.checks.get(2).c; 
		Check check4 = cashier.checks.get(3).c;
		assertEquals(check3.cust, customer3); //Check should have the correct customer
		assertEquals(check4.cust, customer4); //Check should have the correct customer
		assertEquals(check3.choice, orderFood3); //Check should have correct choice
		assertEquals(check4.choice, orderFood1); //Check should have correct choice
		assertEquals(check3.amount, 15.99); //Check should be created with correct amount of money owed
		assertEquals(check4.amount, 10.99); //Check should be created with correct amount of money owed
		assertEquals(cashier.stateChange.availablePermits(), 6); //Should now have 6 permits
		
		//Two more scheduler calls - giving checks to waiters 1 and 2
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler call to invoke an action
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler call to invoke an action
		
		//Checking postconditions for scheduler
		assertTrue(waiter1.log.containsString("Received check from cashier.")); //Correct message call in waiter
		assertEquals(waiter1.log.size(), 2); //2 messages called in waiter
		assertTrue(waiter2.log.containsString("Received check from cashier.")); //Correct message call in waiter
		assertEquals(waiter2.log.size(), 2); //2 messages called in waiter
		assertTrue(cashier.checks.size() == 4); //No more checks added
		
		//Messages from customers to pay bill
		cashier.msgPayBill(check1, customer1.money);
		cashier.msgPayBill(check2, customer2.money);
		cashier.msgPayBill(check3, customer3.money);
		cashier.msgPayBill(check4, customer4.money);
		
		//Postconditions of message
		assertEquals(cashier.stateChange.availablePermits(), 10); //Extra permits due to message calls
		assertTrue(cashier.checks.size() == 4); //No checks added
		MyCheck mc1 = cashier.checks.get(0);
		MyCheck mc2 = cashier.checks.get(1);
		MyCheck mc3 = cashier.checks.get(2);
		MyCheck mc4 = cashier.checks.get(3);
		assertEquals(mc1.amountPaid, customer1.money); //Amount paid is all of customer's money
		assertTrue(mc1.state == checkState.fullyPaid); //Check has been paid
		assertEquals(mc2.amountPaid, customer2.money); //Amount paid is all of customer's money
		assertTrue(mc2.state == checkState.fullyPaid); //Check has been paid
		assertEquals(mc3.amountPaid, customer3.money); //Amount paid 0
		assertTrue(mc3.state == checkState.fullyPaid); //Check has been fully paid!
		assertEquals(mc4.amountPaid, customer4.money); //Amount paid 0
		assertTrue(mc4.state == checkState.fullyPaid); //Check has been fully paid!
		
		//Scheduler - call twice now
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.pickAndExecuteAnAction());
		
		//Postconditions of scheduler
		assertTrue(mc1.state == checkState.finished); //Check exchange is finished
		assertTrue(customer1.log.containsString("Received change from cashier. Leaving restaurant.")); //Change was given to customer1
		assertTrue(mc2.state == checkState.finished); //Check exchange is finished
		assertTrue(customer2.log.containsString("Received change from cashier. Leaving restaurant.")); //Change was given to customer2
		assertEquals(cashier.money, 1000.0 - billAmount1 + check1.amount + check2.amount); //Cashier received correct amount of money from check payments
		
		
		//Final two calls of scheduler
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.pickAndExecuteAnAction());
		
		//Postconditions of scheduler
		assertTrue(mc3.state == checkState.finished); //Check exchange is finished
		assertTrue(customer3.log.containsString("Received change from cashier. Leaving restaurant.")); //Change was given to customer1
		assertTrue(mc4.state == checkState.finished); //Check exchange is finished
		assertTrue(customer4.log.containsString("Received change from cashier. Leaving restaurant.")); //Change was given to customer2
		assertEquals(cashier.money, 1000.0 - billAmount1 + check1.amount + check2.amount + check3.amount + check4.amount); //Cashier received correct amount of money from check payments
		
		assertEquals(cashier.stateChange.availablePermits(), 10); //Still 10 permits available
		assertEquals(cashier.customersWhoOweMoney.size(), 0); //All customers paid their checks completely
		
		assertFalse(cashier.pickAndExecuteAnAction()); //No more actions to be called by cashier!
	}
}
