package cn.lanink.customitemapi.item;

import cn.nukkit.item.ItemArmor;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;

public abstract class ItemCustomArmor extends ItemArmor implements IItemCustom {

    private String textureName;

    private int textureSize = 16;

    public ItemCustomArmor(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemCustomArmor(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemCustomArmor(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemCustomArmor(int id, Integer meta, int count, String name) {
        this(id, meta, count, name, name);
    }

    public ItemCustomArmor(int id, Integer meta, int count, String name, String textureName) {
        super(id, meta, count, name);
        this.textureName = textureName;
    }

    @Override
    public CompoundTag getComponentsData() {
        return IItemCustom.getComponentsData(this);
    }

    @Override
    public CompoundTag getComponentsData(int protocol) {
        CompoundTag data = IItemCustom.getComponentsData(this, protocol);

        data.getCompound("components")
                .putCompound("minecraft:armor", new CompoundTag()
                        .putInt("protection", this.getArmorPoints()))
                .putCompound("minecraft:durability", new CompoundTag()
                        .putInt("max_durability", this.getMaxDurability()));

        if (this.isHelmet()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.head");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putString("slot", "slot.armor.head"));
        } else if (this.isChestplate()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.chest");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putString("slot", "slot.armor.chest"));
        } else if (this.isLeggings()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.legs");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putString("slot", "slot.armor.legs"));
        } else if (this.isBoots()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("wearable_slot", "slot.armor.feet");
            data.getCompound("components")
                    .putCompound("minecraft:wearable", new CompoundTag()
                            .putBoolean("dispensable", true)
                            .putString("slot", "slot.armor.feet"));
        }

        return data;
    }

    @Override
    public String getTextureName() {
        return this.textureName;
    }

    @Override
    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    @Override
    public int getTextureSize() {
        return this.textureSize;
    }

    @Override
    public void setTextureSize(int textureSize) {
        this.textureSize = textureSize;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 166;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public boolean isHelmet() {
        return false;
    }

    @Override
    public boolean isChestplate() {
        return false;
    }

    @Override
    public boolean isLeggings() {
        return false;
    }

    @Override
    public boolean isBoots() {
        return false;
    }

    @Override
    public int getArmorPoints() {
        return 1;
    }

}
