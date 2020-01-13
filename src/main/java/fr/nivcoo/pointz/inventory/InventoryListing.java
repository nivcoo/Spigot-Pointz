package fr.nivcoo.pointz.inventory;

import fr.nivcoo.pointz.inventory.inv.ShopInventory;

public class InventoryListing {


	private ShopInventory shopInventory;

	public InventoryListing() {
		shopInventory = new ShopInventory();
	}

	public ShopInventory getShopInventory() {
		return shopInventory;
		
	}

}
