package fr.nivcoo.pointz.constructor;

public class ItemsConverter {

	private String name;
	private String icon;
	private int price;
	private int priceIg;
	private String lores;
	private String cmd;

	public ItemsConverter(String name, String icon, int price, int priceIg, String lores, String cmd) {
		this.name = name;
		this.icon = icon;
		this.price = price;
		this.priceIg = priceIg;
		this.lores = lores;
		this.cmd = cmd;
	}

	public String getName() {
		return name.replace("&", "§");

	}

	public String getIcon() {
		return icon;

	}

	public int getPrice() {
		return price;

	}

	public int getPriceIg() {
		return priceIg;

	}

	public String getLores() {
		return lores.replace("&", "§");

	}

	public String getCmd() {
		return cmd.replace("&", "§");

	}
}