package com.pinont.elitebossevent;

import com.pinont.elitebossevent.Commands.CommandHandler;
import com.pinont.elitebossevent.Config.Lang;
import com.pinont.elitebossevent.Hooks.MythicMobsAPI;
import com.pinont.elitebossevent.Listeners.EntityListener;
import com.pinont.elitebossevent.Listeners.PlayerListener;
import com.pinont.elitebossevent.Tasks.SummonMobTask;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Debug.DebugType;
import org.bukkit.ChatColor;
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
    public SummonMobTask summonMobTask;

    public EliteBossEvent() {super();}

    protected EliteBossEvent(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        this.getLogger().info(
                "\n" + ChatColor.GREEN +"___________.__  .__  __        __________                    ___________                    __   \n" +
                        ChatColor.GREEN +"\\_   _____/|  | |__|/  |_  ____\\______   \\ ____  ______ _____\\_   _____/__  __ ____   _____/  |_ \n" +
                        ChatColor.GREEN +" |    __)_ |  | |  \\   __\\/ __ \\|    |  _//  _ \\/  ___//  ___/|    __)_\\  \\/ // __ \\ /    \\   __\\\n" +
                        ChatColor.GREEN +" |        \\|  |_|  ||  | \\  ___/|    |   (  <_> )___ \\ \\___ \\ |        \\\\   /\\  ___/|   |  \\  |  \n" +
                        ChatColor.GREEN +"/_______  /|____/__||__|  \\___  >______  /\\____/____  >____  >_______  / \\_/  \\___  >___|  /__|  \n" +
                        ChatColor.GREEN +"        \\/                    \\/       \\/           \\/     \\/        \\/           \\/     \\/      ");
        instance = this;
        log = this.getLogger();
        summonMobTask = new SummonMobTask();
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
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getServer().getPluginCommand("eliteboss")).setExecutor(new CommandHandler());
        Objects.requireNonNull(getServer().getPluginCommand("eliteboss")).setTabCompleter(new CommandHandler());
    }

    public void executeTask() {
        new SummonMobTask().start();
        new Debug("\n======== Summon Task Execution ========\n"
                + "Using seconds as a ticks: " + this.getConfig().getBoolean("debug.change-delay-to-sec") + "\n"
                + "Minimum player Required for spawning: " + this.getConfig().getInt("summon-rules.min-players") + "\n"
                + "Summon Type: " + this.getConfig().getString("summon-rules.mob-type")
                + "\n=======================================", DebugType.INFO);
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