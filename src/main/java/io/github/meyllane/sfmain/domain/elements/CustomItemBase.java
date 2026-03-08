package io.github.meyllane.sfmain.domain.elements;

import io.github.meyllane.sfmain.SFMain;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;

public class CustomItemBase extends Element{
    private String shortName;
    private Material material;
    private String texture;
    private List<String> description;

    public static final String DEFAULT_LORE_COLOR = "#718699";
    public static final String DEFAULT_INFO_COLOR = "#5E5E5E";

    public CustomItemBase(Integer id, String name, String shortName, List<String> description, Material material, String texture) {
        super(id, name);
        this.shortName = shortName;
        this.description = description;
        this.material = material;
        this.texture = texture;
    }

    public String getShortName() {
        return shortName;
    }

    public Material getMaterial() {
        return material;
    }

    public String getTexture() {
        return texture;
    }

    //TODO: Handle description
    public ItemStack toItemStack(Quality quality, int amount) {
        ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(amount);

        List<Component> lore = description.stream()
                .map(elem -> Component.text(elem).color(TextColor.fromHexString(DEFAULT_LORE_COLOR)))
                .collect(Collectors.toList());

        lore.add(Component.text(""));

        //Data
        lore.add(Component.text("ID : " + this.id).color(TextColor.fromHexString(DEFAULT_INFO_COLOR)));
        lore.add(Component.text("Qualité : " + quality.getName()).color(TextColor.fromHexString(DEFAULT_INFO_COLOR)));

        itemStack.editMeta(meta -> {
           meta.displayName(
                   Component.text(this.name).color(TextColor.fromHexString(quality.getColor()))
           );

           meta.lore(lore);
        });

        itemStack.editPersistentDataContainer(pdc -> {
           pdc.set(SFMain.CUSTOM_ITEM_ID_NK, PersistentDataType.INTEGER, this.id);
           pdc.set(SFMain.CUSTOM_ITEM_QUALITY_ID_NK, PersistentDataType.INTEGER, quality.getID());
        });

        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData()
                        .addString(this.texture)
                        .build());

        return itemStack;
    }
}
