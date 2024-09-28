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
        summonMobTask.updateMaxTickedPlayers();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTask(main, () -> {
            summonMobTask.updateMaxTickedPlayers();
        });
    }

}
