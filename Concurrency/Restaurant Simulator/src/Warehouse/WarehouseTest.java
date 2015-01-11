package Warehouse;
//
//
//import static org.junit.Assert.*;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//public class WarehouseTest {
//
//	WarehouseImpl ware;
//	
//	/**
//	 * method that creates a default warehouse for testing
//	 * @return
//	 */
//	WarehouseImpl createDefaultWarehouse()
//	{
//		ConcurrentHashMap<KitchenTool,KitchenTool> tools = 
//				new ConcurrentHashMap<KitchenTool, KitchenTool>();
//		ConcurrentHashMap<Ingredient, Ingredient> ingredients = new ConcurrentHashMap<>();
//
//		KitchenTool k1 = new KitchenTool("Spoon", 5);
//		KitchenTool k2= new KitchenTool("Knife", 2);
//		KitchenTool k3= new KitchenTool("Pikachu", 78);
//		KitchenTool k4= new KitchenTool("Hammer", 3);
//
//		tools.put(k1, k1);
//		tools.put(k2,k2);
//		tools.put(k3,k3);
//		tools.put(k4,k4);
//
//		Ingredient i1 = new Ingredient("Cat",100);
//		Ingredient i2= new Ingredient("Tomato", 22);
//		Ingredient i3= new Ingredient("Milk",1);
//		Ingredient i4= new Ingredient("Egg", 2);
//
//		ingredients.put(i1, i1);
//		ingredients.put(i2, i2);
//		ingredients.put(i3, i3);
//		ingredients.put(i4, i4);
//		WarehouseImpl defaultware = null;//new WarehouseImpl(tools, ingredients);
//		
//		return defaultware;
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		createDefaultWarehouse();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public void testFindTool() {
//		
//		KitchenTool ktTool =  new KitchenTool("Spoon",2);
//		KitchenTool nAnswer = ware.findTool(ktTool);
//		assertEquals(ktTool, nAnswer);
//
//		ktTool = new KitchenTool("Ash", 5);
//		nAnswer = ware.findTool(ktTool);
//		assertEquals(nAnswer, null);
//
//		ktTool = null;
//		nAnswer = ware.findTool(ktTool);
//		assertEquals(nAnswer, null);
//
//	}
//
//	@Test
//	public void testFindIngredient() {
//		
//		Ingredient inIng =  new Ingredient("Egg", 0);
//		Ingredient nAnswer = ware.findIngredient(inIng);
//		assertEquals(nAnswer, inIng);
//
//		inIng = new Ingredient("Ash", 0);
//		nAnswer = ware.findIngredient(inIng);
//		assertEquals(nAnswer, null);
//
//		inIng = null;
//		nAnswer = ware.findIngredient(inIng);
//		assertEquals(nAnswer, null);
//	}
//
//
//	@Test
//	public void testToString() {
//		String ansStr = "TOOLS: name:Spoon-quantity:5 , name:Knife-quantity:2 , name:Pikachu-quantity:78 , name:Hammer-quantity:3" +
//				",INGREDIENTS: name:Cat-quantity:100 , name:Tomato-quantity:22 , name:Milk-quantity:1 , name:Egg-quantity:2";
//		boolean ans = ansStr.equals(ware.toString());
//		assertEquals(ans, true);
//	}
//
//}
