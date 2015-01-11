package System;
/**
 * This class represents a singel point, with two 
 * coordinates- X and Y.
 * 
 * @author Nir Mendel & Yael Mathov
 *
 */
public class Point {
	private final double m_x;
	private final double m_y;

	/**
	 * Constructor
	 * 
	 * @param x - x coordinate
	 * @param y - y  coordinate
	 */
	public Point(double x, double y){
		this.m_x = x;
		this.m_y = y;
	}

	/**
	 * Get anther Point and calculate the distance between them.
	 * 
	 * @param point 
	 * @return Distance between this and point (absolute)
	 */
	public int distanceFrom(Point point){
		return (int)Math.abs(Math.sqrt(Math.abs(Math.pow((this.m_x-point.m_x) ,2)-Math.pow((this.m_y-point.m_y), 2))));
	}
	
	/**
	 * toString override
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Point [x=");
		builder.append(m_x);
		builder.append(", y=");
		builder.append(m_y);
		builder.append("]");
		return builder.toString();
	}
}
