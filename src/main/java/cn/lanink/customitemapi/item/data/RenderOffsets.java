package cn.lanink.customitemapi.item.data;

import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import org.jetbrains.annotations.Nullable;

public class RenderOffsets {
    public final CompoundTag nbt = new CompoundTag();
    public RenderOffsets(@Nullable Offset mainHandFirstPerson, @Nullable Offset mainHandThirdPerson, @Nullable Offset offHandFirstPerson, @Nullable Offset offHandThirdPerson) {
        if (mainHandFirstPerson != null || mainHandThirdPerson != null) {
            this.nbt.putCompound("main_hand", new CompoundTag());
            if (mainHandFirstPerson != null) {
                this.nbt.getCompound("main_hand").putCompound("first_person", xyzToCompoundTag(mainHandFirstPerson.getPosition(), mainHandFirstPerson.getRotation(), mainHandFirstPerson.getScale()));
            }
            if (mainHandThirdPerson != null) {
                this.nbt.getCompound("main_hand").putCompound("third_person", xyzToCompoundTag(mainHandThirdPerson.getPosition(), mainHandThirdPerson.getRotation(), mainHandThirdPerson.getScale()));
            }
        }
        if (offHandFirstPerson != null || offHandThirdPerson != null) {
            this.nbt.putCompound("off_hand", new CompoundTag());
            if (offHandFirstPerson != null) {
                this.nbt.getCompound("off_hand").putCompound("first_person", xyzToCompoundTag(offHandFirstPerson.getPosition(), offHandFirstPerson.getRotation(), offHandFirstPerson.getScale()));
            }
            if (offHandThirdPerson != null) {
                this.nbt.getCompound("off_hand").putCompound("third_person", xyzToCompoundTag(offHandThirdPerson.getPosition(), offHandThirdPerson.getRotation(), offHandThirdPerson.getScale()));
            }
        } else if (mainHandFirstPerson == null && mainHandThirdPerson == null)
            throw new IllegalArgumentException("Do not allow all parameters to be empty, if you do not want to specify, please do not use the renderOffsets method");
    }

    public static RenderOffsets scaleOffset(double multiplier) {
        if (multiplier < 0) {
            multiplier = 1;
        }
        float scale1 = (float) (0.075 / multiplier);
        float scale2 = (float) (0.125 / multiplier);
        float scale3 = (float) (0.075 / (multiplier * 2.4f));
        return new RenderOffsets(
                Offset.builder().scale(scale3, scale3, scale3),
                Offset.builder().scale(scale1, scale2, scale1),
                Offset.builder().scale(scale1, scale2, scale1),
                Offset.builder().scale(scale1, scale2, scale1)
        );
    }

    public static RenderOffsets scaleOffset(int textureSize) {
        double multiplier = textureSize / 16f;
        return scaleOffset(multiplier);
    }

    private CompoundTag xyzToCompoundTag(Vector3f pos, Vector3f rot, Vector3f sc) {
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
