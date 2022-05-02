package fr.nivcoo.pointz.inventory.inv;

import fr.nivcoo.pointz.Pointz;
import fr.nivcoo.pointz.constructor.ItemsShop;
import fr.nivcoo.pointz.constructor.PlayersInformations;
import fr.nivcoo.pointz.utils.ServerVersion;
import fr.nivcoo.utilsz.config.Config;
import fr.nivcoo.utilsz.inventory.ClickableItem;
import fr.nivcoo.utilsz.inventory.Inventory;
import fr.nivcoo.utilsz.inventory.InventoryProvider;
import fr.nivcoo.utilsz.inventory.ItemBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopInventory implements InventoryProvider, Listener {
    public static final String TITLE = "Shop";
    public final String PAGE = "page";
    public static final String UPDATE = "update";
    private Pointz pointz;
    private ClickableItem empty;
    private ClickableItem glass;
    private Config messages;
    private String prefix;
    private String titleGui;
    private int itemsNumber;
    private List<ItemsShop> getItemsShop;
    private Material icon;

    public ShopInventory() {
        pointz = Pointz.get();
        messages = pointz.getMessages();
        prefix = pointz.getPrefix();
        itemsNumber = pointz.getItemsShop().size();
        getItemsShop = pointz.getItemsShop();
        titleGui = pointz.getMWConfig().getGuiShopName();
        empty = ClickableItem.of(ItemBuilder.of(Material.AIR).build());
        glass = ClickableItem.of(ItemBuilder
                .of(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.RED_STAINED_GLASS_PANE
                        : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 7)
                .build());
    }

    @Override
    public String title(Inventory inv) {
        return (titleGui != null && !titleGui.equalsIgnoreCase("")) ? titleGui
                : ChatColor.RED.toString() + ChatColor.BOLD + ChatColor.stripColor(TITLE);
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
        for (ItemsShop item : getItemsShop) {
            List<String> lores = new ArrayList<>();
            lores.add("§7- Prix : §c" + item.getPrice());
            if (item.getPriceIg() != 0)
                lores.add("§7- Prix InGame : §c" + item.getPriceIg());
            icon = Material.getMaterial(item.getIcon().toUpperCase());
            if (icon == null) {
                icon = Material.DIRT;
                lores.add("§cIcon de l'article invalide !");
            }
            ItemStack itemStack = ItemBuilder.of(icon, 1).name(ChatColor.RED + item.getName()).lore(lores).build();

            inv.set(i, ClickableItem.of(itemStack, e -> {

                inv.fill(glass);
                lores.add("§cCliquez à droite ou à gauche pour confirmer");
                inv.set(5, inv.getRows() / 2 + 1, ClickableItem
                        .of(ItemBuilder.of(icon, 1).name(ChatColor.RED + item.getName()).lore(lores).build()));
                if (inv.getRows() % 2 == 0)
                    inv.set(5, inv.getRows() / 2 + 1, ClickableItem
                            .of(ItemBuilder.of(icon, 1).name(ChatColor.RED + item.getName()).lore(lores).build()));
                List<String> confirmLore = Collections.singletonList("§c- §7Cliquez pour confirmer l'achat !");
                ClickableItem buyButton = ClickableItem.of(ItemBuilder
                        .of(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.RED_STAINED_GLASS_PANE
                                : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 14)
                        .name(ChatColor.GREEN + "§aPrix | Confirmation").lore(confirmLore).build(), confirm -> {
                    Player p = (Player) confirm.getWhoClicked();

                    List<PlayersInformations> users = pointz.getWebsiteAPI().getPlayersInfos(Collections.singletonList(p));
                    PlayersInformations user = users.get(0);
                    if (user != null) {
                        double playerMoney = user.getMoney();
                        if (playerMoney >= item.getPrice()) {
                            double removePlayerMoney = playerMoney - item.getPrice();

                            pointz.getWebsiteAPI().setMoneyPlayer(p, removePlayerMoney);

                            pointz.sendCommand(p, item.getCmd());
                            p.sendMessage(messages.getString("menu-shop-success-web", prefix,
                                    String.valueOf(item.getPrice())));
                        } else {
                            p.sendMessage(messages.getString("no-require-money", prefix));
                        }
                    } else {
                        p.sendMessage(messages.getString("no-register-own", prefix));
                    }

                });
                if (item.getPriceIg() > 0)
                    inv.fillRectangle(0, 3, inv.getRows(), ClickableItem.of(
                            ItemBuilder
                                    .of(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                            ? Material.RED_STAINED_GLASS_PANE
                                            : Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 14)
                                    .name(ChatColor.GREEN + "§aPrix en jeux | Confirmation").lore(confirmLore).build(),
                            confirm -> {
                                Player p = (Player) confirm.getWhoClicked();

                                RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
                                        .getRegistration(Economy.class);
                                if (rsp == null)
                                    return;
                                double playerMoney = rsp.getProvider().getBalance(p);
                                if (playerMoney >= item.getPriceIg()) {
                                    rsp.getProvider().withdrawPlayer(p, item.getPriceIg());
                                    p.sendMessage(messages.getString("menu-shop-success-ig", prefix,
                                            String.valueOf(item.getPriceIg())));
                                    pointz.sendCommand(p, item.getCmd());

                                } else {
                                    confirm.getWhoClicked().sendMessage(messages.getString("no-require-money", prefix));
                                }

                            }));
                else
                    inv.fillRectangle(0, 3, inv.getRows(), buyButton);
                inv.fillRectangle(6, 3, inv.getRows(), buyButton);
            }));
            i++;
        }
        inv.put(UPDATE, false);

    }

}
