package fr.nivcoo.pointz.inventory;

import fr.nivcoo.pointz.inventory.inv.ConvertInventory;
import fr.nivcoo.pointz.inventory.inv.ShopInventory;

public class Inventories {


    private ShopInventory shopInventory;
    private ConvertInventory convertInventory;

    public Inventories() {
        shopInventory = new ShopInventory();
        convertInventory = new ConvertInventory();
    }

    public ShopInventory getShopInventory() {
        return shopInventory;

    }

    public ConvertInventory getConvertInventory() {
        // TODO Auto-generated method stub
        return convertInventory;
    }

}
