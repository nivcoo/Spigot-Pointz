package fr.nivcoo.pointz.constructor;

public class Configurations {

	private String name_shop;
	private String name_gui;

	public Configurations(String name_shop, String name_gui) {
		this.name_shop = name_shop;
		this.name_gui = name_gui;
	}

	public String getGuiShopName() {
		return name_shop.replace("&", "§");

	}

	public String getGuiConverterName() {
		return name_gui.replace("&", "§");

	}

}
