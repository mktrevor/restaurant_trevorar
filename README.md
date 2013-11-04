##Restaurant Project Repository

###Student Information
  + Name: Trevor Reed
  + USC Email: trevorar@usc.edu
  + USC ID: 5689597253
  + Section: MW - 4 PM
  + Lab: Tuesday - 6 PM

###Compiling/Running instructions
  + This program should be run in Eclipse
  + 1. Clone this repository.
  + 2. In eclipse, click File->New->Other and then choose Java Project from Existing Ant Buildfile.
  + 3. Click browse, find the cloned repository, and choose the build.xml file.
  + 4. Check the "Link to the buildfile in the file system box" and press finish.
  + 5. If it does not run when you try to run it, make sure that in Run->Run Configurations, the Main Class is set to "restaurant.gui.RestaurantGui".

###Other issues
  + As far as I know, everything in this project works. I've tried all of the scenarios listed in the rubric as well as trying to overload it with many waiters/customers, but it has handled them all.
  + If you encounter any other problems, feel free to email me!

###How to run scenarios!
  + It's a good idea to restart the program for some of the scenarios.

####Cashier/Market interaction
  + In order to test the scenario "One order, fulfilled by TWO markets, 2 bills paid in full," start the program, then click the "Clear Cook Inventory" button and then make a customer and a waiter. When the customer orders, the cook will have to order more food. The first market doesn't have enough to fulfill the full order so a second order is needed.
  + To test the "normative market ordering scenario," either make a lot of customers named "chicken" until the cook has to order more or simply start the program and press the "Inventory check" button.
  + I did not implement a solution for bills that the cashier can't pay for. I left the cashier plenty of money for many orders.
  
####Animation upgrades
  + My waiting area for customers only has room for 15 customers. Also, customers won't go to the waiting room unless all the tables are full.
  + I did implement the extra credit for the cook animation

####JUnit Testing
######Here are the scenarios that my tests cover
  + CashierCustomerTest1: One check request from one waiter to be paid by one customer
  + CashierCustomerTest2: Three check requests from one waiter. Two customers will pay and the third will order food even though he has no money
  + CashierMarketTest1: Cashier receives a single bill from one market and pays it
  + CashierMarketTest2: Cashier receives two bills from one order fulfilled by two markets and pays them both
  + CashierMixedTest1: One waiter sends 3 check requests for two customers who can pay and one who can't. Also, 2 markets send bills at the same time to the cashier.
  + CashierMixedTest2: Two waiters request checks for 2 customers each, and one market sends a bill to the cashier at the same time.

--------

####Other notes
  + To make customers hungry again, check the "Hungry?" checkbox in the infoPanel at the bottom. 
  + To make a waiter ask for a break or finish a break, use the break checkbox in the infoPanel. You may have to click the waiter's name again to make the checkbox refresh. You must press the checkbox to make a waiter finish his or her break. Otherwise they will never return to work.
  + Normal customers start with a random amount of money between $10 and $60, so they can almost always afford their first meal. You can add/remove money with the buttons in the infoPanel when a customer is selected.
  + Usually, if a customer can't afford any items, they will leave. However, this is not the case if the customer's name is "flake". See below.
  + A customer named "normative" will start with $100, ensuring that he can buy the food of his choice.
  + A customer named "steak", "chicken", or "fish" will always order that food unless it runs out and they have to reorder.
  + A customer named "cheap" will start with $11.00 - just enough to buy the cheapest item (chicken). He will leave if chicken is out of stock because he can't afford anything else.
  + A customer named "flake" will start with $0 and will still order a food item. The cashier will let him leave when he can't pay, but if he comes back, the cashier will add his outstanding debt to his new check.
  + A customer named "broke" will start with $0 but will leave because all of the choices are too expensive.
  + A customer named "patient" will wait for an opening if the restaurant is full.
  + A customer named "impatient" will leave if the restaurant is full.