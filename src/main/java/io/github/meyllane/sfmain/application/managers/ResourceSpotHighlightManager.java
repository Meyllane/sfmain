package io.github.meyllane.sfmain.application.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.ProfileRSInteraction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.util.*;

public class ResourceSpotHighlightManager {
    private SFMain plugin;

    public ResourceSpotHighlightManager(SFMain plugin) {
        this.plugin = plugin;
    }

    private int getNextEntityID() {
        World world = plugin.getServer().getWorlds().getFirst();

        Entity temp = world.spawnEntity(new Location(world, 0, 0, 0), EntityType.MARKER);

        int entityID = temp.getEntityId();

        temp.remove();

        return entityID;
    }

    public void spawnSpotHighlights(Player player, Profile profile, Set<ResourceSpot> spots) {
        for (ResourceSpot spot : spots) {
            int entityID = this.getNextEntityID();
            Location spotLocation = spot.getLocation();

            ProfileRSInteraction prs = profile.getProfileRSInteractions().stream()
                    .filter(profileRSInteraction -> profileRSInteraction.getResourceSpotID() == spot.getId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No matching ProfileRSInteraction when trying to spawnSpotHighlights"));

            WrapperPlayServerSpawnEntity spawnPacket = this.buildSpawnPacket(entityID, spotLocation);
            WrapperPlayServerEntityMetadata metadataPacket = this.buildMetaPacket(entityID, spot, prs);

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawnPacket);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, metadataPacket);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, this.buildDestroyPacket(entityID));
            }, 600); //20t per second, for 30 seconds = 600 ticks
        }
    }

    private WrapperPlayServerSpawnEntity buildSpawnPacket(int entityID, Location loc) {
        return new WrapperPlayServerSpawnEntity(
                entityID,
                Optional.of(UUID.randomUUID()),
                EntityTypes.BLOCK_DISPLAY,
                new Vector3d(loc.getX(), loc.getY(), loc.getBlockZ()),
                0f, 0f, 0f,
                0,
                Optional.empty()
        );
    }

    private WrapperPlayServerEntityMetadata buildMetaPacket(int entityID, ResourceSpot spot, ProfileRSInteraction prs) {
        List<EntityData<?>> entityData = new ArrayList<>();

        WrappedBlockState state;
        if (prs.getNbInteraction() < spot.getMaxInteraction()) {
            state = StateTypes.LIME_STAINED_GLASS.createBlockState();
        } else {
            state = StateTypes.RED_STAINED_GLASS.createBlockState();
        }

        entityData.add(new EntityData<>(23, EntityDataTypes.BLOCK_STATE, state.getGlobalId()));
        entityData.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x40)); //Glowing effect
        entityData.add(new EntityData<>(12, EntityDataTypes.VECTOR3F, new Vector3f(1.05f, 1.05f, 1.05f)));

        return new WrapperPlayServerEntityMetadata(entityID, entityData);
    }

    private WrapperPlayServerDestroyEntities buildDestroyPacket(int entityID) {
        return new WrapperPlayServerDestroyEntities(entityID);
    }
}
