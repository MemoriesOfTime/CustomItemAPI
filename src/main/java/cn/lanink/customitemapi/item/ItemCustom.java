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
        Server.mvw("ItemCustom#getComponentsData()");
        return this.getComponentsData(ProtocolInfo.CURRENT_PROTOCOL);

    }

    public CompoundTag getComponentsData(int protocol) {
        CompoundTag data = new CompoundTag();
        data.putCompound("components", new CompoundTag()
                .putCompound("minecraft:display_name", new CompoundTag()
                        .putString("value", this.getName())
                ).putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", this.allowOffHand())
                        .putBoolean("hand_equipped", this.isTool())
                        .putInt("creative_category", this.getCreativeCategory())
                        .putInt("max_stack_size", this.getMaxStackSize())
                )
        );

        if (protocol >= ProtocolInfo.v1_17_30) {
            data.getCompound("components").getCompound("item_properties")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putString("texture", this.getTextureName() != null ? this.getTextureName() : this.name));
        }else {
            data.getCompound("components")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putString("texture", this.getTextureName() != null ? this.getTextureName() : this.name));
        }

        if (this.getTextureSize() != 16) {
            float scale1 = (float) (0.075 / (this.getTextureSize() / 16f));
            float scale2 = (float) (0.125 / (this.getTextureSize() / 16f));
            float scale3 = (float) (0.075 / (this.getTextureSize() / 16f * 2.4f));

            data.getCompound("components")
                    .putCompound("minecraft:render_offsets", new CompoundTag()
                            .putCompound("main_hand", new CompoundTag()
                                    .putCompound("first_person", xyzToCompoundTag(scale3, scale3, scale3))
                                    .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                            ).putCompound("off_hand", new CompoundTag()
                                    .putCompound("first_person", xyzToCompoundTag(scale1, scale2, scale1))
                                    .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                            )
                    );
        }

        return data;
    }

    private static CompoundTag xyzToCompoundTag(float x, float y, float z) {
        return new CompoundTag().putCompound("scale", new CompoundTag().putFloat("x", x).putFloat("y", y).putFloat("z", z));
    }

}
