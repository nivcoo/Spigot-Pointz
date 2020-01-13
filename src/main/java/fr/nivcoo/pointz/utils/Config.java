package fr.nivcoo.pointz.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.nivcoo.pointz.Pointz;

public class Config {
	private File fichierConfig;
	private FileConfiguration fconfig;

	/**
	 * public Config: This method allow you to interact with an yml file.
	 *
	 * @param file Yml file who you want to interact.
	 *
	 */
	public Config(File file) {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			Pointz.get().saveRessources(file.getName());
		}
		this.fichierConfig = file;
		loadConfig();
	}

	/**
	 * public void save: This method will save the yml file.
	 */
	public void save() {
		try {
			fconfig.save(fichierConfig);
		} catch (IOException ex) {
			Bukkit.getLogger().severe("An error has occured while saving file " + fichierConfig.getPath());
		}
	}

	/**
	 * public void loadConfig: This method will load the yml file.
	 */
	private void loadConfig() {
		fconfig = YamlConfiguration.loadConfiguration(fichierConfig);
	}

	/**
	 * public void set: This method will set an object into the yml file.
	 *
	 * @param path The path location where you would save the object.
	 * @param obj  The object who you would save.
	 *
	 */
	public void set(String path, Object obj) {
		fconfig.set(path, obj);
		save();
	}

	/**
	 * public String getString: This method will return the String value.
	 *
	 * @param path The path location where you would get the String.
	 *
	 */
	public String getString(String path, String... lists) {

		String name = fconfig.getString(path);
		if (name != null) {
			if (lists != null) {
				for (int i = 0; i < lists.length; i++) {
					name = name.replace("{" + i + "}", lists[i]).replace("{prefix}", lists[i]);

				}
			}
		}

		return name == null ? null : name.replace("&", "§");
	}

	/**
	 * public int getInt: This method will return the Integer value.
	 *
	 * @param path The path location where you would get the Integer.
	 *
	 */
	public int getInt(String path) {
		return fconfig.getInt(path);
	}

	/**
	 * public long getLong: This method will return the Long value.
	 *
	 * @param path The path location where you would get the Long.
	 *
	 */
	public long getLong(String path) {
		return fconfig.getLong(path);
	}

	/**
	 * public boolean getBoolean: This method will return the Boolean value.
	 *
	 * @param path The path location where you would get the Boolean.
	 *
	 */
	public boolean getBoolean(String path) {
		return fconfig.getBoolean(path);
	}

	/**
	 * public double getDouble: This method will return the Double value.
	 *
	 * @param path The path location where you would get the Double.
	 *
	 */
	public double getDouble(String path) {
		return fconfig.getDouble(path);
	}

	/**
	 * public List<String> getStringList: This method will return a list of String.
	 *
	 * @param path The path location where you would get the StringList.
	 *
	 */
	public List<String> getStringList(String path) {
		List<String> name = new ArrayList<>();
		for (String nom : fconfig.getStringList(path)) {
			name.add(nom.replace("&", "§"));
		}
		return name;
	}

	/**
	 * public List<Integer> getIntegerList: This method will return a list of
	 * Integer.
	 *
	 * @param path The path location where you would get the IntegerList.
	 *
	 */
	public List<Integer> getIntegerList(String path) {
		List<Integer> name = new ArrayList<>();
		for (Integer nom : fconfig.getIntegerList(path)) {
			name.add(nom);
		}
		return name;
	}

	/**
	 * public List<String> getKeys: This method will return a list of Keys.
	 *
	 * @param path The path location where you would get the KeysList.
	 *
	 */
	public List<String> getKeys(String path) {
		List<String> list = new ArrayList<>();
		if ("".equalsIgnoreCase(path)) {
			for (String section : fconfig.getKeys(false)) {
				list.add(section);
			}
		} else {
			for (String section : fconfig.getConfigurationSection(path).getKeys(false)) {
				list.add(section);
			}
		}
		return list;
	}

	/**
	 * public boolean exist: This method will return true if the path exist, however
	 * false.
	 *
	 * @param path The location where you would see if path exist.
	 *
	 */
	public boolean exist(String path) {
		return fconfig.contains(path);
	}
}