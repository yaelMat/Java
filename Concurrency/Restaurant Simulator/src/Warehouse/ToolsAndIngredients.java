package Warehouse;

public interface ToolsAndIngredients {

	void reduceQuantity(ToolsAndIngredients idpOther);
	void  addQuantity(ToolsAndIngredients idpOther);
	boolean hasQuantity(int idpOther);
}


