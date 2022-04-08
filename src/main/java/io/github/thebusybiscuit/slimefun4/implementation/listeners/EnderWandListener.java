package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.EnderWand;

/**
 * created using a {@link EnderWand}.
 * 
 * @author Giulio Martini
 * 
 * @see EnderWand
 *
 */
public class EnderWandListener implements Listener {

    private final EnderWand enderWand;

    public ZeusLightningListener(@Nonnull Slimefun plugin, @Nonnull EnderWand enderWand) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.enderWand = enderWand;
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent e) {
        if (enderWand == null || enderWand.isDisabled()) {
            return;
        }

        if (e.getEntity().getType() == EntityType.FALLING_BLOCK && e.getEntity().hasMetadata("ender_wand")) {
            e.setCancelled(true);
            e.getEntity().removeMetadata("ender_wand", Slimefun.instance());
            e.getEntity().remove();
        }
    }
}