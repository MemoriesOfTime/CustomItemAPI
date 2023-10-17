package cn.lanink.customitemapi.item;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class ItemCustom extends Item implements IItemCustom {

    private String textureName;

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

    @Override
    public CompoundTag getComponentsData() {
        return IItemCustom.getComponentsData(this);
    }

    @Override
    public CompoundTag getComponentsData(int protocol) {
        return IItemCustom.getComponentsData(this, protocol);
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

}
