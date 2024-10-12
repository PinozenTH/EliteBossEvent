package com.pinont.elitebossevent.Listeners;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Tasks.SummonMobTask;
import com.pinont.elitebossevent.Utils.Message.Reply;
import javafx.scene.layout.Priority;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.pinont.elitebossevent.Utils.MobsManager.SummonManager.isWorldAllowed;

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Prevents players from changing dimensions
    public void onDimensionChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom().getSpawnLocation();
        Location to = event.getPlayer().getLocation();
        if (from.getWorld() != to.getWorld() && !isWorldAllowed(to.getWorld())) {
            if (new SummonMobTask().getTickedPlayers().contains(player)) {
                player.teleport(from);
                player.playSound(player.getLocation(), "entity.enderman.teleport", 1, 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
                new Reply(Reply.SenderType.PLAYER, "You are not allowed to change dimensions!");
            }
        }
    }


}
