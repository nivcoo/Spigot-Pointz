package fr.nivcoo.pointz.inventory.inv;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.nivcoo.pointz.inventory.ClickableItem;
import fr.nivcoo.pointz.inventory.Inventory;
import fr.nivcoo.pointz.inventory.InventoryProvider;
import fr.nivcoo.pointz.inventory.ItemBuilder;

public class ShopInventory implements InventoryProvider, Listener {
	public static final String VILLAGER = "edit villager";
	public static final String PAGE = "page";
	public static final String UPDATE = "update";
	private ClickableItem space;
	private ClickableItem separator;
	private ClickableItem separator_2;

	public ShopInventory() {
		space = ClickableItem.of(ItemBuilder.of(Material.STAINED_GLASS_PANE, 1, (short) 12).name(" ").build());
		separator = ClickableItem.of(ItemBuilder.of(Material.STAINED_GLASS_PANE, 1, (short) 15).name(" ").build());
		separator_2 = ClickableItem.of(ItemBuilder.of(Material.STAINED_GLASS_PANE, 1, (short) 7).name(" ").build());
	}

	@Override
	public String title(Inventory inv) {
		Villager villager = (Villager) inv.get(VILLAGER);
		return ChatColor.RED.toString() + ChatColor.BOLD + "Edit " + ChatColor.stripColor(villager.getName());
	}

	@Override
	public int rows(Inventory inv) {
		return 6;
	}

	@Override
	public void init(Inventory inv) {
		inv.fillRectangle(1, 3, 9, 1, space);
		inv.fillRectangle(3, 6, 6, 1, separator);
		inv.fillRectangle(1, 5, 9, 1, separator_2);

	}

	@Override
	public void update(Inventory inv) {

	}

	@Override
	public void onClose(InventoryCloseEvent e, Inventory inv) {
		Player p = (Player) e.getPlayer();
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .4f, 1.7f);

	}

}
