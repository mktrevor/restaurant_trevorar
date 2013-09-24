package restaurant.gui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Gui {

    public void updatePosition();
    public void draw(Graphics2D g);
    public boolean isPresent();
    
    public Image getImage();
    public int getXPos();
    public int getYPos();
    
    Map<Object, Dimension> tableLocations = new HashMap<Object, Dimension>();
}
