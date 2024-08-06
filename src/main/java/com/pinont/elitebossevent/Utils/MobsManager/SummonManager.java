package com.pinont.elitebossevent.Utils.MobsManager;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Hooks.MythicMobsAPI;
import com.pinont.elitebossevent.Utils.Box.Cuboid;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Location;
import org.bukkit.Material;
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

    private static final int distance = main.getConfig().getInt("summon-rules.distance");

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
                        spawn(player.getLocation(), MobType.valueOf(Objects.requireNonNull(main.getConfig().getString("summon-rules.mob-type")).toUpperCase()));
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

    public static void spawn(Location location, MobType mobType) {
        Cuboid cuboid = new Cuboid(location.clone().add(-distance, -distance, -distance), location.clone().add(distance, distance, distance));
        List<Location> filteredLocations = new ArrayList<>();
        for (int x = cuboid.getMinX(); x <= cuboid.getMaxX(); x++) {
            for (int y = cuboid.getMinY(); y <= cuboid.getMaxY(); y++) {
                for (int z = cuboid.getMinZ(); z <= cuboid.getMaxZ(); z++) {
                    filteredLocations.addAll(cuboid.getFilteredBlockLocations(Material.AIR));
                }
            }
        }
        Location spawnLocation = filteredLocations.get(new Random().nextInt(filteredLocations.size()));
        new Debug("Spawn location: " + spawnLocation, Debug.DebugType.BOTH);
        if (mobType.equals(MobType.VANILLA)) {
            summonVanillaEntity(spawnLocation);
        } else if (mobType.equals(MobType.MYTHIC)) {
            summonMythicMobEntity(spawnLocation);
        } else if (mobType.equals(MobType.MIXED)) {
            if (new Random().nextBoolean()) {
                summonVanillaEntity(spawnLocation);
            } else {
                summonMythicMobEntity(spawnLocation);
            }
        }
    }

    public static void summonVanillaEntity(Location location) {
        // spawn vanilla mobs
        Random random = new Random();
        // random vanilla entity types
        List<String> vanillaEntityTypes = main.getConfig().getStringList("vanilla-entity-types");
        spawnVanillaEntity(location, getEntityType(vanillaEntityTypes.get(random.nextInt(vanillaEntityTypes.size()))));
        new Debug("Vanilla mob has been spawned at " + location, Debug.DebugType.BOTH);
    }

    public static void summonMythicMobEntity(Location location) {
        // spawn mythic mobs
        List<String> bosses = new ArrayList<>(main.getConfig().getStringList("mythicMob-bosses-name"));
        List<String> miniBoss = new ArrayList<>(main.getConfig().getStringList("mythicMob-mini-boss-name"));
        Random random = new Random();
        if (random.nextBoolean() && !isBossSpawned) {
            // summon bosses
            MythicMobsAPI.summon(location, bosses.get(random.nextInt(bosses.size())));
            new Debug("Boss has been spawned at " + location, Debug.DebugType.BOTH);
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

    public static EntityType getEntityType(String entityType) {
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

    public static Boolean isUndead(Entity entity) {
        return entity.getType().name().contains("ZOMBIE") || entity.getType().name().contains("SKELETON") || entity.getType().name().contains("PHANTOM");
    }
}
