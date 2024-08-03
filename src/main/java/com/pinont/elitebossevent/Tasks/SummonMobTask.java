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

public class SummonMobTask {

    private final EliteBossEvent main = EliteBossEvent.getInstance();
    public final Map<Player, Integer> tickedPlayers = new HashMap<>();
    private int bossCount = 0;
    public int maxTickedPlayers;
    private final boolean isBossSpawned = bossCount >= main.getConfig().getInt("summon-rules.max-boss-spawn-count-per-world");
    private BukkitTask task;

    public SummonMobTask() {
        int configMaxPlayers = main.getConfig().getInt("summon-rules.max-players");
        if (configMaxPlayers == 0) {
            maxTickedPlayers = Bukkit.getOnlinePlayers().size() / 2;
        } else {
            maxTickedPlayers = configMaxPlayers;
        }
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
                new Debug("Mob will be summoned in " + count[0] + " seconds!", Debug.DebugType.INFO);
                if (count[0] < 0) {
                    new Debug("Summoning mob...", Debug.DebugType.INFO);
                    new Debug("TickedPlayer: " + tickedPlayers, Debug.DebugType.INFO);
                    for (Player player : tickedPlayers.keySet()) {
                        summonNearbyPlayer(player);
                        tickedPlayers.put(player, tickedPlayers.get(player) + 1);
                        new Debug("TickedPlayer: " + player.getName() + " Values: " + tickedPlayers.get(player), Debug.DebugType.INFO);
                    }
                    // if all ticked player has value >= 8 then stop task
                    if (tickedPlayers.values().stream().allMatch(value -> value >= 8)) {
                        tickedPlayers.clear();
                        // stop task
                        cancel();
                        new Debug("Summon Task has been stopped!", Debug.DebugType.INFO);
                    }
                } else {
                    if (count[0] == 15 || count[0] == 10 || count[0] == 5 || count[0] == 3 || count[0] == 2 || count[0] == 1) {
                        // notify player
                        new Reply(Reply.SenderType.ALLPLAYER, "Mob will be summoned in " + count[0] + " seconds!");
                    } else if (count[0] == 0) {
                        // notify player
                        new Reply(Reply.SenderType.ALLPLAYER, "Mob has been summoned!");
                    }
                } count[0]--;
            }
        }.runTaskTimerAsynchronously(main, 0, 20);
    }

    enum MobType {
        VANILLA,
        MYTHIC
    }

    private void summonNearbyPlayer(Player player) {
        // summon mob
        ArrayList<String> mobs = new ArrayList<>(main.getConfig().getStringList("vanilla-entity-types"));
        ArrayList<String> bosses = new ArrayList<>(main.getConfig().getStringList("mythicMob-bosses-name"));
        ArrayList<String> miniBoss = new ArrayList<>(main.getConfig().getStringList("mythicMob-mini-boss-name"));
        boolean allexist = !mobs.isEmpty() && !bosses.isEmpty() && !miniBoss.isEmpty();
        boolean useVanillaMobs = main.getConfig().getBoolean("summon-rules.use-vanilla-mobs");
        MobType mobType = new Random().nextBoolean() ? MobType.VANILLA : MobType.MYTHIC;
        if (mobs.isEmpty() && bosses.isEmpty()) {
            new Reply(Reply.SenderType.BOTH, "No mobs or bosses are configured!");
            if (task != null) task.cancel();
        } else if (mobs.isEmpty() && useVanillaMobs) {
            new Reply(Reply.SenderType.BOTH, "No mobs are configured!");
            if (task != null) task.cancel();
        } else if (allexist && useVanillaMobs) {
            Random random = new Random();
            // random chance of vanilla and mythic mobs
            if (random.nextBoolean()) {
                // summon vanilla mobs
                for (int i = 0; i < main.getConfig().getInt("summon-rules.max-mobs-spawn-count-per-player"); i++) {
                    spawn(randomLocation(player.getLocation(), main.getConfig().getInt("summon-rules.distance")), mobType);
                }
            } else {
                // summon mythic mobs
                for (int i = 0; i < main.getConfig().getInt("summon-rules.max-mobs-spawn-count-per-player"); i++) {
                    spawn(randomLocation(player.getLocation(), main.getConfig().getInt("summon-rules.distance")), mobType);
                }
            }
        } else if (bosses.isEmpty() || miniBoss.isEmpty()) {
            new Reply(Reply.SenderType.BOTH, "No bosses or mini-boss are configured!");
            if (task != null) task.cancel();
        }
    }

    private Location randomLocation(Location location, int distance) {
        Location randomLocation = location.clone();
        int min = main.getConfig().getInt("summon-rules.min-distance");
        int max = main.getConfig().getInt("summon-rules.max-distance");
        int distanceX = new Random().nextInt(max - min) + min;
        int distanceZ = new Random().nextInt(max - min) + min;
        int distanceY = new Random().nextInt(max - min) + min;
        randomLocation.add(distanceX, distanceY, distanceZ);
        if (randomLocation.getBlock().getType().isSolid()) {
            return randomLocation.add(0, 1, 0);
        } else randomLocation(location, distance);
        return randomLocation;
    }

    private void spawn(Location location, MobType mobType) {
        if (mobType.equals(MobType.VANILLA)) {
            // spawn vanilla mobs
            Random random = new Random();
            // random vanilla entity types
            List<String> vanillaEntityTypes = main.getConfig().getStringList("vanilla-entity-types");
            spawnVanillaEntity(location, getEntityType(vanillaEntityTypes.get(random.nextInt(vanillaEntityTypes.size()))));
            new Debug("Vanilla mob has been spawned at " + location, Debug.DebugType.BOTH);
        } else if (mobType.equals(MobType.MYTHIC)) {
            // spawn mythic mobs
            List<String> bosses = new ArrayList<>(main.getConfig().getStringList("mythicMob-bosses-name"));
            List<String> miniBoss = new ArrayList<>(main.getConfig().getStringList("mythicMob-mini-boss-name"));
            Random random = new Random();
            if (random.nextBoolean() && !isBossSpawned) {
                // summon bosses
                MythicMobsAPI.summon(location, bosses.get(random.nextInt(bosses.size())));
                new Debug("Boss has been spawned at " + location, Debug.DebugType.BOTH);
                bossCount++;
                if (isBossSpawned) { new Reply(Reply.SenderType.BOTH, ChatColor.BOLD + "" + ChatColor.AQUA + "Boss has been spawned! at " + location); }
            } else {
                // summon mini-bosses
                MythicMobsAPI.summon(location, miniBoss.get(random.nextInt(miniBoss.size())));
                new Debug("Mini-boss has been spawned at " + location, Debug.DebugType.BOTH);
            }
        }
    }

    private EntityType getEntityType(String entityType) {
        try {
            return EntityType.valueOf(entityType); // get EntityType from string
        } catch (IllegalArgumentException e) {
            new Reply(Reply.SenderType.CONSOLE, "Invalid entity type: " + entityType);
            return null;
        }
    }

    private void spawnVanillaEntity(Location location, EntityType entityType) {
        if (entityType == null) {
            new Reply(Reply.SenderType.CONSOLE, "Failed to spawn entity!");
            return;
        }
        Entity entity = Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);
        if (isUndead(entity)) { // set undead entity as elite boss for tagging at sunburn event
            entity.getPersistentDataContainer().set(eliteboss, PersistentDataType.BOOLEAN, true);
        }
    }

    private Boolean isUndead(Entity entity) {
        return entity.getType().name().contains("ZOMBIE") || entity.getType().name().contains("SKELETON") || entity.getType().name().contains("PHANTOM");
    }

}
