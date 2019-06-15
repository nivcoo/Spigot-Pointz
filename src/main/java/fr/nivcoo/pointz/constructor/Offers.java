package fr.nivcoo.pointz.constructor;

public class Offers {

	private String name;
	private int icon;
	private int price;
	private int price_ig;
	private String lores;
	private String cmd;

	public Offers(String name, int icon, int price, int price_ig, String lores, String cmd) {
		this.name = name;
		this.icon = icon;
		this.price = price;
		this.price_ig = price_ig;
		this.lores = lores;
		this.cmd = cmd;
	}

	public String getName() {
		return name.replace("&", "§");

	}

	public int getIcon() {
		return icon;

	}

	public int getPrice() {
		return price;

	}

	public int getPriceIg() {
		return price_ig;

	}

	public String getLores() {
		return lores.replace("&", "§");

	}

	public String getCmd() {
		return cmd.replace("&", "§");

	}
}
