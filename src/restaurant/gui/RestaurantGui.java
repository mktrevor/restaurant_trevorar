package restaurant.gui;

import restaurant.CustomerAgent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener, ChangeListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	//AnimationPanel animationPanel = new AnimationPanel();
	
	
	
    /* restPanel holds 3 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the Animation Panel
     * 3) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    AnimationPanel animationPanel = new AnimationPanel();
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    
    private JLabel pauseLabel;
    private JLabel hungryLabel;
    private JButton makeHungry;
    private JPanel optionPanel;
    private JButton pauseButton;
    private JButton resumeButton;
    private JLabel speedLabel;
    private JSlider speedSlider;
    
    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */
    private final int WINDOWX = 1200;
    private final int WINDOWY = 800;
    private final int WINDOW_X_COORD = 50;
    private final int WINDOW_Y_COORD = 50;
    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        

        /*animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
        animationFrame.setVisible(true);
    	animationFrame.add(animationPanel); */
    	
    	setBounds(WINDOW_X_COORD, WINDOW_Y_COORD, WINDOWX, WINDOWY);

        //setLayout(new BoxLayout((Container) getContentPane(), 
        //		BoxLayout.Y_AXIS));

    	setLayout(new BorderLayout());
    	
        Dimension restDim = new Dimension(WINDOWX/3, (int) (WINDOWY * .6));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel, BorderLayout.WEST);
        
        Dimension animationDim = new Dimension(WINDOWX/2, (int) (WINDOWY * .75));
        animationPanel.setPreferredSize(animationDim);
        add(animationPanel, BorderLayout.CENTER);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX/3, (int) (WINDOWY * .2));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        
        add(infoPanel, BorderLayout.SOUTH);
        
        //Extra checkbox to make customer hungry again
        makeHungry = new JButton("I'm hungry!");
        makeHungry.setEnabled(false);
        makeHungry.addActionListener(this);
        
        //The pause/resume buttons;
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(this);
        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(this);
        resumeButton.setEnabled(false);
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 30, animationPanel.getTimerInterval());
        speedSlider.addChangeListener(this);
        
        hungryLabel = new JLabel("Make hungry (select customer): ");
        pauseLabel = new JLabel("                Pause/Resume: ");
        speedLabel = new JLabel("                Animation Speed: ");
        
        optionPanel = new JPanel();
        
        optionPanel.add(hungryLabel);
        optionPanel.add(makeHungry);
        optionPanel.add(pauseLabel);
        optionPanel.add(pauseButton);
        optionPanel.add(resumeButton);
        optionPanel.add(speedLabel);
        optionPanel.add(speedSlider);
        
        add(optionPanel, BorderLayout.NORTH);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
            makeHungry.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
                makeHungry.setEnabled(false);
            }
        }
        if(e.getSource() == makeHungry) {
        	if(currentPerson instanceof CustomerAgent) {
        		CustomerAgent c = (CustomerAgent) currentPerson;
        		c.getGui().setHungry();
        		makeHungry.setEnabled(false);
                stateCB.setSelected(c.getGui().isHungry());
        		stateCB.setEnabled(false);
        	}
        }
        if(e.getSource() == pauseButton) {
        	restPanel.pause();
        	pauseButton.setEnabled(false);
        	resumeButton.setEnabled(true);
        }
        if(e.getSource() == resumeButton) {
        	restPanel.resume();
        	resumeButton.setEnabled(false);
        	pauseButton.setEnabled(true);
        }
    }
    
    public void stateChanged(ChangeEvent e) {
    	JSlider slider = (JSlider) e.getSource();
    	if(!slider.getValueIsAdjusting()) {
    		animationPanel.setSpeed((int) (31 - slider.getValue())); // Sets the animation panel timer interval between 1 and 30
    	}
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("Restaurant V2");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
