package fr.nivcoo.pointz.gui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreateItems {

	public static ItemStack item(Material dc, String name, List<String> lores) {
		ItemStack itemStack = new ItemStack(dc, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§c" + name);
		itemMeta.setLore(lores);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}


}
