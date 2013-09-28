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
    private static final int TIMER_INTERVAL = 20;
    
    private ImageIcon tableIcon = new ImageIcon("images/table.png");
    private final int NUM_TABLES = 4;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
    	setPreferredSize(new Dimension(WINDOWX, WINDOWY));
    	setMaximumSize(new Dimension(WINDOWX, WINDOWY));
    	setMinimumSize(new Dimension(WINDOWX, WINDOWY));
        setVisible(true);
    	
        Timer timer = new Timer(TIMER_INTERVAL, this);
        timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(new Color(190, 190, 190));
		g2.fillRect(0, 0, WINDOWX, WINDOWY );
		
		g2.setColor(Color.BLACK);
		for(int i = 0; i < 70; i++) {
			//g2.drawLine(0, 40 * i, 800, 40 * i);
			//g2.drawLine(40 * i, 0, 40 * i, 600);
			g2.drawLine(20 * i, 0, 0, 20 * i);
		}
		for(int i = -40; i < 40; i++) {
			g2.drawLine(0, 20 * i, 800, 20 * i + 800);
		}
		
		g2.setColor(Color.BLACK);
		g2.fillRect(250,  5, 250, 40);
		g2.setColor(Color.WHITE);
		g2.fillRect(252, 7, 246, 36);
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Verdana", Font.BOLD, 24));
		g2.drawString("Restaurant V2", 275, 35);
        
		//Drawing tables
        g2.setColor(new Color(105, 75, 35)); // RGB values for brown
		g2.fillRect(200, 200, 100, 60);
		g2.fillRect(450, 200, 100, 60);
		g2.fillRect(200, 400, 100, 60);
		g2.fillRect(450, 400, 100, 60);
		
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
	}

    /*public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLE_X_COORD, TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);//200 and 250 need to be table params
        
        g2.fillRect(3 * TABLE_X_COORD, TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);
        g2.fillRect(TABLE_X_COORD, 2 * TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);
        g2.fillRect(3 * TABLE_X_COORD, 2 * TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);
        

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }*/

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
}
