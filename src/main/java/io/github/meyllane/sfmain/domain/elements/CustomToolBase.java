package io.github.meyllane.sfmain.domain.elements;

import io.github.meyllane.sfmain.SFMain;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CustomToolBase extends CustomItemBase {
    private MasterySpeElement masterySpeElement;
    private int maxDurability;

    public CustomToolBase(Integer id, String name, String shortName, List<String> description, Material material, String texture) {
        super(id, name, shortName, description, material, texture);
    }

    public MasterySpeElement getMasterySpeElement() {
        return masterySpeElement;
    }

    public void setMasterySpeElement(MasterySpeElement masterySpeElement) {
        this.masterySpeElement = masterySpeElement;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public void setMaxDurability(int maxDurability) {
        this.maxDurability = maxDurability;
    }

    @Override
    public ItemStack toItemStack(Quality quality, int amount) {
        ItemStack stack = super.toItemStack(quality, amount);

        stack.editPersistentDataContainer(pdc -> {
            pdc.set(SFMain.CUSTOM_ITEM_MASTERY_SPE_ID_NK, PersistentDataType.INTEGER, masterySpeElement.getId());
        });

        List<Component> lore = stack.lore();

        lore.add(Component.text("Spécialisation : " + masterySpeElement.getName()).color(TextColor.fromHexString(CustomItemBase.DEFAULT_INFO_COLOR)));

        stack.lore(lore);

        this.maxDurability = quality.getBaseDurability();
        stack.setData(DataComponentTypes.MAX_DAMAGE, maxDurability);

        return stack;
    }
}
