package fr.nivcoo.pointz;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.nivcoo.pointz.commands.Commands;
import fr.nivcoo.pointz.commands.gui.guiPs;
import fr.nivcoo.pointz.commands.gui.guiShop;
import fr.nivcoo.pointz.configuration.Config;
import fr.nivcoo.pointz.configuration.DataBase;
import fr.nivcoo.pointz.constructor.Configurations;
import fr.nivcoo.pointz.constructor.Items;
import fr.nivcoo.pointz.constructor.Offers;

public class Points extends JavaPlugin implements Listener {
	public Config config = new Config(new File("plugins" + File.separator + "Points" + File.separator + "config.yml"));;
	public Config messageConfig = new Config(
			new File("plugins" + File.separator + "Points" + File.separator + "message.yml"));;
	String h = config.getString("host");
	String n = config.getString("name");
	String p = config.getString("pass");
	String db = config.getString("dbName");
	public DataBase bdd = new DataBase(h, db, n, p);
	private File message;
	private File messageOld;
	public static guiPs guiPS;
	public static guiShop guiShop;
	public static List<Items> getItems;
	public static List<Offers> getOffers;
	public static List<Configurations> getConfig;

	@Override
	public void onEnable() {

		saveDefaultConfig();
		message = new File(getDataFolder(), "message.yml");
		messageOld = new File(getDataFolder(), "message.yml.BACK-4.5");

		if (!(messageConfig.getString("menu-shop-success-ig") != null)) {
			message.renameTo(messageOld);
		}

		if (!message.exists()) {
			message.getParentFile().mkdirs();
			saveResource("message.yml", false);
		}

		ResultSet getlistItems = bdd.getResultSet("SELECT * FROM pointz__items");
		ResultSet getlistOffers = bdd.getResultSet("SELECT * FROM pointz__offers");
		ResultSet getlistConfig = bdd.getResultSet("SELECT * FROM pointz__configurations");
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
		guiPS = new guiPs(this);
		guiShop = new guiShop(this);
		getCommand("points").setExecutor(new Commands());
		getCommand("pshop").setExecutor(new Commands());
		getCommand("pgui").setExecutor(new Commands());
		bdd.connection();
		System.out.println("[Points] Le plugin vient de s'allumer");
	}

	@Override
	public void onDisable() {
		System.out.println("[Points] Le plugin vient de s'eteindre");
		bdd.disconnection();
	}

}
