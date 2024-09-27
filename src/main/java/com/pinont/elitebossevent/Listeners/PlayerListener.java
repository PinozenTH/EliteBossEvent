package com.pinont.elitebossevent.Listeners;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Tasks.SummonMobTask;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    SummonMobTask summonMobTask = EliteBossEvent.getInstance().summonMobTask;
    EliteBossEvent main = EliteBossEvent.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Bukkit.getOnlinePlayers().isEmpty() || main.getConfig().getInt("elite-boss-event.min-players") > Bukkit.getOnlinePlayers().size()) {
            if (Bukkit.getOnlinePlayers().size() == 1) {
                new Reply(Reply.SenderType.CONSOLE, "Minimum players reached, Elite event is resuming!");
                main.executeTask();
            }
            summonMobTask.updateMaxTickedPlayers();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTask(main, () -> {
            if (Bukkit.getOnlinePlayers().isEmpty() || main.getConfig().getInt("elite-boss-event.min-players") > Bukkit.getOnlinePlayers().size()) {
                new Reply(Reply.SenderType.CONSOLE, "Online players are less than minimum players required, Elite event is pausing!");
                summonMobTask.stop();
            }
            summonMobTask.updateMaxTickedPlayers();
        });
    }

}
