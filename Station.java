package fr.TPIII_INF4063;

public class Station {
	private String name;
	private int number;
	private int positionX;
	private int positionY;
	private boolean inService;

	public Station(String name, int number) {
		this.name = name;
		this.number = number;
		this.inService = true;
	}

	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPosition(int x, int y) {
		this.positionX = x;
		this.positionY = y;
	}

	public boolean isInService() {
		return inService;
	}
	
	public String toString() {
    return name;
	}


	public void setInService(boolean inService) {
		this.inService = inService;
	}
}