package io.github.meyllane.sfmain.domain.elements;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CustomItemBuilder {

    public static CustomItem build(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) return null;

        PersistentDataContainerView view = itemStack.getPersistentDataContainer();

        Integer customItemID = view.get(SFMain.CUSTOM_ITEM_ID_NK, PersistentDataType.INTEGER);
        if (customItemID == null) return null;

        CustomItemBase base = SFMain.customItemsRegistry.get(customItemID);
        if (base == null) return null;

        Integer qualityID = view.get(SFMain.CUSTOM_ITEM_QUALITY_ID_NK, PersistentDataType.INTEGER);
        if (qualityID == null) return null;

        Quality quality = null;
        try {
            quality = Quality.getByID(qualityID);
        } catch (Exception e) {
            if (e instanceof SFException sfex) return null;
            throw e;
        }

        if (base instanceof CustomToolBase toolBase) {
            return buildTool(itemStack, toolBase, quality, view);
        }

        return new CustomItem(itemStack, base, quality);
    }

    private static CustomTool buildTool(
            ItemStack itemStack,
            CustomToolBase toolBase,
            Quality quality,
            PersistentDataContainerView view
    ) {
        Integer masterySpeID = view.get(SFMain.CUSTOM_ITEM_MASTERY_SPE_ID_NK, PersistentDataType.INTEGER);
        if (masterySpeID == null) return null;

        MasterySpeElement masterySpeElement = SFMain.masterySpeElementRegistry.get(masterySpeID);
        if (masterySpeElement == null) return null;

        return new CustomTool(itemStack, toolBase, quality, masterySpeElement);
    }
}
