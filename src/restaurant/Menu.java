package restaurant;

import java.util.ArrayList;
import java.util.List;

public class Menu {
	private List<Choice> choices = new ArrayList<Choice>();

	public Menu() {
		choices.add(new Choice("steak", 15.99));
		choices.add(new Choice("fish", 13.99));
		choices.add(new Choice("chicken", 10.99));
	}

	Choice getChoice(int index) {
		return choices.get(index);
	}

	public int getMenuSize() {
		return choices.size();
	}

	public void removeChoice(String choice) {
		for(Choice c : choices) {
			if(c.type == choice) {
				choices.remove(c);
			}
		}
	}


	public class Choice {
		String type;
		double price;

		public Choice(String type, double price) {
			this.type = type;
			this.price = price;
		}

		public String getType() {
			return type;
		}

		public double getPrice() {
			return price;
		}
	}
}
