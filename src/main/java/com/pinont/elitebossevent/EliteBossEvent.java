package com.pinont.elitebossevent;

import com.pinont.elitebossevent.Commands.CommandHandler;
import com.pinont.elitebossevent.Config.Lang;
import com.pinont.elitebossevent.Hooks.MythicMobsAPI;
import com.pinont.elitebossevent.Listeners.EntityListener;
import com.pinont.elitebossevent.Listeners.MythicMobsListener;
import com.pinont.elitebossevent.Tasks.SummonMobTask;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Debug.DebugType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

import static com.pinont.elitebossevent.Config.Lang.loadLang;


public class EliteBossEvent extends JavaPlugin {

    public final Boolean debug = getConfig().getBoolean("debug.enabled");
    private Logger log;
    private static EliteBossEvent instance;
    public static YamlConfiguration LANG;
    public static File LANG_FILE;

    public EliteBossEvent() {super();}

    protected EliteBossEvent(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        instance = this;
        log = this.getLogger();
        loadLang();
        LANG = YamlConfiguration.loadConfiguration(LANG_FILE);
        LANG_FILE = new File(getDataFolder(), "lang.yml");
        saveResource("lang.yml", false);
        MythicMobsAPI.hook();
        loadConfig();
        registerListeners();
        registerCommands();
        executeTask();
        log.info(Lang.ENABLE.toString());
        new Debug("Debug mode is enabled!", DebugType.WARNING);
    }

    public void onDisable() {
        log.info(Lang.DISABLE.toString());
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new MythicMobsListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getServer().getPluginCommand("eliteboss")).setExecutor(new CommandHandler());
        Objects.requireNonNull(getServer().getPluginCommand("eliteboss")).setTabCompleter(new CommandHandler());
    }

    private void executeTask() {
        new SummonMobTask().start();
    }

    public static EliteBossEvent getInstance() {
        return instance;
    }

    /**
     * Gets the lang.yml config.
     * @return The lang.yml config.
     */
    public YamlConfiguration getLang() {
        return LANG;
    }

    /**
     * Get the lang.yml file.
     * @return The lang.yml file.
     */
    public File getLangFile() {
        return LANG_FILE;
    }
}