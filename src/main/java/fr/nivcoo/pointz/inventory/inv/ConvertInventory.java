package fr.nivcoo.pointz.inventory.inv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.commands.Commands;
import fr.nivcoo.pointz.constructor.ItemsConverter;
import fr.nivcoo.pointz.inventory.ClickableItem;
import fr.nivcoo.pointz.inventory.Inventory;
import fr.nivcoo.pointz.inventory.InventoryProvider;
import fr.nivcoo.pointz.inventory.ItemBuilder;
import fr.nivcoo.pointz.utils.Config;
import fr.nivcoo.pointz.utils.ServerVersion;
import net.milkbowl.vault.economy.Economy;

public class ConvertInventory implements InventoryProvider, Listener {
	public static final String TITLE = "Converter";
	public final String PAGE = "page";
	public static final String UPDATE = "update";
	private Pointz pointz;
	private ClickableItem empty;
	private ClickableItem glass;
	private Config messages;
	private String prefix;
	private String titleGui;

	private int itemsNumber;
	private List<ItemsConverter> getItemsConverter;
	private Material icon;

	public ConvertInventory() {
		pointz = Pointz.get();
		messages = pointz.getMessages();
		prefix = pointz.getPrefix();
		itemsNumber = pointz.getItemsConverter().size();
		getItemsConverter = pointz.getItemsConverter();
		titleGui = pointz.getMWConfig().getGuiConverterName();
		empty = ClickableItem.of(ItemBuilder.of(Material.AIR).build());
		glass = ClickableItem.of(ItemBuilder
				.of(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.RED_STAINED_GLASS_PANE
						: Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 7)
				.build());
	}

	@Override
	public String title(Inventory inv) {
		return (titleGui != null && !titleGui.equalsIgnoreCase("")) ? titleGui : ChatColor.RED.toString() + ChatColor.BOLD + ChatColor.stripColor((String) TITLE);
	}

	@Override
	public int rows(Inventory inv) {
		int row = ((itemsNumber + 8) / 9);
		if (row == 0)
			row++;
		return row;
	}

	@Override
	public void init(Inventory inv) {
		inv.fill(empty);
		inv.put(UPDATE, true);

	}

	@Override
	public void update(Inventory inv) {
		boolean update = (boolean) inv.get(UPDATE);
		if (!update)
			return;
		inv.fill(empty);
		int i = 0;
		for (ItemsConverter offer : getItemsConverter) {
			List<String> lores = new ArrayList<String>();
			int a = 0;
			for (String lore : offer.getLores().split("\\[[^\\[]*\\]")) {
				if (a >= 6)
					break;
				lores.add(lore);
				a++;
			}

			lores.add("§7- Gain boutique : §c" + offer.getPrice());
			lores.add("§7- Prix en jeux : §c" + offer.getPriceIg());
			icon = Material.getMaterial(offer.getIcon().toUpperCase());
			if (icon == null) {
				icon = Material.DIRT;
				lores.add("§cIcon de l'article invalide !");
			}

			ItemStack itemStack = ItemBuilder.of(icon, 1).name(ChatColor.RED + offer.getName()).lore(lores).build();

			inv.set(i, ClickableItem.of(itemStack, e -> {
				inv.fill(glass);
				lores.add("§cCliquez à droite ou à gauche pour confirmer");
				inv.set(5, inv.getRows() / 2 + 1, ClickableItem
						.of(ItemBuilder.of(icon, 1).name(ChatColor.RED + offer.getName()).lore(lores).build()));
				if (inv.getRows() % 2 == 0)
					inv.set(5, inv.getRows() / 2 + 1, ClickableItem
							.of(ItemBuilder.of(icon, 1).name(ChatColor.RED + offer.getName()).lore(lores).build()));
				List<String> confirmLore = Arrays.asList("§c- §7Cliquez pour confirmer l'achat !");
				ClickableItem confirmation = ClickableItem.of(
						ItemBuilder
								.of(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
										? Material.RED_STAINED_GLASS_PANE
										: Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 14)
								.name(ChatColor.GREEN + "§aConvertir | Confirmation").lore(confirmLore).build(),
						confirm -> {
							Player p = (Player) confirm.getWhoClicked();
							HashMap<String, String> user = pointz.getWebsiteAPI().getPlayerInfos(p);

							if (user.get("error") == "false") {
								RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
										.getRegistration(Economy.class);
								double playerMoney = rsp.getProvider().getBalance(p);
								if (playerMoney >= offer.getPriceIg()) {
									rsp.getProvider().withdrawPlayer(p, offer.getPriceIg());
									float playerMoneyWebsite = Float.parseFloat(user.get("money"));
									float removePlayerMoney = playerMoneyWebsite + offer.getPrice();
									pointz.getWebsiteAPI().setMoneyPlayer(p, removePlayerMoney);
									Commands.sendCommand(p, offer.getCmd());
									p.sendMessage(messages.getString("menu-converter-success-ig", prefix,
											String.valueOf(offer.getPrice())));
									p.sendMessage(messages.getString("menu-converter-success-web", prefix,
											String.valueOf(removePlayerMoney)));
									return;
								} else {
									p.sendMessage(messages.getString("no-require-money", prefix));
								}
								return;
							} else {
								p.sendMessage(messages.getString("no-register-own", prefix));
							}

						});

				inv.fillRectangle(0, 3, inv.getRows(), confirmation);
				inv.fillRectangle(6, 3, inv.getRows(), confirmation);
			}));
			i++;
		}
		inv.put(UPDATE, false);

	}

}
