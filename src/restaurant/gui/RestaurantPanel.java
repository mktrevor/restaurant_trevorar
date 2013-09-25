package restaurant.gui;

import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Sarah");
    private HostGui hostGui = new HostGui(host);
    
    private CookAgent cook = new CookAgent("Chef");
    private CookGui cookGui = new CookGui(cook);

    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host.setGui(hostGui);

        //gui.animationPanel.addGui(hostGui);
        host.startThread();
        cook.startThread();

        setLayout(new BorderLayout(0, 0));
        group.setLayout(new GridLayout(1, 3, 1, 1));

        group.add(customerPanel);
        group.add(waiterPanel);

        initRestLabel();
        add(restLabel, BorderLayout.WEST);
        add(group, BorderLayout.CENTER);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Fish</td><td>$13.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("    "), BorderLayout.EAST);
        restLabel.add(new JLabel("    "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if(type.equals("Waiters")) {
        	for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, boolean isHungry) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);
    		if(isHungry) {
    			g.setHungry();
    		}

    		gui.animationPanel.addGui(g);
    		c.setHost(host);
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    	}
    	if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name);
    		WaiterGui g = new WaiterGui(w);
    		
    		gui.animationPanel.addGui(g);
    		w.setHost(host);
    		w.setCook(cook);
    		w.setGui(g);
    		host.addWaiter(w);
    		waiters.add(w);
    		w.startThread();
    	}
    }
    
    public void pause() {
    	cook.pause();
    	host.pause();
    	for(CustomerAgent c : customers) {
    		c.pause();
    	}
    	for(WaiterAgent w : waiters) {
    		w.pause();
    	}
    }
    
    public void resume() {
    	cook.resume();
    	host.resume();
    	for(CustomerAgent c : customers) {
    		c.resume();
    	}
    	for(WaiterAgent w : waiters) {
    		w.resume();
    	}
    }

}
