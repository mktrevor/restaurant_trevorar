package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {

	public JScrollPane pane =
			new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JPanel view = new JPanel();
	private List<JButton> list = new ArrayList<JButton>();
	private JButton addPersonB = new JButton("Add");
	private JTextField enterName;
	private JCheckBox hungryChk;
	private JLabel nameLabel;

	private RestaurantPanel restPanel;
	private String type;

	/**
	 * Constructor for ListPanel.  Sets up all the gui
	 *
	 * @param rp   reference to the restaurant panel
	 * @param type indicates if this is for customers or waiters
	 */
	public ListPanel(RestaurantPanel rp, String type) {
		restPanel = rp;
		this.type = type;

		setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
		add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

		nameLabel = new JLabel("Enter name here:");
		enterName = new JTextField();
		enterName.setMaximumSize(new Dimension(200, 10)); // Size of text field to enter customer/waiter names
		hungryChk = new JCheckBox("Hungry?");
		hungryChk.setEnabled(false);

		enterName.addKeyListener(
			new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
				}

				public void keyTyped(KeyEvent e) {
				}

				public void keyPressed(KeyEvent e) {
					hungryChk.setEnabled(true);
				}
			}
		);

		add(nameLabel);
		add(enterName);
		if(type == "Customers")
			add(hungryChk);
		addPersonB.addActionListener(this);
		add(addPersonB);

		view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
		pane.setViewportView(view);
		add(pane);
	}

	/**
	 * Method from the ActionListener interface.
	 * Handles the event of the add button being pressed
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addPersonB) {
			if(enterName.getText().trim().length() != 0) {
				addPerson(enterName.getText().trim(), hungryChk.isSelected());
				enterName.setText("");
				hungryChk.setEnabled(false);
				hungryChk.setSelected(false);
			}
			else {
				hungryChk.setEnabled(false);
				hungryChk.setSelected(false);
			}
		}
		else if(e.getSource() == enterName) {
			hungryChk.setEnabled(true);
		}
		else {
			for (JButton temp:list){
				if (e.getSource() == temp)
					restPanel.showInfo(type, temp.getText());
			}
		}
	}

	/**
	 * If the add button is pressed, this function creates
	 * a spot for it in the scroll pane, and tells the restaurant panel
	 * to add a new person.
	 *
	 * @param name name of new person
	 */
	public void addPerson(String name, boolean isHungry) {
		if (name != null) {
			JButton button = new JButton(name);
			button.setBackground(Color.white);

			Dimension paneSize = pane.getSize();
			Dimension buttonSize = new Dimension(paneSize.width - 20,
					(int) (paneSize.height / 7));
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			button.addActionListener(this);
			list.add(button);
			view.add(button);
			restPanel.addPerson(type, name, isHungry);//puts customer/waiter on list
			restPanel.showInfo(type, name);//puts hungry button on panel
			validate();
		}
	}
}
