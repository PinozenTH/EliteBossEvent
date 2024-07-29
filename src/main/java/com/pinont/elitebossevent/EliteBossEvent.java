package com.pinont.elitebossevent;

import com.pinont.elitebossevent.Commands.CommandHandler;
import com.pinont.elitebossevent.Hooks.MythicMobsAPI;
import com.pinont.elitebossevent.Listeners.EntityListener;
import com.pinont.elitebossevent.Listeners.MythicMobsListener;
import com.pinont.elitebossevent.Tasks.SummonMobTask;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Debug.DebugType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;


public final class EliteBossEvent extends JavaPlugin {

    public final Boolean debug = getConfig().getBoolean("debug.enabled");
    private Logger log;
    static EliteBossEvent instance;

    @Override
    public void onEnable() {
        instance = this;
        log = this.getLogger();
        MythicMobsAPI.hook();
        loadConfig();
        registerListeners();
        registerCommands();
        executeTask();
        log.info("EliteBossEvent has been Enabled!");
        new Debug("Debug mode is enabled!", DebugType.WARNING);
    }

    public void onDisable() {
        log.info("EliteBossEvent has been Disabled!");
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
        getServer().getPluginCommand("eliteboss").setExecutor(new CommandHandler());
    }

    private void executeTask() {
        new SummonMobTask().start();
    }

    public static EliteBossEvent getInstance() {
        return instance;
    }
}
