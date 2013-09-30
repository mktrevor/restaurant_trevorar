#Restaurant V2 Agent Documentation

##Waiter Agent

###DATA

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

###MESSAGES

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

###SCHEDULER

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

###ACTIONS

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
	DoGoToTable(c.table);
	c.c.HereIsYourFood(c.choice);
	c.s = served;
	DoLeaveCustomer();
}

tellHostCustomerIsDone(MyCustomer c) {
	host.tableIsFree(c.table, this);
	c.s = leftRestaurant;
	c.table = 0; // Customer is not at a table
}




##Host Agent

###DATA

List<MyCustomer> customers;

List<MyWaiter> waiters;

class MyCustomer {
	Customer c;
	boolean waiting = true;
}

class MyWaiter {
	Waiter w;
	int numCustomers = 0; // Keeps track of how many customers the waiter is serving
}

List<Table> tables;

class Table {
	int tableNumber;
	boolean occupied;
}

###MESSAGES

ImHungry(Customer c) {
	if c is not in customers
		customers.add(new MyCustomer(c));
	else
		c.s = waiting;
}

TableIsFree(int table, Waiter w) {
	Table table = tables.find(table);
	table.occupied = false;

	MyWaiter waiter = waiters.find(w);
	w.numCustomers--;
	// decrease number of customers that waiter is dealing with by 1
}

###SCHEDULER

if there is a c in customers such that c.s = waiting;
	if there is a t in tables such that t.occupied = false;
		if waiters is not empty
			seatCustomer(c, t)
			
###ACTIONS

seatCustomer(MyCustomer c, Table table) {
MyWaiter w = waiters.findLeastBusyWaiter(w)
	// This part finds the waiter with the lowest numCustomers

// Assigns the customer to the least busy waiter
	w.PleaseSeatCustomer(c.c, table.tableNumber);
	w.numCustomers++; // Waiter has one more customer
	c.s = seated;
	table.occupied = true;
}




##Customer Agent

###DATA

string choice;

Timer timer; // Timer for eating food

enum agentState { doingNothing, waitingInRestaurant, beingSeated, seated, ordering, eating, doneEating, Leaving };

enum agentEvent { none, gotHungry, followWaiter, seated, askedToOrder, ordered, startedEating, doneEating, doneLeaving }

agentState state = doingNothing;
agentEvent event = none;

HostAgent host; 
WaiterAgent waiter;

###MESSAGES

GotHungry() {
	event = gotHungry;
}

FollowMe(Waiter w, Menu m) {
	waiter = w;
	event = followWaiter;
}

msgAnimationFinishedGoToSeat() {
	event  = seated;
}

WhatDoYouWant() {
	event = askedToOrder;
}

HereIsYourFood(string choice) {
	event = startedEating;
}
	
msgAnimationDoneEatingFood() {
	event = doneEating;
}

###SCHEDULER

if state = doingNothing and event = gotHungry
	state = waitingInRestaurant;
	goToRestaurant();

if state = waitingInRestaurant and event = followWaiter
	state = beingSeated;
	sitDown();

if state = beingSeated and event = seated
	state = seated;
	readyToOrder();

if state = seated and event = askedToOrder
	state = ordering;
	orderFood();

if state = ordering and event = startedEating
	state = eating;
	eatFood();

if state = eating and event = doneEating
	state = doneEating;
	tellWaiterImDone();

if state = doneEating and event = doneLeaving
	state = leaving;
	leaveRestaurant();
	
if state = leaving and event = doneLeaving
	state = doingNothing
	event = none


###ACTIONS

goToRestaurant() {
	DoGoToRestaurant();
	host.ImHungry(this);
}

sitDown() {
	DoGoToSeat();
}

readyToOrder() {
	run timer for a few seconds
	waiter.ImReadyToOrder(this);
}

orderFood() {
	choice = random food; // Steak, chicken, or fish
	// Choice is chosen based on the first letter of the customer’s name
	waiter.HereIsMyChoice(this, choice);
}

eatFood() {
	DoEatingFood();
	run timer for a few seconds
	event = doneEating;
}

tellWaiterImDone() {
	waiter.ImDoneEating(this);
	event = doneLeaving;
}

leaveRestaurant() {
	DoLeaveRestaurant();
}




##Cook Agent

###DATA

List<Order> orders;

class Order {
	Waiter w;
	String choice;
	int table;
	OrderState s;
}

enum OrderState { pending, cooking, cooked, finished };

Timer timer; // For cooking times

Class Food {
	string type;
	int cookingTime;
}

map(string, Food) foods; // For cooking times of each food

###MESSAGES

HereIsOrder(Waiter w, string choice, int table) {
	orders.add(new Order(w, choice, table, pending));
}

foodDone(Order o) {
	o.s = cooked;
}

###SCHEDULER

if there is an o in orders such that o.s = cooked
	then plateIt(o);
if there is an o in orders such that o.s = pending
	then cookIt(o);


###ACTIONS

CookIt(Order o) {
	DoCooking(o); // Animation and print statements
	o.s = cooking;

	timer.start( run(foodDone(o)), foods.get(o.choice).cookingTime);
}

PlateIt(Order o) {
	DoPlating(o); // Animation and print statements
	o.w.OrderDone(o.choice, o.table);
	o.s = finished;
}
