package cn.lanink.customitemapi.item;

import cn.nukkit.Server;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ProtocolInfo;

public interface IItemCustom extends IItem {

    default CustomItemDefinition getDefinition() {
        return null;
    }

    String getTextureName();

    void setTextureName(String textureName);

    int getTextureSize();

    void setTextureSize(int textureSize);

    default boolean allowOffHand() {
        if (this.getDefinition() != null) {
            return this.getDefinition().allowOffHand();
        }
        return false;
    }

    default int getCreativeCategory() {
        if (this.getDefinition() != null) {
            return this.getDefinition().getCreativeCategory();
        }
        return 4;
    }

    default String getCreativeGroup() {
        if (this.getDefinition() != null) {
            return this.getDefinition().getCreativeGroup();
        }
        return "none";
    }

    /**
     * Возвращает описание предмета для отображения в имени.
     * @return описание предмета
     */
    default String[] getItemDescription() {
        return new String[0]; // Возвращаем пустой массив по умолчанию, если описание не задано
    }

    CompoundTag getComponentsData();

    CompoundTag getComponentsData(int protocol);

    static CompoundTag getComponentsData(IItemCustom item, int protocol) {
        if (item.getDefinition() != null) {
            return item.getDefinition().getNbt(protocol);
        }

        CompoundTag data = new CompoundTag();
        CompoundTag components = new CompoundTag();

        // Добавляем отображаемое имя с описанием
        StringBuilder displayName = new StringBuilder(item.getName());
        String[] description = item.getItemDescription();
        for (String line : description) {
            displayName.append("\n").append(line);
        }
        components.putCompound("minecraft:display_name", new CompoundTag().putString("value", displayName.toString()));

        // Добавляем остальные компоненты
        components.putCompound("item_properties", new CompoundTag()
                .putBoolean("allow_off_hand", item.allowOffHand())
                .putBoolean("hand_equipped", item.isTool())
                .putInt("creative_category", item.getCreativeCategory())
                .putInt("max_stack_size", item.getMaxStackSize())
        );

        if (!item.getCreativeGroup().isEmpty()) {
            components.getCompound("item_properties")
                    .putString("creative_group", item.getCreativeGroup());
        }

        if (protocol >= ProtocolInfo.v1_20_60) {
            components.getCompound("item_properties")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putCompound("textures", new CompoundTag().putString("default", item.getTextureName() != null ? item.getTextureName() : item.getName())));
        } else if (protocol >= ProtocolInfo.v1_17_30) {
            components.getCompound("item_properties")
                    .putCompound("minecraft:icon", new CompoundTag()
                            .putString("texture", item.getTextureName() != null ? item.getTextureName() : item.getName()));
        } else {
            components.putCompound("minecraft:icon", new CompoundTag()
                    .putString("texture", item.getTextureName() != null ? item.getTextureName() : item.getName()));
        }

        if (item.getTextureSize() != 16) {
            float scale1 = (float) (0.075 / (item.getTextureSize() / 16f));
            float scale2 = (float) (0.125 / (item.getTextureSize() / 16f));
            float scale3 = (float) (0.075 / (item.getTextureSize() / 16f * 2.4f));

            CompoundTag offsets;
            if (protocol >= ProtocolInfo.v1_19_0) {
                offsets = new CompoundTag()
                        .putCompound("main_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(null, null, new Vector3f(scale3, scale3, scale3)))
                                .putCompound("third_person", xyzToCompoundTag(null, null, new Vector3f(scale1, scale2, scale1)))
                        ).putCompound("off_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(null, null, new Vector3f(scale1, scale2, scale1)))
                                .putCompound("third_person", xyzToCompoundTag(null, null, new Vector3f(scale1, scale2, scale1)))
                        );
            } else {
                offsets = new CompoundTag()
                        .putCompound("main_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(scale3, scale3, scale3))
                                .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                        ).putCompound("off_hand", new CompoundTag()
                                .putCompound("first_person", xyzToCompoundTag(scale1, scale2, scale1))
                                .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                        );
            }
            components.putCompound("minecraft:render_offsets", offsets);
        }

        data.putCompound("components", components);
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
