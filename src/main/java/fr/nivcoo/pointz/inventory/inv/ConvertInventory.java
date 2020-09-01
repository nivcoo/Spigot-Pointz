package fr.nivcoo.pointz.inventory.inv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
import fr.nivcoo.pointz.utils.DataBase;
import fr.nivcoo.pointz.utils.ServerVersion;
import net.milkbowl.vault.economy.Economy;

public class ConvertInventory implements InventoryProvider, Listener {
	public static final String TITLE = "Converter";
	public final String PAGE = "page";
	public static final String UPDATE = "update";
	private Pointz pointz;
	private DataBase db;
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
		db = pointz.getDB();
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
							try {
								Player p = (Player) confirm.getWhoClicked();
								String playerName = p.getName();
								String playerNameWebsite = getPseudoPlayer(p);

								if (playerNameWebsite.equalsIgnoreCase(playerName)) {
									RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
											.getRegistration(Economy.class);
									double playerMoney = rsp.getProvider().getBalance(p);
									if (playerMoney >= offer.getPriceIg()) {
										rsp.getProvider().withdrawPlayer(p, offer.getPriceIg());
										int playerMoneyWebsite = getMoneyPlayer(p);
										int removePlayerMoney = playerMoneyWebsite + offer.getPrice();
										setPlayerMoney(p, removePlayerMoney);
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
							} catch (SQLException e1) {
								e1.printStackTrace();
							}

						});

				inv.fillRectangle(0, 3, inv.getRows(), confirmation);
				inv.fillRectangle(6, 3, inv.getRows(), confirmation);
			}));
			i++;
		}
		inv.put(UPDATE, false);

	}

	@Override
	public void onClose(InventoryCloseEvent e, Inventory inv) {
		Player p = (Player) e.getPlayer();
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .4f, 1.7f);

	}

	private int getMoneyPlayer(Player player) throws SQLException {
		PreparedStatement ps = null;
		Connection c = null;
		ResultSet rs = null;
		try {
			c = db.getConnection();
			ps = c.prepareStatement("SELECT money FROM users WHERE pseudo = ?");

			ps.setString(1, player.getName());
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("money");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ps.close();
			c.close();
		}
		return 0;

	}

	private String getPseudoPlayer(Player player) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			c = db.getConnection();
			ps = c.prepareStatement("SELECT pseudo FROM users WHERE pseudo = ?");

			ps.setString(1, player.getName());
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("pseudo");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.closeDb(c, ps, rs);
		}
		return null;

	}

	private void setPlayerMoney(Player player, int money) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = db.getConnection();
			ps = c.prepareStatement("UPDATE users SET money = ? WHERE pseudo = ?");

			ps.setInt(1, money);
			ps.setString(2, player.getName());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.closeDb(c, ps, null);
		}

	}

	private void closeDb(Connection c, PreparedStatement ps, ResultSet rs) {
		try {
			if (c != null)
				c.close();
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			System.out.println("Error while closing database c: " + e);
		}
	}

}
