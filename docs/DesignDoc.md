#Waiter Agent

##DATA

Class MyCustomer {
	Customer c;
	int table;
	CustomerState s;
	string choice;
}

HostAgent host;

CookAgent cook;

List<MyCustomer> customers;

enum CustomerState = { waiting, seated, readyToOrder, askedForOrder, ordered, orderSentToCook,  foodReady, served, finished, leftRestaurant };

Semaphore atDestination;
Semaphore customerAtTable;

##MESSAGES

PleaseSeatCustomer(Customer c, int table) {
	if customer is not in list,
		customers.add(new MyCustomer(c, table, waiting));
	else 
		c.s = waiting;
		c.table = table;
}

ImReadyToOrder(Customer c) {
	MyCustomer mc = customers.find(c);
	mc.s = readyToOrder;
}

HereIsMyChoice(Customer c, string choice) {
	MyCustomer mc = customers.find(c);
	mc.s = ordered;
mc.choice = choice;
}

OrderDone(string choice, int t) {
	MyCustomer mc = customer at table t
	mc.s = foodReady;
}

ImDoneEating(Customer c) {
	MyCustomer mc = customers.find(c);
	mc.s = finished;
}

msgAtDestination() { // Message from animation
	atDestination.release();
}

msgCustomerSatDown() { // Message from animation
	customerAtTable.release();
}

##SCHEDULER

If there is a c in customers such that c.s = finished
	tellHostCustomerIsDone(c);

If there is a c in customers such that c.s = foodReady
	bringFoodToCustomer(c);

If there is a c in customers such that c.s = waiting
	seatCustomer(c);

If there is a c in customers such that c.s = readyToOrder
	takeOrder(c);

If there is a c in customers such that c.s = ordered
	sendOrderToCook(c);

##ACTIONS

seatCustomer(MyCustomer c) {
	c.c.followMe(this, new Menu());
	DoSeatCustomer(c);
	c.s = seated;
	DoLeaveCustomer();
}

takeOrder(MyCustomer c) {
	DoGoToTable(c.table);
	c.c.WhatDoYouWant();
	c.s = askedForOrder;
	DoLeaveCustomer()
}

sendOrderToCook(MyCustomer c) {
	cook.HereIsOrder(this, c.choice, c.table);
	c.s = foodOrdered;
}

bringFoodToCustomer(MyCustomer c) {
	DoGoToCook();

	DoGoToTable(c.tabke);
	c.c.HereIsYourFood(c.choice);
	c.s = served;

	DoLeaveCustomer();
}

tellHostCustomerIsDone(MyCustomer c) {
	host.tableIsFree(c.table, this);
	c.s = leftRestaurant;
	c.table = 0; // Customer is not at a table
}
