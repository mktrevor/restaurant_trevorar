package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private static final int WINDOWX = 800;
    private static final int WINDOWY = 600;
    private static final int TIMER_INTERVAL = 15;
    
    private final int NUM_TABLES = 4;

    private List<Gui> guis = new ArrayList<Gui>();
    
    private Timer timer;

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
    	setPreferredSize(new Dimension(WINDOWX, WINDOWY));
    	setMaximumSize(new Dimension(WINDOWX, WINDOWY));
    	setMinimumSize(new Dimension(WINDOWX, WINDOWY));
        setVisible(true);
    	
        timer = new Timer(TIMER_INTERVAL, this);
        timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(190, 190, 190)); // Background color = gray
		g2.fillRect(0, 0, WINDOWX, WINDOWY );
		
		// Drawing a pattern on the ground
		g2.setColor(Color.BLACK);
		for(int i = 0; i < 70; i++) {
			g2.drawLine(20 * i, 0, 0, 20 * i);
		}
		for(int i = -40; i < 40; i++) {
			g2.drawLine(0, 20 * i, 800, 20 * i + 800);
		}
		// End of floor pattern code
        
		// Drawing tables at the correct locations
        g2.setColor(new Color(105, 75, 35)); // RGB values for brown
		g2.fillRect(200, 200, 100, 60);
		g2.fillRect(450, 200, 100, 60);
		g2.fillRect(200, 400, 100, 60);
		g2.fillRect(450, 400, 100, 60);
		
		// Table outlines
		g2.setColor(Color.BLACK);
		g2.drawRect(200, 200, 100, 60);
		g2.drawRect(450, 200, 100, 60);
		g2.drawRect(200, 400, 100, 60);
		g2.drawRect(450, 400, 100, 60);
		
		for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
		
		for(Gui gui : guis) {
			gui.draw(g2);
		}
		
		// This draws a rectangular "Restaurant V2" sign
		g2.setColor(Color.BLACK);
		g2.fillRect(250,  5, 250, 40); // Size and position of restaurant sign border
		g2.setColor(Color.WHITE);
		g2.fillRect(252, 7, 246, 36); // Size and position of restaurant sign
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Verdana", Font.BOLD, 24));
		g2.drawString("Restaurant V2.1", 265, 35); // Position of text on restaurant sign
	}

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }

	public void addGui(WaiterGui gui) {
		guis.add(gui);
	}
	
	public void addGui(CookGui gui) {
		guis.add(gui);
	}
	
	public void addGui(CashierGui gui) {
		guis.add(gui);
	}
	
	public int getTimerInterval() {
		return TIMER_INTERVAL;
	}
	
	public void setSpeed(int s) {
		timer.stop();
		timer = new Timer(s, this);
		timer.start();
	}
}
