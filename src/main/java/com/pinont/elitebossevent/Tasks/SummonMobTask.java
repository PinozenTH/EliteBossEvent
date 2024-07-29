package com.pinont.elitebossevent.Tasks;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Hooks.MythicMobsAPI;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class SummonMobTask {

    private final EliteBossEvent main = EliteBossEvent.getInstance();
    private final Map<Player, Integer> tickedPlayers = new HashMap<>();
    private final boolean boss = false;
    private final int maxBosses = main.getConfig().getInt("max-boss-spawn-count-per-world");

    public void start() {
        int delay = main.getConfig().getInt("summon-rules.event-delay");
        boolean seconds = main.getConfig().getBoolean("debug.change-delay-to-sec");
        if (delay < 1) {
            main.getLogger().warning("Event delay is less than 1, setting to default value of 10 minutes.");
            delay = 10;
        }
        int period = seconds ? delay * 20 : delay * 1200;
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(main, new BukkitRunnable() {
            @Override
            public void run() {
                if (main.getConfig().getInt("summon-rules.min-players") > Bukkit.getOnlinePlayers().size()) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (tickedPlayers.size() >= main.getConfig().getInt("summon-rules.max-players")) {
                        break;
                    }
                    if (tickedPlayers.containsKey(player)) {
                        continue;
                    }
                    tickedPlayers.put(player, 0);
                    // notify player
                    new Reply(player, "Mob will be summoned at a certain player " + 30 + " seconds!");
                }
                summonMob();
            }
        }, 0L, period); // 1200 ticks = 60 seconds
    }

    private void summonMob() {
        // delay 10 sec and notify player every sec before summoning mob
        BukkitRunnable task = new BukkitRunnable() {
            int count = 30;
            @Override
            public void run() {
                if (count == 0) {
                    // summon mob
                    ArrayList<String> mobs = new ArrayList<>(main.getConfig().getStringList("mob"));
                    ArrayList<String> bosses = new ArrayList<>(main.getConfig().getStringList("boss"));
                    Boolean useVanillaMobs = main.getConfig().getBoolean("summon-rules.use-vanilla-mobs");
                    if (mobs.isEmpty() && bosses.isEmpty()) {
                        new Reply(Reply.SenderType.BOTH, "No mobs or bosses are configured!");
                        cancel();
                        return;
                    } else if (mobs.isEmpty() && !useVanillaMobs) {
                        new Reply(Reply.SenderType.BOTH, "No mobs are configured!");
                        cancel();
                        return;
                    } else if (mobs.isEmpty() && useVanillaMobs) {
                        Random random = new Random();
                        // random chance of vanilla and mythic mobs
                        if (random.nextBoolean()) {
                            // summon vanilla mobs

                        } else {
                            // summon mythic mobs

                        }
                    } else if (bosses.isEmpty()) {
                        new Reply(Reply.SenderType.BOTH, "No bosses are configured!");
                        cancel();
                        return;
                    }
                    // clear ticked players cache
                    tickedPlayers.clear();
                    // stop task
                    cancel();
                } else {
                    if (count == 15 || count == 10 || count >= 5) {
                        // notify player
                        new Reply(Reply.SenderType.ALLPLAYER, "Mob will be summoned in " + count + " seconds!");
                    }
                }
            }
        };
    }

    enum MobType {
        VANILLA,
        MYTHIC
    }

    private void spawn(Location location, MobType mobType) {
        if (mobType.equals(MobType.VANILLA)) {
            // spawn vanilla mobs
            Random random = new Random();
            // random vanilla entity types
            List<String> vanillaEntityTypes = main.getConfig().getStringList("vanilla-entity-types");
            spawnVanillaEntity(location, getEntityType(vanillaEntityTypes.get(random.nextInt(vanillaEntityTypes.size()))));
        } else if (mobType.equals(MobType.MYTHIC)) {
            // spawn mythic mobs
            List<String> mobs = new ArrayList<>(main.getConfig().getStringList("mobs"));
            MythicMobsAPI.summon(location, mobs.get(new Random().nextInt(mobs.size())));
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
            entity.getPersistentDataContainer().set(EliteBossEvent.eliteBoss, PersistentDataType.BOOLEAN, true);
        }
    }

    private Boolean isUndead(Entity entity) {
        return entity.getType().name().contains("ZOMBIE") || entity.getType().name().contains("SKELETON") || entity.getType().name().contains("PHANTOM");
    }

}
