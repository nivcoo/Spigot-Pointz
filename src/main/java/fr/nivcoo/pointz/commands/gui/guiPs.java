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
import fr.nivcoo.pointz.constructor.Offers;
import net.milkbowl.vault.economy.Economy;

public class guiPs implements Listener {
	Config message = new Config(new File("plugins" + File.separator + "Points" + File.separator + "message.yml"));
	Config config = new Config(new File("plugins" + File.separator + "Points" + File.separator + "config.yml"));
	String PrefixPoint = message.getString("prefix");
	String h = config.getString("host");
	String n = config.getString("name");
	String p = config.getString("pass");
	String db = config.getString("dbName");
	public final DataBase bdd = new DataBase(h, db, n, p);

	private Inventory inv;

	@SuppressWarnings("deprecation")
	public guiPs(Plugin p) {
		String guiName = "Achat de Points";
		int row = ((Points.getOffers.size() + 8) / 9) * 9;
		for (Configurations getGuiName : Points.getConfig) {
			if (!getGuiName.getGuiName().isEmpty())
				guiName = getGuiName.getGuiName();
		}
		inv = Bukkit.getServer().createInventory(null, row, "�r" + guiName);
		int i = 0;
		ItemStack itemList;
		for (Offers offers : Points.getOffers) {
			itemList = createItem(Material.getMaterial(offers.getIcon()), offers.getName(), offers.getLores(),
					offers.getPriceIg(), offers.getPrice());
			inv.setItem(i, itemList);
			i++;
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, p);
	}

	private ItemStack createItem(Material dc, String name, String lore, int priceIg, int price) {
		ItemStack ItemStack = new ItemStack(dc, 1);
		ItemMeta ItemMeta = ItemStack.getItemMeta();
		List<String> loresList = new ArrayList<String>();
		int i = 0;
		for (String lores : lore.split("\\[[^\\[]*\\]")) {
			if (i >= 6)
				break;
			loresList.add(lores);
			i++;
		}
		loresList.add("�7- Prix en Jeux :�c " + priceIg);
		loresList.add("�7- Argent re�u :�c " + price);
		ItemMeta.setLore(loresList);
		ItemMeta.setDisplayName(name);
		ItemStack.setItemMeta(ItemMeta);
		return ItemStack;

	}

	public void show(Player p) {
		p.openInventory(inv);
	}

	private HashMap<UUID, Integer> inventoryPlayer = new HashMap<>();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.getInventory().getName().equalsIgnoreCase(inv.getName()))
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
			Offers offer = Points.getOffers.get(inventoryPlayer.get(player.getUniqueId()));
			String playerName = player.getName();
			String playerNameWebsite = bdd.getString("SELECT pseudo FROM users WHERE pseudo = '" + playerName + "';",
					1);
			if (playerNameWebsite.equalsIgnoreCase(playerName)) {
				RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
						.getRegistration(Economy.class);
				double playerMoney = rsp.getProvider().getBalance(player);
				if (playerMoney >= offer.getPriceIg()) {
					rsp.getProvider().withdrawPlayer(player, offer.getPriceIg());
					int playerMoneyWebsite = bdd.getInt("SELECT money FROM users WHERE pseudo = '" + playerName + "';",
							1);
					int removePlayerMoney = playerMoneyWebsite + offer.getPrice();
					/*
					 * bdd.sendRequest( "UPDATE users SET money = " + removePlayerMoney +
					 * " WHERE pseudo = '" + playerName + "';");
					 */
					bdd.sendPreparedRequest("UPDATE", "users", "money", (int) removePlayerMoney, "pseudo", playerName);
					SendCommand.sendCommand(player, offer.getCmd());
					player.sendMessage(PrefixPoint
							+ message.getString("menu-gui-success-ig").replace("{1}", "" + offer.getPrice()));
					player.sendMessage(PrefixPoint
							+ message.getString("menu-gui-success-web").replace("{1}", "" + removePlayerMoney));
					return;
				} else {
					player.sendMessage(PrefixPoint + message.getString("no-require-money"));
				}
				return;
			} else {
				player.sendMessage(PrefixPoint + message.getString("no-register-own"));
			}
			return;
		}

	}
}