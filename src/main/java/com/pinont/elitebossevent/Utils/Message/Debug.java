package com.pinont.elitebossevent.Utils.Message;

import com.pinont.elitebossevent.EliteBossEvent;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class Debug {

    private static final boolean debug = EliteBossEvent.getInstance().getConfig().getBoolean("debug.enabled");
    private static final boolean bypass_perm = EliteBossEvent.getInstance().getConfig().getBoolean("debug.bypass-permission");
    private static final String prefix = "Debug: ";
    public Logger log;

    public Debug(String message) {
        if (debug) {
            log.info(message);
        }
    }

    public Debug(String message, DebugType type) {
        if (debug && DebugType.INFO.equals(type)) {
            log.info(prefix + message);
        } else if (debug && DebugType.WARNING.equals(type)) {
            log.warning(prefix + message);
        } else if (debug && DebugType.SEVERE.equals(type)) {
            log.severe(prefix + message);
        } else if (debug && DebugType.PLAYER.equals(type)) {
            for (Player player : EliteBossEvent.getInstance().getServer().getOnlinePlayers()) {
                if (player.hasPermission("elitebossevent.debug") || bypass_perm) {
                    player.sendMessage(prefix + message);
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }
        } else if (debug && DebugType.BOTH.equals(type)) {
            new Debug(message, DebugType.INFO);
            new Debug(message, DebugType.PLAYER);
        } else {
            log.info(prefix + message);
        }
    }

    public enum DebugType {
        INFO,
        WARNING,
        SEVERE,
        PLAYER,
        BOTH
    }
}
