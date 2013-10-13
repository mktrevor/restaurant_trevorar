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
  + To make customers hungry again, check the "Hungry?" checkbox in the infoPanel at the bottom. 
  + To make a waiter ask for a break or finish a break, use the break checkbox in the infoPanel. You may have to click the waiter's name again to make the checkbox refresh.
  + A waiter will always be refused a break if he is the only one working.
  + You must press the checkbox to make a waiter finish his or her break. Otherwise they will never return to work.

--------

  + The buttons on the top of the window can be used to clear out the inventory of a market so that the cook will have to order from another or so that the cook can't order any more food at all. 
  + Also, the buttons can clear out the cook's inventory so that customers have to reorder.
  + Note: the cook always gets a delivery near the beginning, so make sure you set his inventory to 0 after the delivery.

--------

  + Normal customers start with a random amount of money between $0 and $50. You can add/remove money with the buttons in the infoPanel when a customer is selected.
  + A customer named "normative" will start with $100 - plenty to buy anything for a few rounds.
  + A customer named "steak", "chicken", or "fish" will always order that food unless it runs out and they have to reorder.
  + A customer named "cheap" will start with $11.00 - just enough to buy the cheapest item (chicken). He will leave if chicken is out of stock because he can't afford anything else.
  + A customer named "flake" will start with $0 and will still order a food item. The cashier will let him leave when he can't pay, but if he comes back, the cashier will add his outstanding debt to his new check.
  + A customer named "broke" will start with $0 but will leave because all of the choices are too expensive.
  + A customer named "patient" will wait for an opening if the restaurant is full.
  + A customer named "impatient" will leave if the restaurant is full.

--------

  + You can use all of these hacks together to test any of the non-normatives. Please feel free to email me if there's a scenario you can't figure out how to test!