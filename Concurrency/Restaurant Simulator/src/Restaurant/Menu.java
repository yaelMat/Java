package Restaurant;
import java.util.ArrayList;
/**
 * This class represents the resturant's menu.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Menu {
	private ArrayList<Dish> arr_menu;

	/**
	 * Constructor
	 * 
	 * @param arrMenu: menu
	 */
	public Menu()
	{
		arr_menu = new ArrayList<Dish>();
	}

	/**
	 * Add an order to the menu
	 * 
	 * @param dish: dish to add to the menu
	 */
	public void addDish(Dish dish)
	{
		if(!(this.arr_menu.contains(dish))){
			this.arr_menu.add(dish);
		}
	}

	/**
	 * Find and return the dish from the menu. 
	 * if not found-return null.
	 * 
	 * @param strName: The dish need to search's name
	 * @return The dish in the menu, or null if not found.
	 */
	public Dish getDish(String strName)
	{
		try
		{
			Dish d = new Dish(strName, 0, null, null, 0, 0);
			if (arr_menu.contains(d))
			{
				return (arr_menu.get(arr_menu.indexOf(d)));
			}
		}
		catch (Exception ex)
		{

		}

		return null;
	}

	/**
	 * toString override
	 */
	@Override
	public String toString() 
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Menu [");
		if (arr_menu != null) {
			builder.append("menu:");
			for(int i=0 ; i<this.arr_menu.size();i++){
				if(arr_menu.get(i)!=null){
					builder.append("\n");
					builder.append("dish number ");
					builder.append(i+1);
					builder.append(":\n");
					builder.append(arr_menu.get(i).toString());
				}
			}
		}
		builder.append("]");
		return builder.toString();
	}


}
