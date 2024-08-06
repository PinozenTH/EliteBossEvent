package com.pinont.elitebossevent.Tasks;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Hooks.MythicMobsAPI;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.pinont.elitebossevent.Listeners.EntityListener.eliteboss;
import static com.pinont.elitebossevent.Utils.MobsManager.SummonManager.spawnAtPlayer;

public class SummonMobTask {

    private final EliteBossEvent main = EliteBossEvent.getInstance();
    public final Map<Player, Integer> tickedPlayers = new HashMap<>();
    public int maxTickedPlayers;
    private BukkitTask task;

    public void addTickPlayer(Player player) {
        tickedPlayers.put(player, 0);
        new Debug("Player " + player.getName() + " has been ticked!", Debug.DebugType.BOTH);
        new Debug("Ticked players" + getTickedPlayers());
    }

    public void removeTickPlayer(Player pLayer) {
        tickedPlayers.remove(pLayer);
        new Debug("Player " + pLayer.getName() + " has been removed from ticked list!", Debug.DebugType.BOTH);
    }

    public void clearTickPlayers() {
        tickedPlayers.clear();
        new Debug("All players have been removed from ticked list!", Debug.DebugType.BOTH);
    }

    public HashMap<Player, Integer> getTickedPlayers() {
        return (HashMap<Player, Integer>) tickedPlayers;
    }

    public int getMaxTickedPlayers() {
        int configMaxPlayers = main.getConfig().getInt("summon-rules.max-players");
        if (configMaxPlayers == 0) {
            maxTickedPlayers = Bukkit.getOnlinePlayers().size() / 2;
        } else {
            maxTickedPlayers = configMaxPlayers;
        }
        return maxTickedPlayers;
    }

    public void start() {
        int delay = main.getConfig().getInt("summon-rules.event-delay");
        boolean seconds = main.getConfig().getBoolean("debug.change-delay-to-sec");
        if (delay < 1) {
            main.getLogger().warning("Event delay is less than 1, setting to default value of 10 minutes.");
            delay = 10;
        }
        int period = seconds ? delay * 20 : delay * 1200;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (main.getConfig().getInt("summon-rules.min-players") > Bukkit.getOnlinePlayers().size()) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (tickedPlayers.size() >= maxTickedPlayers) {
                        break;
                    }
                    if (tickedPlayers.containsKey(player)) {
                        continue;
                    }
                    tickedPlayers.put(player, 0);
                    // notify player
                    new Reply(player, "Mob will be summoned at a certain player " + 30 + " seconds!");
                }
                summonMob(30);
            }
        }.runTaskTimerAsynchronously(main, 0, period);
    }

    public void summonMob(int countDown) {
        final int[] count = {countDown};
        // delay 10 sec and notify player every sec before summoning mob
        new Reply(Reply.SenderType.BOTH, "Mob will be summoned in 30 seconds!");
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] == 0) {
                    if (tickedPlayers.isEmpty()) {
                        new Reply(Reply.SenderType.BOTH, "No player ticked, mob will not be summoned!");
                        cancel();
                        return;
                    }
                    new Reply(Reply.SenderType.BOTH, "Mob has been summoned!");
                    spawnAtPlayer(new ArrayList<>(tickedPlayers.keySet()));
                    tickedPlayers.clear();
                    cancel();
                    return;
                }
                new Reply(Reply.SenderType.BOTH, "Mob will be summoned in " + count[0] + " seconds!");
                count[0]--;
            }
        }.runTaskTimerAsynchronously(main, 0, 20);
    }

}
