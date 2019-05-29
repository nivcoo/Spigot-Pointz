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
import fr.nivcoo.pointz.configuration.Config;
import fr.nivcoo.pointz.configuration.DataBase;
import fr.nivcoo.pointz.constructor.Configurations;
import fr.nivcoo.pointz.constructor.Items;
import fr.nivcoo.pointz.constructor.Offers;
import fr.nivcoo.pointz.gui.shop.GuiShop;

public class Pointz extends JavaPlugin implements Listener {
	private static Config config = new Config(
			new File("plugins" + File.separator + "Pointz" + File.separator + "config.yml"));
	private static Config configMessage = new Config(
			new File("plugins" + File.separator + "Pointz" + File.separator + "message.yml"));
	private static DataBase bdd;
	private File message;
	public static GuiShop guiShop;
	public static List<Items> getItems;
	public static List<Offers> getOffers;
	public static List<Configurations> getConfig;

	@Override
	public void onEnable() {
		bdd = new DataBase(config.getString("database.host"), config.getString("database.database"),
				config.getString("database.username"), config.getString("database.password"));
		bdd.connection();
		ResultSet getlistItems = null;
		ResultSet getlistOffers = null;
		ResultSet getlistConfig = null;
		Bukkit.getConsoleSender().sendMessage("§c===============§b==============");
		Bukkit.getConsoleSender().sendMessage("§7Pointz §av" + this.getDescription().getVersion());
		if (bdd.connected()) {
			Bukkit.getConsoleSender().sendMessage("§7Database: §aOkay !");
			getlistItems = bdd.getResultSet("SELECT * FROM pointz__items");
			getlistOffers = bdd.getResultSet("SELECT * FROM pointz__offers");
			getlistConfig = bdd.getResultSet("SELECT * FROM pointz__configurations");
		} else
			Bukkit.getConsoleSender().sendMessage("§7Database: §cNo !");

		if (getlistItems != null)
			Bukkit.getConsoleSender().sendMessage("§7Plugin-Pointz: §aOkay !");
		else
			Bukkit.getConsoleSender().sendMessage("§7Plugin-Pointz: §cNo !");
		Bukkit.getConsoleSender().sendMessage("");
		if (bdd.connected() && getlistItems != null)
			Bukkit.getConsoleSender().sendMessage("§aPlugin Enabled !");
		else {
			Bukkit.getConsoleSender().sendMessage("§cPlugin Disabled !");
			Bukkit.getConsoleSender().sendMessage("§c==============§b===============");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Bukkit.getConsoleSender().sendMessage("§c==============§b===============");
		message = new File(getDataFolder(), "message.yml");
		if (!message.exists()) {
			message.getParentFile().mkdirs();
			saveResource("message.yml", false);
		}

		saveDefaultConfig();

		try {
			getItems = new ArrayList<>();
			getOffers = new ArrayList<>();
			getConfig = new ArrayList<>();
			while (getlistConfig.next()) {
				getConfig.add(
						new Configurations(getlistConfig.getString("name_shop"), getlistConfig.getString("name_gui")));
			}
			while (getlistOffers.next()) {
				getOffers.add(new Offers(getlistOffers.getString("name"), getlistOffers.getInt("icon"),
						getlistOffers.getInt("price"), getlistOffers.getInt("price_ig"),
						getlistOffers.getString("lores"), getlistOffers.getString("commands")));
			}
			while (getlistItems.next()) {
				ResultSet getAllItems = bdd
						.getResultSet("SELECT * FROM shop__items WHERE id=" + getlistItems.getInt("item_id"));
				while (getAllItems.next()) {
					getItems.add(new Items(getAllItems.getString("name"), getAllItems.getInt("price"),
							getlistItems.getInt("price_ig"), getlistItems.getInt("icon"),
							getAllItems.getString("commands")));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		guiShop = new GuiShop(this);
		getCommand("pointz").setExecutor(new Commands());
		getCommand("pshop").setExecutor(new GuiCommands());
		getCommand("pconverter").setExecutor(new GuiCommands());
	}

	@Override
	public void onDisable() {
		bdd.disconnection();
	}

	public static Config getMessages() {
		return configMessage;
	}

	public static Config getConfiguration() {
		return config;
	}

	public static DataBase getBdd() {
		return bdd;
	}

}
