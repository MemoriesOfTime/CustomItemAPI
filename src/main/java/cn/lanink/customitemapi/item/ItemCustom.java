package cn.lanink.customitemapi.item;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.Getter;
import lombok.Setter;

public abstract class ItemCustom extends Item {

    @Setter
    @Getter
    private String textureName;

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
        Server.mvw("ItemCustom#getComponentsData()");
        return this.getComponentsData(ProtocolInfo.CURRENT_PROTOCOL);

    }

    public CompoundTag getComponentsData(int protocol) {
        CompoundTag data = new CompoundTag();
        data.putCompound("components", new CompoundTag()
                .putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", this.allowOffHand())
                        .putBoolean("hand_equipped", this.isTool())
                        .putInt("creative_category", this.getCreativeCategory())
                        .putInt("max_stack_size", this.getMaxStackSize())));
        if (protocol >= ProtocolInfo.v1_17_30) {
            data.getCompound("components").getCompound("item_properties")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putString("texture", this.getTextureName() != null ? this.getTextureName() : this.name));
        }else {
            data.getCompound("components")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putString("texture", this.getTextureName() != null ? this.getTextureName() : this.name));
        }
        return data;
    }

}
