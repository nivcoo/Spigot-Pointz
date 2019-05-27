package fr.nivcoo.pointz.commands.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import fr.nivcoo.pointz.Points;
import fr.nivcoo.pointz.commands.SendCommand;
import fr.nivcoo.pointz.configuration.Config;
import fr.nivcoo.pointz.configuration.DataBase;
import fr.nivcoo.pointz.constructor.Configurations;
import fr.nivcoo.pointz.constructor.Items;
import net.milkbowl.vault.economy.Economy;

public class guiShop implements Listener {
	Config message = new Config(new File("plugins" + File.separator + "Points" + File.separator + "message.yml"));
	Config config = new Config(new File("plugins" + File.separator + "Points" + File.separator + "config.yml"));
	String PrefixPoint = message.getString("prefix");
	String h = config.getString("host");
	String n = config.getString("name");
	String p = config.getString("pass");
	String db = config.getString("dbName");
	public final DataBase bdd = new DataBase(h, db, n, p);

	private Inventory inv;
	private Inventory invConfirm;
	private ItemStack itemConfirm2;
	private ItemStack itemConfirm3;

	@SuppressWarnings("deprecation")
	public guiShop(Plugin p) {
		int row = ((Points.getItems.size() + 8) / 9) * 9;
		String guiName = "Shop";
		for (Configurations getGuiName : Points.getConfig) {
			if (!getGuiName.getShopName().isEmpty())
				guiName = getGuiName.getShopName();
		}
		inv = Bukkit.getServer().createInventory(null, row, "�r�r" + guiName);
		invConfirm = Bukkit.getServer().createInventory(null, 18, "Confirmation");
		int i = 0;
		ItemStack itemList;
		for (Items items : Points.getItems) {

			itemList = createItem(Material.getMaterial(items.getId()), items.getName(), items.getPriceIg(),
					items.getPrice());

			inv.setItem(i, itemList);
			i++;
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, p);
	}

	private ItemStack createItem(Material dc, String name, int priceIg, int price) {
		List<String> loresList = new ArrayList<String>();
		ItemStack itemStack = new ItemStack(dc, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("�c" + name);
		if (priceIg != 0) {
			loresList.add("�7- Prix en Jeux :�c " + priceIg);
		}
		loresList.add("�7- Prix :�c " + price);
		itemMeta.setLore(loresList);
		itemStack.setItemMeta(itemMeta);
		return itemStack;

	}

	private ItemStack createItemConfirm(Material material, ItemMeta itemMeta, boolean type, String name, int id) {
		ItemStack itemStack = new ItemStack(material, 1, (short) id);
		if (type) {
			List<String> loresList = new ArrayList<String>();
			itemMeta.setDisplayName(name);
			loresList.add("�c- �7Cliquez pour confirmer l'achat !");
			itemMeta.setLore(loresList);
			itemStack.setItemMeta(itemMeta);
		} else {
			itemStack.setItemMeta(itemMeta);
		}

		return itemStack;

	}

	public void showConfirm(Player p, ItemMeta itemMeta, Material material, int getPriceIg) {
		ItemStack ItemConfirm = createItemConfirm(material, itemMeta, false, "", 1);
		invConfirm.setItem(4, ItemConfirm);

		if (getPriceIg > 0) {
			itemConfirm2 = createItemConfirm(Material.STAINED_GLASS_PANE, itemMeta, true,
					"�aPrix en jeux | Confirmation", 5);
		} else {
			itemConfirm2 = createItemConfirm(Material.STAINED_GLASS_PANE, itemMeta, true, "�cPrix en jeux | D�sactiv�",
					14);
		}
		invConfirm.setItem(11, itemConfirm2);
		itemConfirm3 = createItemConfirm(Material.STAINED_GLASS_PANE, itemMeta, true, "�aArgent r�el | Confirmation",
				4);
		invConfirm.setItem(15, itemConfirm3);
		p.openInventory(invConfirm);
	}

	public void show(Player p) {
		p.openInventory(inv);
	}

	private HashMap<UUID, Integer> inventoryPlayer = new HashMap<>();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.getInventory().getName().equalsIgnoreCase(inv.getName())
				&& !e.getInventory().getName().equalsIgnoreCase(invConfirm.getName()))
			return;
		if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) {
			e.setCancelled(true);
			return;
		}

		if (e.getCurrentItem().getItemMeta() == null)
			return;
		e.setCancelled(true);
		Player player = (Player) e.getWhoClicked();
		if (e.getInventory().getName().equalsIgnoreCase(inv.getName())) {
			inventoryPlayer.put(player.getUniqueId(), e.getSlot());
			Items item = Points.getItems.get(inventoryPlayer.get(player.getUniqueId()));
			Points.guiShop.showConfirm(player.getPlayer(), e.getCurrentItem().getItemMeta(),
					e.getCurrentItem().getType(), item.getPriceIg());

		}

		else {
			Items item = Points.getItems.get(inventoryPlayer.get(player.getUniqueId()));
			if ((itemConfirm2 != null)
					&& e.getCurrentItem().getItemMeta().getDisplayName()
							.equalsIgnoreCase(itemConfirm2.getItemMeta().getDisplayName())
					&& !itemConfirm2.getItemMeta().getDisplayName().equalsIgnoreCase("�cPrix en jeux | D�sactiv�")) {
				RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
						.getRegistration(Economy.class);
				double playerMoney = rsp.getProvider().getBalance(player);
				if (playerMoney >= item.getPriceIg()) {
					rsp.getProvider().withdrawPlayer(player, item.getPriceIg());
					player.sendMessage(PrefixPoint
							+ message.getString("menu-shop-success-ig").replace("{1}", "" + item.getPriceIg()));
					SendCommand.sendCommand(player, item.getCmd());
					return;

				} else {
					player.sendMessage(PrefixPoint + message.getString("no-require-money"));
					return;
				}

			} else if (e.getCurrentItem().getItemMeta().getDisplayName()
					.equalsIgnoreCase(itemConfirm3.getItemMeta().getDisplayName())) {

				String playerName = player.getName();
				String playerNameWebsite = bdd
						.getString("SELECT pseudo FROM users WHERE pseudo = '" + playerName + "';", 1);
				if (playerName.equalsIgnoreCase(playerNameWebsite)) {
					int playerMoney = bdd.getInt("SELECT money FROM users WHERE pseudo = '" + playerName + "';", 1);
					if (playerMoney >= item.getPrice()) {
						double removePlayerMoney = playerMoney - item.getPrice();
						/*
						 * bdd.sendRequest("UPDATE users SET money = " + removePlayerMoney +
						 * " WHERE pseudo = '" + playerName + "';");
						 */
						bdd.sendPreparedRequest("UPDATE", "users", "money", (int) removePlayerMoney, "pseudo",
								playerName);
						player.sendMessage(PrefixPoint
								+ message.getString("menu-shop-success-web").replace("{1}", "" + item.getPrice()));
						SendCommand.sendCommand(player, item.getCmd());
					} else {
						player.sendMessage(PrefixPoint + message.getString("no-require-money"));
						return;
					}
				} else {
					player.sendMessage(PrefixPoint + message.getString("no-register-own"));
				}
				return;

			}
		}

	}
}