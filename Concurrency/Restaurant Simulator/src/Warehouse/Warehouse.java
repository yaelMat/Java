package Warehouse;


public interface Warehouse{
	//@INV: none
	
	//@PRE: none
	int findTool(KitchenTool tool);
	//@POST: -1 if tool exists in the tool's collection. else - returns the tool's index in the collection
	
	//@PRE: none
	int findIngredient(Ingredient ingredient);
	//@POST: -1 if tool exists in the ingredient's collection. else - returns the ingredient's index in the collection
	
	// @PRE: tool != null
	boolean isToolAvailable(KitchenTool tool);
	// @POST: @return == true if tool exists in tools collection (aka t) and t.available(tool) == true
	
	// @PRE: ingredient != null
	boolean isIngredientAvailable(Ingredient ingredient);
	// @POST: @return == true if i exists in ingredients collection (aka i) and i.avaliable(ingredient) == true
	
	//@PRE: none
	boolean isToolAndIngredientAvailable(Warehouse warehouse);
	//POST: @return == warehouse != null and 
	// 				for each t in the tools collection: isToolAvailable(t) == true and 
	//				for each i in the ingredient collection: isIngredientAvailable(i) == true
	
	// @PRE: isToolAndIngredientAvailable(warehouse) == true
	void takeToolsAndIngredients(Warehouse warehouse);
	// @POST: for each tool t in warehouse : @PRE(isToolAvailable(t)) == true
	//        	and (tool in @PRE(this.toolCollection) where tool == t and tool2 in @POST(this.toolCollection) where tool2 == t) 
	//			-> tool2.quantity + t.quantity == tool.quantity)
	//			AND for each ingredient i in warehouse : @PRE(isIngredientAvailable(i)) == true
	//        	and (ing in @PRE(this.ingredientCollection) where ing == i and 
	//			ingredient2 in @POST(this.ingredeintCollection) where ing2 == i) 
	//			-> ing2.quantity + i.quantity == ing.quantity)
	
	// @PRE: none
	void returnTools(Warehouse warehouse);
	// @POST: for each tool t in tools : 
	//			@POST(isToolAvailable(t) == true) and 
	//			(tool in @PRE(this.toolCollection) where tool == t and tool2 in @POST(this.toolCollection) where tool2 == t) 
	//			-> tool2.quantity - t.quantity == tool.quantity)
	
	// @PRE: none
	Warehouse merge(Warehouse warehouse);
	// @POST: @return == new warehouse with : new collection lt : for each t1 in warehouse and for each t2 in this
	//										t1==t2 -> lt.add(t1+t2)
	//										if t1 not exists in this: lt.add(t1)
	//										if t2 not exists in warehouse: lt.add(t2)
	//										new collection li : for each i1 in warehouse and for each i2 in this
	//										i1 == i2 -> li.add(i1+i2)
	//										if i1 not exists in this: li.add(i1)
	//										if i2 not exists in warehouse: li.add(i2)
	
	// @PRE: none
	String toString();
	// @POST: @return == returns all tool.toString() and all ingredient.toString() in collections
	
	// @PRE: none
	boolean compare(Warehouse warehouse);
	// @POST: @return == warehouse == this for wach tool and ignredient

}
