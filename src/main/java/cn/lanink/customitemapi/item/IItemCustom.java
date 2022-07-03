package cn.lanink.customitemapi.item;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ProtocolInfo;

public interface IItemCustom extends IItem {

    String getTextureName();

    void setTextureName(String textureName);

    int getTextureSize();

    void setTextureSize(int textureSize);

    default boolean allowOffHand() {
        return false;
    }

    default int getCreativeCategory() {
        return 4;
    }

    CompoundTag getComponentsData();

    CompoundTag getComponentsData(int protocol);

    static CompoundTag getComponentsData(IItemCustom item) {
        Server.mvw("ItemCustom#getComponentsData()");
        return getComponentsData(item, ProtocolInfo.CURRENT_PROTOCOL);
    }

    static CompoundTag getComponentsData(IItemCustom item, int protocol) {
        CompoundTag data = new CompoundTag();
        data.putCompound("components", new CompoundTag()
                .putCompound("minecraft:display_name", new CompoundTag()
                        .putString("value", item.getName())
                ).putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", item.allowOffHand())
                        .putBoolean("hand_equipped", item.isTool())
                        .putInt("creative_category", item.getCreativeCategory())
                        .putInt("max_stack_size", item.getMaxStackSize())
                )
        );

        if (protocol >= ProtocolInfo.v1_17_30) {
            data.getCompound("components").getCompound("item_properties")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putString("texture", item.getTextureName() != null ? item.getTextureName() : item.getName()));
        }else {
            data.getCompound("components")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putString("texture", item.getTextureName() != null ? item.getTextureName() : item.getName()));
        }

        if (item.getTextureSize() != 16) {
            float scale1 = (float) (0.075 / (item.getTextureSize() / 16f));
            float scale2 = (float) (0.125 / (item.getTextureSize() / 16f));
            float scale3 = (float) (0.075 / (item.getTextureSize() / 16f * 2.4f));

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

    static CompoundTag xyzToCompoundTag(float x, float y, float z) {
        return new CompoundTag().putCompound("scale", new CompoundTag().putFloat("x", x).putFloat("y", y).putFloat("z", z));
    }

}
