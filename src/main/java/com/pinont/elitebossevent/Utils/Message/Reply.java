package com.pinont.elitebossevent.Utils.Message;

import com.pinont.elitebossevent.EliteBossEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public class Reply {

    private static final boolean bypass_perm = EliteBossEvent.getInstance().getConfig().getBoolean("debug.bypass-permission");
    public Logger log;

    public Reply(SenderType type, String message) {
        if (SenderType.CONSOLE.equals(type)) {
            log.info(message);
            new Debug("Message sent to console: " + message, Debug.DebugType.PLAYER);
        } else if (SenderType.PLAYER.equals(type)) {
            if (!getServer().getOnlinePlayers().isEmpty()) {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.hasPermission("elitebossevent.notify") || bypass_perm) {
                        player.sendMessage(message);
                        new Debug("Message sent to player: " + player.getName() + " with message: " + message, Debug.DebugType.BOTH);
                        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        new Debug("Sound played to player: " + player.getName(), Debug.DebugType.BOTH);
                    }
                }
            }
        } else if (SenderType.ALLPLAYER.equals(type)) {
            getServer().broadcastMessage(message);
            if (!getServer().getOnlinePlayers().isEmpty()) {
                for (Player player : getServer().getOnlinePlayers()) {
                    player.sendMessage(message);
                    new Debug("Message sent to player: " + player.getName() + " with message: " + message, Debug.DebugType.BOTH);
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    new Debug("Sound played to player: " + player.getName(), Debug.DebugType.BOTH);
                }
            }
        } else if (SenderType.BOTH.equals(type)) {
            log.info(message);
            new Debug("Message sent to console: " + message, Debug.DebugType.PLAYER);
            if (!getServer().getOnlinePlayers().isEmpty()) {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.hasPermission("elitebossevent.notify") || bypass_perm) {
                        player.sendMessage(message);
                        new Debug("Message sent to player: " + player.getName() + " with message: " + message, Debug.DebugType.BOTH);
                        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        new Debug("Sound played to player: " + player.getName(), Debug.DebugType.BOTH);
                    }
                }
            }
        }
    }

    public Reply(String permission, String message) {
        if (!getServer().getOnlinePlayers().isEmpty()) {
            for (Player player : getServer().getOnlinePlayers()) {
                if (player.hasPermission("elitebossevent.notify") || bypass_perm) {
                    player.sendMessage(message);
                    new Debug("Message sent to player: " + player.getName() + " with message: " + message, Debug.DebugType.BOTH);
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    new Debug("Sound played to player: " + player.getName(), Debug.DebugType.BOTH);
                }
            }
        }
    }

    public Reply(Player player, String message) {
        if (player != null) {
            player.sendMessage(message);
            new Debug("Message sent to player: " + player.getName() + " with message: " + message, Debug.DebugType.BOTH);
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            new Debug("Sound played to player: " + player.getName(), Debug.DebugType.BOTH);
        }
    }

    public Reply(CommandSender sender, String message) {
        if (sender instanceof org.bukkit.entity.Player)
            new Reply((Player) sender, "You don't have permission to use this command!");
        else
            new Reply(Reply.SenderType.CONSOLE, "You don't have permission to use this command!");
    }

    public enum SenderType {
        CONSOLE,
        PLAYER,
        BOTH,
        ALLPLAYER
    }

}
