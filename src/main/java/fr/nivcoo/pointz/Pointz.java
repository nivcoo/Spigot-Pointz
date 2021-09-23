package fr.nivcoo.pointz;

import fr.nivcoo.pointz.cache.CacheManager;
import fr.nivcoo.pointz.commands.commands.CheckCMD;
import fr.nivcoo.pointz.commands.commands.SendCMD;
import fr.nivcoo.pointz.commands.commands.gui.ConverterGuiCMD;
import fr.nivcoo.pointz.commands.commands.gui.ShopGuiCMD;
import fr.nivcoo.pointz.commands.commands.manage.AddManageCMD;
import fr.nivcoo.pointz.commands.commands.manage.DelManageCMD;
import fr.nivcoo.pointz.commands.commands.manage.SetManageCMD;
import fr.nivcoo.pointz.constructor.ItemsConverter;
import fr.nivcoo.pointz.constructor.ItemsShop;
import fr.nivcoo.pointz.constructor.MWConfig;
import fr.nivcoo.pointz.inventory.Inventories;
import fr.nivcoo.pointz.inventory.InventoryManager;
import fr.nivcoo.pointz.placeholder.PlaceHolderAPI;
import fr.nivcoo.pointz.utils.WebsiteAPI;
import fr.nivcoo.utilsz.commands.CommandManager;
import fr.nivcoo.utilsz.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Pointz extends JavaPlugin implements Listener {
    private static Pointz INSTANCE;
    private Config config;
    private Config configMessage;
    // public static GuiShop guiShop;
    private WebsiteAPI websiteAPI;
    private InventoryManager inventoryManager;
    private Inventories inventories;
    private String prefix;
    private MWConfig mwConfig;

    private CommandManager commandManager;

    private CacheManager cacheManager;
    private List<ItemsConverter> getItemsConverter;
    private List<ItemsShop> getItemsShop;

    HashMap<String, HashMap<String, String>> getUserWebsite;

    @Override
    public void onEnable() {
        INSTANCE = this;
        config = new Config(loadFile("config.yml"));
        configMessage = new Config(loadFile("messages.yml"));
        prefix = configMessage.getString("prefix");
        saveDefaultConfig();
        boolean goodKey = false;
        boolean pluginWebIsEnabled = false;

        try {
            websiteAPI = new WebsiteAPI(config.getString("api.public_key"), config.getString("api.website_url"));
            goodKey = true;
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger()
                    .severe("[Pointz] The public_key isn't valid ! Please copy it on the website in config section");
        }
        if (websiteAPI != null) {
            HashMap<String, String> check;
            try {
                check = websiteAPI.check();
                if (Objects.equals(check.get("error"), "true"))
                    goodKey = false;
                pluginWebIsEnabled = true;
            } catch (Exception e) {
                goodKey = false;
            }

        }

        Bukkit.getConsoleSender().sendMessage("§c===============§b==============");
        Bukkit.getConsoleSender().sendMessage("§7Pointz §av" + this.getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§7Website URL: §a" + config.getString("api.website_url") + " !");
        if (pluginWebIsEnabled)
            Bukkit.getConsoleSender().sendMessage("§7Plugin-Pointz: §aOkay !");
        else
            Bukkit.getConsoleSender().sendMessage("§7Plugin-Pointz: §cNo !");
        if (goodKey)
            Bukkit.getConsoleSender().sendMessage("§7Public Key: §aOkay !");
        else
            Bukkit.getConsoleSender().sendMessage("§7Public Key: §cNo !");
        Bukkit.getConsoleSender().sendMessage("");
        if (goodKey)
            Bukkit.getConsoleSender().sendMessage("§aPlugin Enabled !");
        else {
            Bukkit.getConsoleSender().sendMessage("§cPlugin Disabled !");
            Bukkit.getConsoleSender().sendMessage("§c==============§b===============");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getConsoleSender().sendMessage("§c==============§b===============");
        mwConfig = websiteAPI.initMWConfig();
        getItemsConverter = websiteAPI.initItemsConverter();
        getItemsShop = websiteAPI.initItemsShop();

        commandManager = new CommandManager(this, configMessage, "pointz", "pointz.commands");

        registerCommands();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && config.getBoolean("hooks.placeholder-api")) {
            new PlaceHolderAPI().register();
        }

        inventoryManager = new InventoryManager();
        inventoryManager.init();
        inventories = new Inventories();

        getUserWebsite = new HashMap<>();

        cacheManager = new CacheManager();
    }

    @Override
    public void onDisable() {
        if (inventoryManager != null)
            inventoryManager.closeAllInventories();

        cacheManager.stopScheduler();
    }

    public void registerCommands() {
        commandManager.addCommand(new CheckCMD());
        commandManager.addCommand(new SendCMD());

        commandManager.addCommand(new AddManageCMD());
        commandManager.addCommand(new DelManageCMD());
        commandManager.addCommand(new SetManageCMD());

        commandManager.addCommand(new ConverterGuiCMD());
        commandManager.addCommand(new ShopGuiCMD());
    }

    private File loadFile(String path) {
        File configFile = new File(getDataFolder(), path);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(path, false);
        }

        return configFile;
    }

    public void sendCommand(Player player, String cmds) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        for (String cmd : cmds.split("\\[\\{\\+\\}\\]")) {
            Bukkit.dispatchCommand(console, cmd.replace("{PLAYER}", player.getName()));
        }
    }

    public Config getMessages() {
        return configMessage;
    }

    public Config getConfiguration() {
        return config;
    }

    public static Pointz get() {
        return INSTANCE;
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

    public MWConfig getMWConfig() {
        return mwConfig;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public WebsiteAPI getWebsiteAPI() {
        return websiteAPI;
    }

    public void saveRessources(String name) {
        saveResource(name, false);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public List<ItemsConverter> getItemsConverter() {
        return getItemsConverter;
    }

    public List<ItemsShop> getItemsShop() {
        return getItemsShop;
    }

    public HashMap<String, HashMap<String, String>> getUserWebsite() {
        return getUserWebsite;
    }

}
