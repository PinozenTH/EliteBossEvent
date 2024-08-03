package com.pinont.elitebossevent.Hooks;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Utils.Message.Reply;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MythicMobsAPI {

    public static void hook() {
        if (EliteBossEvent.getInstance().getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            EliteBossEvent.getInstance().getLogger().info("Hooked into MythicMobs!");
            new Reply(Reply.LoggerType.INFO, ChatColor.AQUA + "Checking provided MythicMobs...");
            checkProvidedEntity();
        } else {
            EliteBossEvent.getInstance().getLogger().warning("MythicMobs not found! Disabling plugin...");
            EliteBossEvent.getInstance().getServer().getPluginManager().disablePlugin(EliteBossEvent.getInstance());
        }
    }

    private static void checkProvidedEntity() {
        EliteBossEvent.getInstance().getConfig().getStringList("mythicMob-bosses-name").forEach(MythicMobsAPI::isPresent);
        EliteBossEvent.getInstance().getConfig().getStringList("mythicMob-mini-boss-name").forEach(MythicMobsAPI::isPresent);
    }

    private static void isPresent(String entityName) {
        if (MythicBukkit.inst().getMobManager().getMythicMob(entityName).isPresent()) {
            new Reply(Reply.LoggerType.INFO, ChatColor.AQUA + "✔Registered MythicMob: " + entityName);
        } else {
            new Reply(Reply.LoggerType.WARNING, ChatColor.RED + entityName + " is not a valid MythicMob!");
        }
    }

    public static void summon(Location location, String mobName) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobName).orElse(null);
        if (mob != null) {
            // spawns mob
            mob.spawn(BukkitAdapter.adapt(location), 1);
        } else {
            Bukkit.getLogger().warning(mobName + " is null, make sure that mobs is exist!");
        }
    }

    public static Boolean isMysticMobs(Entity entity) {
        return MythicBukkit.inst().getMobManager().isMythicMob(entity);
    }

    public static List<String> getActiveMobs(Entity entity) {
        List<String> mobList = new ArrayList<>();
        Optional<ActiveMob> optActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
//        Collection<ActiveMob> activeMobs = MythicBukkit.inst().getMobManager().getActiveMobs(am -> am.getMobType().equals("SkeletalKnight"));
        optActiveMob.ifPresent(activeMob -> {
            mobList.add("[" + activeMob.getUniqueId() + "]" + " Type: " + activeMob.getMobType() + " Name: " + activeMob.getName() + " At " + activeMob.getLocation().toPosition());
        });
        if (mobList.isEmpty()) {
            mobList.add("There are no active mobs!");
        }
        return mobList;
    }

}
