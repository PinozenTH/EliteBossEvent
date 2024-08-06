package com.pinont.elitebossevent.Config;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.units.qual.Prefix;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.pinont.elitebossevent.EliteBossEvent.LANG_FILE;

public enum Lang {

    // Plugins
    PREFIX("prefix","&8[&6EliteBossEvent&8]"),
    RELOAD("reload", "EliteBossEvent has been reloaded!"),
    ENABLE("enable", "EliteBossEvent has been Enabled!"),
    DISABLE("disable", "EliteBossEvent has been Disabled!"),
    WARN_LOW_DELAY("warn_low_delay", "Event delay is less than 1, setting to default value of 10 minutes."),
    // Commands
    NO_PERMISSION("no_permission", "You do not have permission to use this command!"),
    INVALID_COMMAND("invalid_command", "Invalid command!"),
    // Elite Boss Event
    ELITE_EVENT_STARTED("elite_event_started", "Elite Boss Event has started!"),
    ELITE_EVENT_STOPPED("elite_event_stopped", "Elite Boss Event has stopped!"),
    ELITE_EVENT_STARTING("elite_event_starting", "Mob will be summoned in <count> seconds!"),
    NO_PLAYER_TICKED("no_player_ticked", "No player ticked, mob will not be summoned!"),
    SUMMON_MOB("summon_mob", "Mob has been summoned!"),
    SUMMON_NOTIFY("summon_notify", "Mob will be summoned at a certain player <cooldown> seconds!"),
    WARNING_PLAYER("warning_player", "Please avoid a player with a glowing effect!"),
    BOSS_SPAWNED("boss_spawned", "Boss has been spawned at <location>"),
    MINI_BOSS_SPAWNED("mini_boss_spawned", "Mini-boss has been spawned at <location>"),
    // Debug Mode
    DEBUG_MODE_ENABLED("debug_mode_enabled", "Debug mode is enabled!"),
    // Mythic Mobs
    HOOKED_TO_MYTHIC_MOBS("hooked_to_mythic_mobs", "Hooked into MythicMobs!"),
    CHECKING_PROVIDED_MYTHIC_MOBS("checking_provided_mythic_mobs", "Checking provided MythicMobs..."),
    NO_MYTHIC_MOBS("no_mythic_mobs", "MythicMobs not found! Disabling plugin..."),
    NOT_VALID_MYTHIC_MOB("not_valid_mythic_mob", "&c is not a valid MythicMob!"),
    REGISTERED_MYTHIC_MOB("registered_mythic_mob", "&a✔Registered MythicMob: &e"),
    NULL_MYTHIC_MOB("null_mythic_mob", "&c is null, make sure that mobs is exist!");

    private static final EliteBossEvent main = EliteBossEvent.getInstance();
    private final String path;
    private final String def;
    private static YamlConfiguration LANG = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "lang.yml"));

    Lang(String path, String def) {
        this.path = path;
        this.def = def;
    }

    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));
    }

    public String getDefault() {
        return this.def;
    }

    public String getPath() {
        return this.path;
    }

    /**
     * Load the lang.yml file.
     * @return The lang.yml config.
     */
    public static void loadLang() {
        File lang = new File(main.getDataFolder(), "lang.yml");
        if (!lang.exists()) {
            try {
                main.getDataFolder().mkdir();
                lang.createNewFile();
                File defConfigStream = main.getLangFile();
                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    defConfig.save(lang);
                    Lang.setFile(defConfig);
                    return;
                }
            } catch(IOException e) {
                e.printStackTrace(); // So they notice
                new Reply(Reply.LoggerType.SEVERE, "&8[&6EliteBossEvent&8] Couldn't create language file.");
                new Reply(Reply.LoggerType.SEVERE, "&8[&6EliteBossEvent&8] This is a fatal error. Now disabling");
                Bukkit.getPluginManager().disablePlugin(main); // Without it loaded, we can't send them messages
            }
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for(Lang item:Lang.values()) {
            if (conf.getString(item.getPath()) == null) {
                conf.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(conf);
        EliteBossEvent.LANG = conf;
        LANG_FILE = lang;
        try {
            conf.save(main.getLangFile());
        } catch(IOException e) {
            new Reply(Reply.LoggerType.WARNING, "PluginName: Failed to save lang.yml.");
            new Reply(Reply.LoggerType.WARNING, "PluginName: Report this stack trace to <your name>.");
            e.printStackTrace();
        }
    }
}