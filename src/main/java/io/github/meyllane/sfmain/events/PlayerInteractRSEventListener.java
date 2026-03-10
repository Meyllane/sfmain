package io.github.meyllane.sfmain.events;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.elements.*;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.ProfileRSInteraction;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

public class PlayerInteractRSEventListener implements Listener {
    private static final long RESET_TIME = 168; //Time, in hours, before reseting the number of interaction; 7 * 24h

    @EventHandler
    public void onPlayerInteractRSEvent(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick()) return;

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        ResourceSpot spot = getResourceSpot(event.getClickedBlock());
        if (spot == null) return;

        try {
            if (!spot.isSetup()) throw new SFException(ErrorMessage.get("resource_spot.not_setup"));

            Player player = event.getPlayer();
            Profile profile = getActiveProfile(player);
            ProfileRSInteraction prs = getOrCreatePRS(profile, spot);

            checkInteractionLimit(prs, spot);

            CustomItem customItem = getCustomItemFromHands(player);
            int score = spot.getInteractionScore(customItem, profile);

            Quality quality = scoreToQuality(score);

            giveItemToPlayer(player, spot.getItemBase().toItemStack(quality, 1));

            if (customItem instanceof CustomTool customTool) customTool.damage(1, player);

            updatePRS(prs, profile);

        } catch (Exception e) {
            handleErrors(e, event.getPlayer());
        }
    }

    private ResourceSpot getResourceSpot(Block block) {
        if (block == null || block.getBlockData().getMaterial().isAir()) return null;

        return SFMain.resourceSpotsRegistry.getByLocation(block.getLocation());
    }

    private Profile getActiveProfile(Player player) throws SFException {
        Profile profile = SFMain.userRegistry.get(player.getUniqueId().toString()).getActiveProfile();
        if (profile == null) throw new SFException(ErrorMessage.get("resource_spot.no_profile"));

        return profile;
    }

    private ProfileRSInteraction getOrCreatePRS(Profile profile, ResourceSpot spot) {
        Optional<ProfileRSInteraction> prs = profile.getProfileRSInteractions().stream()
                .filter(p -> p.getResourceSpotID() == spot.getId())
                .findFirst();

        if (prs.isPresent()) return prs.get();

        ProfileRSInteraction newPRS = new ProfileRSInteraction(spot.getId(), 0, LocalDateTime.now());

        profile.addProfileRSInteraction(newPRS);

        return newPRS;
    }

    private void checkInteractionLimit(ProfileRSInteraction prs, ResourceSpot spot) throws SFException {
        Duration duration = Duration.between(prs.getLastInteractionDate(), LocalDateTime.now());

        if (prs.getNbInteraction() == spot.getMaxInteraction() && duration.toHours() < RESET_TIME) {
            Duration remaining = Duration.ofHours(RESET_TIME).minus(duration);
            String formattedRemaining = String.format("%02d:%02d:%02d",
                    remaining.toHours(),
                    remaining.toMinutesPart(),
                    remaining.toSecondsPart()
            );
            throw new SFException(String.format(ErrorMessage.get("resource_spot.max_interaction"), formattedRemaining));
        } else if (duration.toHours() >= RESET_TIME) {
            prs.setNbInteraction(0);
        }
    }

    private void updatePRS(ProfileRSInteraction prs, Profile profile) {
        prs.increaseInteractionCount();
        prs.setLastInteractionDate(LocalDateTime.now());
        Bukkit.getScheduler().runTaskAsynchronously(
                SFMain.getPlugin(SFMain.class),
                () -> SFMain.profileEntityRepository.update(profile)
        );
    }

    private CustomItem getCustomItemFromHands(Player player) {
        CustomItem customItem = CustomItemBuilder.build(player.getInventory().getItemInMainHand());
        if (customItem == null) customItem = CustomItemBuilder.build(player.getInventory().getItemInOffHand());

        return customItem;
    }

    private Quality scoreToQuality(int score) {
        if (score <= -1) return Quality.BAD;
        if (score <= 2)  return Quality.NORMAL;
        if (score <= 5)  return Quality.GOOD;

        return Quality.EXCELLENT;
    }

    private void giveItemToPlayer(Player player, ItemStack item) throws SFException {
        Map<Integer, ItemStack> remaining = player.getInventory().addItem(item);
        if (!remaining.isEmpty()) throw new SFException(ErrorMessage.get("resource_spot.full_inventory"));
    }

    private void handleErrors(Throwable ex, Player player) {
        String message;
        if (ex instanceof SFException sfex) {
            message = sfex.getMessage();
        } else {
            ex.printStackTrace();
            message = ErrorMessage.get("general.unexpected_error");
        }
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(message, PluginMessageType.ERROR));
    }
}