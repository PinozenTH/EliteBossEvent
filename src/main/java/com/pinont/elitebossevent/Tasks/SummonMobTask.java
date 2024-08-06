package com.pinont.elitebossevent.Tasks;

import com.pinont.elitebossevent.Config.Lang;
import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

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
            main.getLogger().warning(Lang.WARN_LOW_DELAY.toString());
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
                for (int i = 0; i >= maxTickedPlayers && i < Bukkit.getOnlinePlayers().size(); i++) {
                    Random random = new Random();
                    Player player = Bukkit.getOnlinePlayers().toArray(new Player[0])[random.nextInt(Bukkit.getOnlinePlayers().size())];
                    if (tickedPlayers.size() >= maxTickedPlayers) {
                        break;
                    }
                    if (tickedPlayers.containsKey(player)) {
                        continue;
                    }
                    tickedPlayers.put(player, 0);
                    // notify player
                    new Reply(player, Objects.requireNonNull(Lang.SUMMON_NOTIFY.toString().replace("<countdown>", "30")));
                    new Reply(Reply.SenderType.ALLPLAYER, Objects.requireNonNull(Lang.WARNING_PLAYER.toString()));
                    for (Player tickedPlayer : tickedPlayers.keySet()) {
                        tickedPlayer.addPotionEffect(PotionEffectType.GLOWING.createEffect(30 * 20, 1));
                    }
                }
                summonMob(30);
            }
        }.runTaskTimerAsynchronously(main, 0, period);
    }

    public void summonMob(int countDown) {
        final int[] count = {countDown};
        // delay 10 sec and notify player every sec before summoning mob
        new Reply(Reply.SenderType.BOTH, Objects.requireNonNull(Lang.SUMMON_NOTIFY.toString().replace("<countdown>", "30")));
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] == 0) {
                    if (tickedPlayers.isEmpty()) {
                        new Reply(Reply.SenderType.BOTH, Lang.NO_PLAYER_TICKED.toString());
                        cancel();
                        return;
                    }
                    new Reply(Reply.SenderType.BOTH, Lang.SUMMON_MOB.toString());
                    spawnAtPlayer(new ArrayList<>(tickedPlayers.keySet()));
                    tickedPlayers.clear();
                    cancel();
                    return;
                }
                new Reply(Reply.SenderType.BOTH, Objects.requireNonNull(Lang.ELITE_EVENT_STARTING.toString().replace("<count>", String.valueOf(count[0]))));
                count[0]--;
            }
        }.runTaskTimerAsynchronously(main, 0, 20);
    }

    public void stop() {
        clearTickPlayers();
        task.cancel();
        new Reply(Reply.SenderType.ALLPLAYER, Lang.ELITE_EVENT_STOPPED.toString());
    }
}
