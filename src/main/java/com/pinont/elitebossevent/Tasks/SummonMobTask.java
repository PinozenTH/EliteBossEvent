package com.pinont.elitebossevent.Tasks;

import com.pinont.elitebossevent.EliteBossEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class SummonMobTask {

    private final EliteBossEvent main = EliteBossEvent.getInstance();

    public void start() {
        int delay = main.getConfig().getInt("summon-rules.event-delay");
        boolean seconds = main.getConfig().getBoolean("debug.change-delay-to-sec");
        int period = seconds ? delay * 20 : delay * 1200;
        if (delay < 1) {
            main.getLogger().warning("Event delay is less than 1, setting to default value of 10 minutes.");
            delay = 10;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(main, new BukkitRunnable() {
            @Override
            public void run() {

            }
        }, 0L, period); // 1200 ticks = 60 seconds

    }

}
