package restaurant.test;

import restaurant.CashierAgent;
import restaurant.CashierAgent.MarketBill;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;

import junit.framework.*;

/* A class to test a scenario with two markets and one cashier */

public class CashierMarketTest2 extends TestCase
{
	//instantiated in setUp()
	CashierAgent cashier;
	MockMarket market1;
	MockMarket market2;
	double billAmount1;
	double billAmount2;
	
	public void setUp() throws Exception {
		super.setUp();		
		cashier = new CashierAgent("cashier");
		market1 = new MockMarket("market1");
		market2 = new MockMarket("market2");
		
		billAmount1 = 75.0;
		billAmount2 = 120.50;
	}	
	/* This tests the cashier receiving two separate bills from two markets and paying them off one after another */
	public void testOneMarketBill() {		
		
		//Preconditions
		assertTrue(cashier.marketBills.isEmpty()); //Cashier should have no bills
		assertEquals(cashier.money, 1000.0); //Cashier starts with $1000
		assertEquals(market1.money, 1000.0);
		assertEquals(market2.money, 1000.0);
		assertEquals(cashier.stateChange.availablePermits(), 1); //stateChange semaphore should have 1 permit to start with
		
		//Send message
		cashier.msgYouOwe(market1, billAmount1); //The first bill for the cashier from market1
		
		//Postconditions of first message
		assertTrue(cashier.marketBills.size() == 1); //Cashier should now have a single bill
		MarketBill bill = cashier.marketBills.get(0); 
		assertEquals(bill.m, market1); //Bill should be associated with the market
		assertEquals(bill.amountOwed, billAmount1);
		assertEquals(cashier.stateChange.availablePermits(), 2); //stateChange semaphore should have 2 permits
		
		//Second bill message
		cashier.msgYouOwe(market2, billAmount2);
		
		//Postconditions of second message
		assertTrue(cashier.marketBills.size() == 2); //Cashier should now have two bills
		MarketBill bill2 = cashier.marketBills.get(1); 
		assertEquals(bill2.m, market2); //Bill2 should be associated with the market2
		assertEquals(bill2.amountOwed, billAmount2);
		assertEquals(cashier.stateChange.availablePermits(), 3); //stateChange semaphore should have 3 permits
		
		//Scheduler call
		assertTrue(cashier.pickAndExecuteAnAction()); //scheduler should call an action to pay the bill
		assertTrue(cashier.pickAndExecuteAnAction()); //schedule should call action to pay second bill
		
		//Final postconditions
		assertEquals(cashier.marketBills.size(), 0); //No bills left to pay
		assertEquals(cashier.stateChange.availablePermits(), 3); //Still should have 3 permits
		assertTrue(market1.log.containsString("Received payment for order.")); //Log should have a record of this message call
		assertTrue(market2.log.containsString("Received payment for order."));
		assertEquals(cashier.money, 1000.0 - billAmount1 - billAmount2); //Cashier's money goes down
		assertEquals(market1.money, 1000.0 + billAmount1); //Market's money goes up 
		assertEquals(market2.money, 1000.0 + billAmount2); //Market's money goes up

		assertFalse(cashier.pickAndExecuteAnAction()); //No more actions to be called by cashier's scheduler!
	}
}
