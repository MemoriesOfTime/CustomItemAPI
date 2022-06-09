package cn.lanink.customitemapi.item;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;

public abstract class ItemCustom extends Item implements IItemCustom {

    @Setter
    @Getter
    private String textureName;

    @Setter
    @Getter
    private int textureSize = 16;

    public ItemCustom(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemCustom(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemCustom(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemCustom(int id, Integer meta, int count, String name) {
        this(id, meta, count, name, name);
    }

    public ItemCustom(int id, Integer meta, int count, String name, String textureName) {
        super(id, meta, count, name);
        this.textureName = textureName;
    }

    public boolean allowOffHand() {
        return false;
    }

    public int getCreativeCategory() {
        return 4;
    }

    public CompoundTag getComponentsData() {
        return IItemCustom.getComponentsData(this);
    }

    public CompoundTag getComponentsData(int protocol) {
        return IItemCustom.getComponentsData(this, protocol);
    }

    private static CompoundTag xyzToCompoundTag(float x, float y, float z) {
        return new CompoundTag().putCompound("scale", new CompoundTag().putFloat("x", x).putFloat("y", y).putFloat("z", z));
    }

}
