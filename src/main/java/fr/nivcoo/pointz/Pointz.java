package fr.nivcoo.pointz;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nivcoo.pointz.commands.Commands;
import fr.nivcoo.pointz.commands.GuiCommands;
import fr.nivcoo.pointz.constructor.MWConfig;
import fr.nivcoo.pointz.constructor.ItemsShop;
import fr.nivcoo.pointz.constructor.ItemsConverter;
import fr.nivcoo.pointz.inventory.Inventories;
import fr.nivcoo.pointz.inventory.InventoryManager;
import fr.nivcoo.pointz.placeholder.RegisterMVDWPAPI;
import fr.nivcoo.pointz.placeholder.placeholder.PlaceHolderAPI;
import fr.nivcoo.pointz.utils.Config;
import fr.nivcoo.pointz.utils.DataBase;

public class Pointz extends JavaPlugin implements Listener {
	private static Pointz INSTANCE;
	private static Config config;
	private static Config configMessage;
	private static DataBase db;
	// public static GuiShop guiShop;
	private List<ItemsShop> getItemsShop;
	private List<ItemsConverter> getItemsConverter;
	private MWConfig getMWConfig;
	private InventoryManager inventoryManager;
	private Inventories inventories;
	private String prefix;

	@Override
	public void onEnable() {
		INSTANCE = this;
		config = new Config(new File("plugins" + File.separator + "Pointz" + File.separator + "config.yml"));
		configMessage = new Config(new File("plugins" + File.separator + "Pointz" + File.separator + "messages.yml"));
		db = new DataBase(config.getString("database.host"), config.getString("database.database"),
				config.getString("database.username"), config.getString("database.password"),
				config.getString("database.port"));
		prefix = configMessage.getString("prefix");
		db.connection();
		ResultSet getlistItemsShop = null;
		ResultSet getlistItemsConverter = null;
		ResultSet getlistMWConfig = null;

		saveDefaultConfig();
		Bukkit.getConsoleSender().sendMessage("§c===============§b==============");
		Bukkit.getConsoleSender().sendMessage("§7Pointz §av" + this.getDescription().getVersion());
		if (db.connected()) {
			Bukkit.getConsoleSender().sendMessage("§7Database: §aOkay !");
			getlistItemsShop = db.getResultSet("SELECT * FROM pointz__items__shop");
			getlistItemsConverter = db.getResultSet("SELECT * FROM pointz__items__converter");
			getlistMWConfig = db.getResultSet("SELECT * FROM pointz__configurations");
		} else
			Bukkit.getConsoleSender().sendMessage("§7Database: §cNo !");

		if (getlistItemsShop != null)
			Bukkit.getConsoleSender().sendMessage("§7Plugin-Pointz: §aOkay !");
		else
			Bukkit.getConsoleSender().sendMessage("§7Plugin-Pointz: §cNo !");
		Bukkit.getConsoleSender().sendMessage("");
		if (db.connected() && getlistItemsShop != null)
			Bukkit.getConsoleSender().sendMessage("§aPlugin Enabled !");
		else {
			Bukkit.getConsoleSender().sendMessage("§cPlugin Disabled !");
			Bukkit.getConsoleSender().sendMessage("§c==============§b===============");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Bukkit.getConsoleSender().sendMessage("§c==============§b===============");

		try {

			getItemsShop = new ArrayList<>();
			getItemsConverter = new ArrayList<>();
			while (getlistMWConfig.next()) {
				getMWConfig = new MWConfig(getlistMWConfig.getString("name_shop"),
						getlistMWConfig.getString("name_gui"));
			}
			while (getlistItemsConverter.next()) {
				getItemsConverter.add(new ItemsConverter(getlistItemsConverter.getString("name"),
						getlistItemsConverter.getString("icon"), getlistItemsConverter.getInt("price"),
						getlistItemsConverter.getInt("price_ig"), getlistItemsConverter.getString("lores"),
						getlistItemsConverter.getString("commands")));
			}
			while (getlistItemsShop.next()) {
				ResultSet getAllItems = db
						.getResultSet("SELECT * FROM shop__items WHERE id=" + getlistItemsShop.getInt("item_id"));
				while (getAllItems.next()) {
					getItemsShop.add(new ItemsShop(getAllItems.getString("name"), getAllItems.getInt("price"),
							getlistItemsShop.getInt("price_ig"), getlistItemsShop.getString("icon"),
							getAllItems.getString("commands")));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// guiShop = new GuiShop(this);
		getCommand("pointz").setExecutor(new Commands());
		getCommand("pshop").setExecutor(new GuiCommands());
		getCommand("pconverter").setExecutor(new GuiCommands());
		if (Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")
				&& config.getBoolean("hooks.mvdwplaceholder-api")) {
			new RegisterMVDWPAPI("pointz_get_money", this);

		}

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && config.getBoolean("hooks.placeholder-api")) {
			new PlaceHolderAPI().register();

		}
		db.disconnection();

		inventoryManager = new InventoryManager();
		inventoryManager.init();
		inventories = new Inventories();
	}

	@Override
	public void onDisable() {
		db.disconnection();
		if (inventoryManager != null)
			inventoryManager.closeAllInventories();

	}

	public Config getMessages() {
		return configMessage;
	}

	public Config getConfiguration() {
		return config;
	}

	public DataBase getDB() {
		return db;
	}

	public static Pointz get() {
		return INSTANCE;
	}

	public MWConfig getMWConfig() {
		return getMWConfig;
	}

	public List<ItemsShop> getItemsShop() {
		return getItemsShop;
	}

	public List<ItemsConverter> getItemsConverter() {
		return getItemsConverter;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public Inventories getInventories() {
		return inventories;
	}

	public String getPrefix() {
		return prefix;
	}

	public void saveRessources(String name) {
		saveResource(name, false);

	}

}
