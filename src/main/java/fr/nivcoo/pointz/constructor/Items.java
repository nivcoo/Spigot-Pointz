package fr.nivcoo.pointz.constructor;

public class Items {

	private String name;
	private int price;
	private int priceIg;
	private int icon;
	private String cmd;

	public Items(String name, int price, int priceIg, int icon, String cmd) {
		this.name = name;
		this.price = price;
		this.priceIg = priceIg;
		this.icon = icon;
		this.cmd = cmd;
	}

	public String getName() {
		return name;

	}

	public int getPrice() {
		return price;

	}

	public int getPriceIg() {
		return priceIg;

	}

	public int getIcon() {
		return icon;

	}

	public String getCmd() {
		return cmd;

	}
}
