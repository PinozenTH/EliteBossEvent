package com.pinont.elitebossevent.Listeners;

import com.pinont.elitebossevent.Utils.MythicMobs.Conditions;
import com.pinont.elitebossevent.Utils.MythicMobs.Drops;
import com.pinont.elitebossevent.Utils.MythicMobs.Mechanics;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class MythicMobsListener implements Listener {

    public Logger log;

    /*
     * Registers all of the custom mechanics when MythicMechanicLoadEvent is called
     */
    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        log.info("MythicMechanicLoadEvent called for mechanic " + event.getMechanicName());

        if (event.getMechanicName().equalsIgnoreCase("EXAMPLE")) {
            event.register(new Mechanics(event.getConfig()));
            log.info("-- Registered Example mechanic!");
        }
    }

    /*
     * Registers all of the custom conditions when MythicConditionLoadEvent is called
     */
    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent event) {
        log.info("MythicConditionLoadEvent called for condition " + event.getConditionName());

        if (event.getConditionName().equalsIgnoreCase("EXAMPLE")) {
            event.register(new Conditions(event.getConfig()));
            log.info("-- Registered Example condition!");
        }
    }

    /*
     * Registers all of the custom drops when MythicDropLoadEvent is called
     */
    @EventHandler
    public void onMythicDropLoad(MythicDropLoadEvent event) {
        log.info("MythicDropLoadEvent called for drop " + event.getDropName());

        if (event.getDropName().equalsIgnoreCase("EXAMPLE")) {
            event.register(new Drops(event.getConfig(), event.getArgument()));
            log.info("-- Registered Excample drop!");
        }
    }
}
