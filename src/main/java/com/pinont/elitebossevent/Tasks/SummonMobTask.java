package com.pinont.elitebossevent.Tasks;

import com.pinont.elitebossevent.Config.Lang;
import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.pinont.elitebossevent.Utils.MobsManager.SummonManager.spawnAtPlayer;

public class SummonMobTask {

    int ticks = 0;
    public BukkitTask scheduleTask;

    private final EliteBossEvent main = EliteBossEvent.getInstance();
    public final ArrayList<Player> tickedPlayers = new ArrayList<>();
    public int maxTickedPlayers;
    private BukkitTask task;

    public void clearTickPlayers() {
        tickedPlayers.clear();
        new Debug("All players have been removed from ticked list!", Debug.DebugType.BOTH);
    }

    public ArrayList<Player> getTickedPlayers() {
        for (int i = 0; i <= updateMaxTickedPlayers() && i <= (Bukkit.getOnlinePlayers().size() + 1); i++) {
            new Debug("Start ticking player " + (i + 1) + "/" + maxTickedPlayers, Debug.DebugType.BOTH);
            Player player = getRandomOnlinePlayer();
            if (player == null) {
                new Debug("Player is null!", Debug.DebugType.BOTH);
                continue;
            }
            new Debug("Player " + player.getName() + " has been ticked!", Debug.DebugType.BOTH);
            if (tickedPlayers.size() >= maxTickedPlayers) {
                new Debug("Max ticked players reached!", Debug.DebugType.BOTH);
                break;
            }
            if (tickedPlayers.contains(player)) {
                new Debug("Player " + player.getName() + " has already been ticked!", Debug.DebugType.BOTH);
                continue;
            }
            tickedPlayers.add(player);
            // notify player
            new Reply(player, Objects.requireNonNull(Lang.SUMMON_NOTIFY.toString().replace("<countdown>", "30")));
            new Reply(Reply.SenderType.ALLPLAYER, Objects.requireNonNull(Lang.WARNING_PLAYER.toString()));
            Bukkit.getScheduler().runTask(main, () -> {
                for (Player p : tickedPlayers) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 1, true, false, true));
                }
            });
        }
        new Debug("Ticked players: " + tickedPlayers, Debug.DebugType.BOTH);
        return tickedPlayers;
    }

    public int updateMaxTickedPlayers() {
        int configMaxPlayers = main.getConfig().getInt("summon-rules.max-players");
        if (configMaxPlayers == 0) {
            maxTickedPlayers = (Bukkit.getOnlinePlayers().size() / 2) + 1;
        } else {
            maxTickedPlayers = configMaxPlayers;
        }
        new Debug("Max ticked players: " + maxTickedPlayers, Debug.DebugType.BOTH);
        return maxTickedPlayers;
    }

    public Player getRandomOnlinePlayer() {
        Random random = new Random();
        List<Player> onlinePlayers = List.copyOf(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            return null; // or handle the case when no players are online
        }
        int randomIndex = random.nextInt(onlinePlayers.size());
        return onlinePlayers.get(randomIndex);
    }

    public void start() {
        int delay = main.getConfig().getInt("summon-rules.event-delay");
        boolean seconds = main.getConfig().getBoolean("debug.change-delay-to-sec");
        ticks = delay;
        if (delay < 1) {
            main.getLogger().warning(Lang.WARN_LOW_DELAY.toString());
            delay = 10;
        }
        int period = seconds ? delay * 20 : delay * 1200;

        scheduleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (main.getConfig().getInt("summon-rules.min-players") > Bukkit.getOnlinePlayers().size()) {
                    new Reply(Reply.SenderType.CONSOLE, "No player online, pausing event...");
                    cancel();
                    return;
                }
                summonMob(30);
            }
        }.runTaskTimerAsynchronously(main, 5 * 30 * 20, period);
        new Debug("Task has been scheduled! [" + scheduleTask.getTaskId() + "]", Debug.DebugType.INFO);
    }

    public void summonMob(int countDown) {
        final int[] count = {countDown};
        new Reply(Reply.SenderType.ALLPLAYER, Objects.requireNonNull(Lang.ELITE_EVENT_STARTING.toString().replace("<count>", String.valueOf(count[0]))));
        // delay 10 sec and notify player every sec before summoning mob
        new Reply(Reply.SenderType.BOTH, Objects.requireNonNull(Lang.SUMMON_NOTIFY.toString().replace("<countdown>", "30")));
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] == 0) {
                    ArrayList<Player> targetedPlayers = getTickedPlayers();
                    if (targetedPlayers.isEmpty()) {
                        new Reply(Reply.SenderType.BOTH, Lang.NO_PLAYER_TICKED.toString());
                        cancel();
                        return;
                    }
                    new Reply(Reply.SenderType.BOTH, Lang.SUMMON_MOB.toString());
                    spawnAtPlayer(targetedPlayers);
                    tickedPlayers.clear();
                    cancel();
                    return;
                }
                if (count[0] <= 5) new Reply(Reply.SenderType.ALLPLAYER, Objects.requireNonNull(Lang.ELITE_EVENT_STARTING.toString().replace("<count>", String.valueOf(count[0]))));
                count[0]--;
            }
        }.runTaskTimerAsynchronously(main, 20, 20);
    }

    public void stop() {
        clearTickPlayers();
        task.cancel();
        new Reply(Reply.SenderType.ALLPLAYER, Lang.ELITE_EVENT_STOPPED.toString());
    }

    public int getDelayStatus() {
        return ticks;
    }
}
