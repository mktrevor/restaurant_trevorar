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

###How to run non-normatives!
  + It's a good idea to restart the program for some of the scenarios. There is no way to add more inventory to markets right now, so not everything can be tested in one run-through. Market 3 starts off with a high supply which shouldn't run out unless the user presses the "empty Market 3" button.

####Waiter Breaks and Hungry Customers
  + To make customers hungry again, check the "Hungry?" checkbox in the infoPanel at the bottom. 
  + To make a waiter ask for a break or finish a break, use the break checkbox in the infoPanel. You may have to click the waiter's name again to make the checkbox refresh.
  + A waiter will always be refused a break if he is the only one working.
  + You must press the checkbox to make a waiter finish his or her break. Otherwise they will never return to work.

--------

####Cook/Market Ordering
  + The "Inventory Check" button on the top panel will remove some food from the cook's inventory and have him recheck the inventory to demonstrate the normative ordering scenario (no customers). Otherwise, the cook starts off with sufficient foods.
  + The buttons on the top of the window can be used to clear out the inventory of a market so that the cook will have to order from another or so that the cook can't order any more food at all. 
  + Also, the "food = 0" buttons can clear out the cook's inventory so that customers have to reorder or have no options left.
  + Note: If you clear the cook's inventory, make sure there are no orders on the way. If there are, you'll have to clear the inventory again after the order arrives.

--------

####Customer Name Hacks
  + Normal customers start with a random amount of money between $10 and $60, so they can almost always afford their first meal. You can add/remove money with the buttons in the infoPanel when a customer is selected.
  + Usually, if a customer can't afford any items, they will leave. However, this is not the case if the customer's name is "flake". See below.
  + A customer named "normative" will start with $100, ensuring that he can buy the food of his choice.
  + A customer named "steak", "chicken", or "fish" will always order that food unless it runs out and they have to reorder.
  + A customer named "cheap" will start with $11.00 - just enough to buy the cheapest item (chicken). He will leave if chicken is out of stock because he can't afford anything else.
  + A customer named "flake" will start with $0 and will still order a food item. The cashier will let him leave when he can't pay, but if he comes back, the cashier will add his outstanding debt to his new check.
  + A customer named "broke" will start with $0 but will leave because all of the choices are too expensive.
  + A customer named "patient" will wait for an opening if the restaurant is full.
  + A customer named "impatient" will leave if the restaurant is full.

--------

  + You can use all of these hacks together to test any of the non-normatives. Please feel free to email me if there's a scenario you can't figure out how to test!