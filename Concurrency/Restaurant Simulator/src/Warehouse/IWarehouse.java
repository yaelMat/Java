package Warehouse;
public interface IWarehouse extends Comparable<IWarehouse>{
	void reduceQuantity(IWarehouse idpOther);
	void addQuantity(IWarehouse idpOther);
	int getQuantity();
	boolean equals(Object o);
	int compareTo(IWarehouse other);
}
