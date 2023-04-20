package behaviorParameterization;

public class Apple {
	private int no;
	private String color;
	private int weight;
	public Apple() {
		// TODO Auto-generated constructor stub
	}
	public Apple(int no, String color, int weight) {
		this.no = no;
		this.color = color;
		this.weight = weight;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "Apple [no=" + no + ", color=" + color + ", weight=" + weight + "]\n";
	}
	
	

}
