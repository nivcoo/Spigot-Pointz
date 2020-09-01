package fr.nivcoo.pointz.constructor;

public class ItemsShop {

	private String name;
	private int price;
	private int priceIg;
	private String icon;
	private String cmd;

	public ItemsShop(String name, int price, int priceIg, String icon, String cmd) {
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

	public String getIcon() {
		return icon;

	}

	public String getCmd() {
		return cmd;

	}
}
