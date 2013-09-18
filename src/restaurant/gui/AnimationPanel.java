package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {

    private static final int WINDOWX = 450;
    private static final int WINDOWY = 350;
    private static final int TABLE_LENGTH = 50, TABLE_WIDTH = 50;
    private static final int TABLE_X_COORD = 200, TABLE_Y_COORD = 250;
    private static final int TIMER_INTERVAL = 20;
    
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
    	setPreferredSize(new Dimension(WINDOWX, WINDOWY));
    	setMaximumSize(new Dimension(WINDOWX, WINDOWY));
    	setMinimumSize(new Dimension(WINDOWX, WINDOWY));
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(TIMER_INTERVAL, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLE_X_COORD, TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);//200 and 250 need to be table params
        
        g2.fillRect(2 * TABLE_X_COORD, TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);
        g2.fillRect(TABLE_X_COORD, 2 * TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);
        g2.fillRect(2 * TABLE_X_COORD, 2 * TABLE_Y_COORD, TABLE_LENGTH, TABLE_WIDTH);
        

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
}
