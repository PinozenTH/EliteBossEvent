package com.pinont.elitebossevent.Utils.MythicMobs;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Drops implements IItemDrop {

    private final Material material;

    public Drops(MythicLineConfig config, String argument) {
        String str = config.getString(new String[]{"type", "t"}, "STONE", argument);

        this.material = Material.valueOf(str.toUpperCase());
    }

    @Override
    public AbstractItemStack getDrop(DropMetadata data, double amount) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("SPECIAL EXAMPLE ITEM");
        item.setItemMeta(meta);

        return new BukkitItemStack(item);
    }

}