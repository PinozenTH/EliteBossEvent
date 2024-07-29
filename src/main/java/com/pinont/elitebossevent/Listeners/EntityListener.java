package com.pinont.elitebossevent.Listeners;

import com.pinont.elitebossevent.EliteBossEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.persistence.PersistentDataType;

public class EntityListener implements Listener {
    @EventHandler
    public void entityCombustEvent(EntityCombustEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(EliteBossEvent.eliteBoss, PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
        }
    }
}
