package com.pinont.elitebossevent.Utils.MobsManager;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Hooks.MythicMobsAPI;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.pinont.elitebossevent.Listeners.EntityListener.eliteboss;

public class SummonManager {

    private static final Boolean isBossSpawned = false;

    private static final EliteBossEvent main = EliteBossEvent.getInstance();

    public static void spawnAtPlayer(List<Player> players) {
        HashMap<Player, Integer> playerMap = new HashMap<>();
        for (Player player : players) {
            playerMap.put(player, 0);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Integer> entry : playerMap.entrySet()) {
                    Player player = entry.getKey();
                    int count = entry.getValue();
                    if (count >= main.getConfig().getInt("summon-rules.max-mobs-spawn-count-per-player")) {
                        playerMap.remove(player);
                    } else {
                        summonNearbyPlayer(player);
                        playerMap.put(player, count + 1);
                    }
                }
            }
        }.runTaskTimer(main, 0, 20);
    }

    enum MobType {
        VANILLA,
        MYTHIC,
        MIXED
    }

    private static void summonNearbyPlayer(Player player) {
        Location location = player.getLocation();
        Location randomLocation = getSafeRandomLocation(location);
        if (randomLocation == null) {
            new Reply(Reply.SenderType.BOTH, "error with summoning entity");
        }
        MobType mobType = MobType.valueOf(Objects.requireNonNull(main.getConfig().getString("summon-rules.mob-type")).toUpperCase());
        spawn(randomLocation, mobType);
    }

    private static Location getSafeRandomLocation(Location location) {
        Location randomLocation = randomLocation(location);
        Block b = getHighestBlock((int) randomLocation.getX(), (int) randomLocation.getZ(), Objects.requireNonNull(randomLocation.getWorld()));
        if (!b.getType().isSolid()) { //Water, lava, shrubs...
            b = randomLocation.getWorld().getBlockAt((int) randomLocation.getX(), b.getY() - 1, (int) randomLocation.getZ());
        }
        //Between max and min y
        if (b.getY() >= main.getConfig().getInt("summon-rules.Y-level")) {
            return randomLocation.add(0,1,0);
        }
        return null;
    }

    public static Block getHighestBlock(int x, int z, World world) {
        Block b = world.getHighestBlockAt(x, z);
        if (b.getType().toString().endsWith("AIR")) //1.15.1 or less
            b = world.getBlockAt(x, b.getY() - 1, z);
        return b;
    }

    private static void spawn(Location randomLocation, MobType mobType) {
        if (mobType.equals(MobType.VANILLA)) {
            summonVanillaEntity(randomLocation);
        } else if (mobType.equals(MobType.MYTHIC)) {
            summonMythicMobEntity(randomLocation);
        } else if (mobType.equals(MobType.MIXED)) {
            if (new Random().nextBoolean()) {
                summonVanillaEntity(randomLocation);
            } else {
                summonMythicMobEntity(randomLocation);
            }
        }
    }

    private static void summonVanillaEntity(Location location) {
        // spawn vanilla mobs
        Random random = new Random();
        // random vanilla entity types
        List<String> vanillaEntityTypes = main.getConfig().getStringList("vanilla-entity-types");
        spawnVanillaEntity(location, getEntityType(vanillaEntityTypes.get(random.nextInt(vanillaEntityTypes.size()))));
        new Debug("Vanilla mob has been spawned at " + location, Debug.DebugType.BOTH);
    }

    private static void summonMythicMobEntity(Location location) {
        // spawn mythic mobs
        List<String> bosses = new ArrayList<>(main.getConfig().getStringList("mythicMob-bosses-name"));
        List<String> miniBoss = new ArrayList<>(main.getConfig().getStringList("mythicMob-mini-boss-name"));
        Random random = new Random();
        if (random.nextBoolean() && !isBossSpawned) {
            // summon bosses
            MythicMobsAPI.summon(location, bosses.get(random.nextInt(bosses.size())));
            new Debug("Boss has been spawned at " + location, Debug.DebugType.BOTH);
            if (isBossSpawned) { new Reply(Reply.SenderType.BOTH, ChatColor.BOLD + "" + ChatColor.AQUA + "Boss has been spawned! at " + location); }
        } else {
            // summon mini-bosses
            MythicMobsAPI.summon(location, miniBoss.get(random.nextInt(miniBoss.size())));
            new Debug("Mini-boss has been spawned at " + location, Debug.DebugType.BOTH);
        }
    }

    private static Location randomLocation(Location location) {
        Location randomLocation = location.clone();
        int min = main.getConfig().getInt("summon-rules.min-distance");
        int max = main.getConfig().getInt("summon-rules.max-distance");
        int distanceX = new Random().nextInt(max - min) + min;
        int distanceZ = new Random().nextInt(max - min) + min;
        int distanceY = new Random().nextInt(max - min) + min;
        randomLocation.add(distanceX, distanceY, distanceZ);
        return randomLocation.add(0,1,0);
    }

    private static EntityType getEntityType(String entityType) {
        try {
            return EntityType.valueOf(entityType); // get EntityType from string
        } catch (IllegalArgumentException e) {
            new Reply(Reply.SenderType.CONSOLE, "Invalid entity type: " + entityType);
            return null;
        }
    }

    private static void spawnVanillaEntity(Location location, EntityType entityType) {
        if (entityType == null) {
            new Reply(Reply.SenderType.CONSOLE, "Failed to spawn entity!");
            return;
        }
        Entity entity = Objects.requireNonNull(location.getWorld()).spawnEntity(location, entityType);
        if (isUndead(entity)) { // set undead entity as elite boss for tagging at sunburn event
            entity.getPersistentDataContainer().set(eliteboss, PersistentDataType.BOOLEAN, true);
        }
    }

    private static Boolean isUndead(Entity entity) {
        return entity.getType().name().contains("ZOMBIE") || entity.getType().name().contains("SKELETON") || entity.getType().name().contains("PHANTOM");
    }
}
