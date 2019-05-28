package fr.nivcoo.pointz.gui.shop;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.commands.SendCommand;
import fr.nivcoo.pointz.configuration.Config;
import fr.nivcoo.pointz.configuration.DataBase;
import fr.nivcoo.pointz.constructor.Configurations;
import fr.nivcoo.pointz.constructor.Items;
import fr.nivcoo.pointz.constructor.Offers;
import fr.nivcoo.pointz.gui.CreateItems;
import net.milkbowl.vault.economy.Economy;

public class GuiShop implements Listener {

	private Config message = Pointz.getMessages();
	String PrefixPoint = message.getString("prefix");
	private DataBase bdd = Pointz.getBdd();

	private Inventory invShop;
	private Inventory invConverter;
	private Inventory invConfirm;
	CreateItems createItems;

	public GuiShop(Plugin p) {
		int rowShop = ((Pointz.getItems.size() + 8) / 9) * 9;
		int rowBuy = ((Pointz.getOffers.size() + 8) / 9) * 9;
		String guiShopName = "Shop";
		String guiConverterName = "Shop";
		for (Configurations getGuiName : Pointz.getConfig) {
			if (!getGuiName.getGuiShopName().isEmpty())
				guiShopName = getGuiName.getGuiShopName();
			if (!getGuiName.getGuiConverterName().isEmpty())
				guiConverterName = getGuiName.getGuiConverterName();
		}
		invShop = Bukkit.getServer().createInventory(null, rowShop, guiShopName);
		invConverter = Bukkit.getServer().createInventory(null, rowBuy, guiConverterName);
		invConfirm = Bukkit.getServer().createInventory(null, 18, "Confirmation");
		int i = 0;
		ItemStack item;
		for (Items items : Pointz.getItems) {
			List<String> lores = new ArrayList<String>();
			lores.add("§7- Prix : §c" + items.getPrice());
			if (items.getPriceIg() != 0)
				lores.add("§7- Prix InGame : §c" + items.getPriceIg());

			item = CreateItems.item(Material.getMaterial("" + items.getIcon()), items.getName(), lores);
			invShop.setItem(i, item);
			i++;
		}
		// pconverter items listing
		i = 0;
		for (Offers items : Pointz.getOffers) {
			List<String> lores = new ArrayList<String>();
			int a = 0;
			for (String lore : items.getLores().split("\\[[^\\[]*\\]")) {
				if (a >= 6)
					break;
				lores.add(lore);
				a++;
			}
			lores.add("§7- Prix : §c" + items.getPrice());
			if (items.getPriceIg() != 0)
				lores.add("§7- Gain en jeux : §c" + items.getPriceIg());

			item = CreateItems.item(Material.getMaterial("" + items.getIcon()), items.getName(), lores);
			invConverter.setItem(i, item);
			i++;
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, p);
	}

	public void showConfirm(Player p, ItemStack itemStack, int getPriceIg) {
		invConfirm.setItem(4, itemStack);
		List<String> Lore = Arrays.asList("§c- §7Cliquez pour confirmer l'achat !");

		if (getPriceIg > 0)
			invConfirm.setItem(11, CreateItems.item(Material.STAINED_GLASS_PANE, "§aPrix en jeux| Confirmation", Lore));

		invConfirm.setItem(15, CreateItems.item(Material.STAINED_GLASS_PANE, "§aPrix | Confirmation", Lore));
		p.openInventory(invConfirm);
	}

	public void show(Player p, int type) {
		if (type == 0)
			p.openInventory(invShop);
		else
			p.openInventory(invConverter);
	}

	private HashMap<UUID, Integer> inventoryPlayer = new HashMap<>();

	@EventHandler
	public void onInventoryClickShop(InventoryClickEvent e) {
		if (!e.getInventory().getName().equalsIgnoreCase(invShop.getName())
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
		if (e.getInventory().getName().equalsIgnoreCase(invShop.getName())) {
			inventoryPlayer.put(player.getUniqueId(), e.getSlot());
			Items item = Pointz.getItems.get(inventoryPlayer.get(player.getUniqueId()));
			this.showConfirm(player.getPlayer(), e.getCurrentItem(), item.getPriceIg());

		} else {
			Items item = Pointz.getItems.get(inventoryPlayer.get(player.getUniqueId()));
			String itemConfirm = invConfirm.getItem(11).getItemMeta().getDisplayName();
			if ((itemConfirm != null)
					&& e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(itemConfirm)) {
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
					.equalsIgnoreCase(invConfirm.getItem(15).getItemMeta().getDisplayName())) {

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

	@EventHandler
	public void onInventoryClickBuy(InventoryClickEvent e) {
		if (!e.getInventory().getName().equalsIgnoreCase(invConverter.getName()))
			return;
		if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) {
			e.setCancelled(true);
			return;
		}
		if (e.getCurrentItem().getItemMeta() == null)
			return;
		e.setCancelled(true);
		Player player = (Player) e.getWhoClicked();

		if (e.getInventory().getName().equalsIgnoreCase(invConverter.getName())) {
			inventoryPlayer.put(player.getUniqueId(), e.getSlot());
			Offers offer = Pointz.getOffers.get(inventoryPlayer.get(player.getUniqueId()));
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
