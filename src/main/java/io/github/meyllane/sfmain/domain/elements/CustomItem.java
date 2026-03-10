package io.github.meyllane.sfmain.domain.elements;

import org.bukkit.inventory.ItemStack;

public class CustomItem {
    protected ItemStack itemStack;
    protected final CustomItemBase itemBase;
    protected final Quality quality;

    public CustomItem(ItemStack itemStack, CustomItemBase itemBase, Quality quality) {
        this.itemStack = itemStack;
        this.itemBase = itemBase;
        this.quality = quality;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public CustomItemBase getItemBase() {
        return itemBase;
    }

    public Quality getQuality() {
        return quality;
    }
}
