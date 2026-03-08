package io.github.meyllane.sfmain.events;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractRSEventListener implements Listener {
    @EventHandler
    public void onPlayerInteractRSEvent(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) return;

        Block block = event.getClickedBlock();

        if (block == null || block.getBlockData().getMaterial().isAir()) return;

        Location blockLoc = block.getLocation();

        ResourceSpot spot = SFMain.resourceSpotsRegistry.getByLocation(blockLoc);

        if (spot == null) return;


    }
}
