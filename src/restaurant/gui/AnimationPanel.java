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
    private static final int TABLE_LENGTH = 50, TABLE_WIDTH = 50;
    private static final int TABLE_X_COORD = 200, TABLE_Y_COORD = 200;
    private static final int TIMER_INTERVAL = 20;
    
    //private Image bufferImage;
    //private Dimension bufferSize;
    
    private ImageIcon tableIcon = new ImageIcon("images/table.png");
    private List<Image> tables = new ArrayList<Image>();
    private final int NUM_TABLES = 4;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
    	setPreferredSize(new Dimension(WINDOWX, WINDOWY));
    	setMaximumSize(new Dimension(WINDOWX, WINDOWY));
    	setMinimumSize(new Dimension(WINDOWX, WINDOWY));
        setVisible(true);
        
        for(int i = 0; i < NUM_TABLES; i++) {
        	tables.add(tableIcon.getImage());
        }
    	
        Timer timer = new Timer(TIMER_INTERVAL, this);
        timer.start();
    }
    /*public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        
        bufferSize = this.getSize();
        
    	Timer timer = new Timer(TIMER_INTERVAL, this );
    	timer.start();
    }*/

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
		//Drawing tables
		g2.drawImage(tables.get(0), 200, 200, 100, 60, this);
		g2.drawImage(tables.get(1), 450, 200, 100, 60, this);
		g2.drawImage(tables.get(2), 200, 400, 100, 60, this);
		g2.drawImage(tables.get(3), 450, 400, 100, 60, this);
		
		for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
		
		for(Gui gui : guis) {
			gui.draw(g2);
			/*if(gui instanceof CustomerGui) {
				CustomerGui tempGui = (CustomerGui) gui;
				if(tempGui.getEating()) {
					g2.drawImage(tempGui.getFoodImage(), tempGui.getXPos() + 10, tempGui.getYPos() + 30, 20, 20, this);
				}
				gui.draw(g2);
				
			}*/
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
