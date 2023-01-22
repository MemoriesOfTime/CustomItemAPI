package cn.lanink.customitemapi.item;

import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
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

    default String getCreativeGroup() {
        return "";
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

        if (!item.getCreativeGroup().isEmpty()) {
            data.getCompound("components").getCompound("item_properties")
                    .putString("creative_group", item.getCreativeGroup());
        }

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

            CompoundTag offsets;
            if (protocol >= ProtocolInfo.v1_19_0) { //TODO 检查具体是哪个版本修改的
                offsets = new CompoundTag()
                        .putCompound("main_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(null, null, new Vector3f(scale3, scale3, scale3)))
                                .putCompound("third_person", xyzToCompoundTag(null, null, new Vector3f(scale1, scale2, scale1)))
                        ).putCompound("off_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(null, null, new Vector3f(scale1, scale2, scale1)))
                                .putCompound("third_person", xyzToCompoundTag(null, null, new Vector3f(scale1, scale2, scale1)))
                        );
            }else {
                offsets = new CompoundTag()
                        .putCompound("main_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(scale3, scale3, scale3))
                                .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                        ).putCompound("off_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(scale1, scale2, scale1))
                                .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                );
            }
            data.getCompound("components").putCompound("minecraft:render_offsets", offsets);
        }

        return data;
    }

    static CompoundTag xyzToCompoundTag(float x, float y, float z) {
        return new CompoundTag().putCompound("scale", new CompoundTag().putFloat("x", x).putFloat("y", y).putFloat("z", z));
    }

    static CompoundTag xyzToCompoundTag(Vector3f pos, Vector3f rot, Vector3f sc) {
        CompoundTag result = new CompoundTag();
        if (pos != null) {
            ListTag<FloatTag> position = new ListTag<>("position");
            position.add(new FloatTag("", pos.x));
            position.add(new FloatTag("", pos.y));
            position.add(new FloatTag("", pos.z));
            result.putList(position);
        }
        if (rot != null) {
            ListTag<FloatTag> rotation = new ListTag<>("rotation");
            rotation.add(new FloatTag("", rot.x));
            rotation.add(new FloatTag("", rot.y));
            rotation.add(new FloatTag("", rot.z));
            result.putList(rotation);
        }
        if (sc != null) {
            ListTag<FloatTag> scale = new ListTag<>("scale");
            scale.add(new FloatTag("", sc.x));
            scale.add(new FloatTag("", sc.y));
            scale.add(new FloatTag("", sc.z));
            result.putList(scale);
        }
        return result;
    }

}
