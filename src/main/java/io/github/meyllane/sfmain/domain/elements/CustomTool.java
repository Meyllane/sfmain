package io.github.meyllane.sfmain.domain.elements;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class CustomTool extends CustomItem{
    private final MasterySpeElement masterySpeElement;

    public CustomTool(ItemStack itemStack, CustomItemBase itemBase, Quality quality, MasterySpeElement masterySpeElement) {
        super(itemStack, itemBase, quality);
        this.masterySpeElement = masterySpeElement;
    }

    public MasterySpeElement getMasterySpeElement() {
        return masterySpeElement;
    }

    public void damage(int amount, LivingEntity livingEntity) {
        this.itemStack = this.itemStack.damage(amount, livingEntity);
    }
}
