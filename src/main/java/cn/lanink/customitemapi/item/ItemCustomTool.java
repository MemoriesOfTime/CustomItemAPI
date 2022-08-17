package cn.lanink.customitemapi.item;

import cn.nukkit.item.ItemTool;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.Getter;
import lombok.Setter;

public abstract class ItemCustomTool extends ItemTool implements IItemCustom {

    @Setter
    @Getter
    private String textureName;

    @Setter
    @Getter
    private int textureSize = 16;

    public ItemCustomTool(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemCustomTool(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemCustomTool(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemCustomTool(int id, Integer meta, int count, String name) {
        this(id, meta, count, name, name);
    }

    public ItemCustomTool(int id, Integer meta, int count, String name, String textureName) {
        super(id, meta, count, name);
        this.textureName = textureName;
    }

    @Override
    public int getCreativeCategory() {
        return 3;
    }

    @Override
    public CompoundTag getComponentsData() {
        return IItemCustom.getComponentsData(this);
    }

    @Override
    public CompoundTag getComponentsData(int protocol) {
        CompoundTag data = IItemCustom.getComponentsData(this, protocol);

        data.getCompound("components")
                .putCompound("minecraft:durability",
                        new CompoundTag().putInt("max_durability", this.getMaxDurability())
                )
                .getCompound("item_properties")
                .putInt("damage", this.getAttackDamage());

        if(this.isPickaxe()) {
            data.getCompound("components")
                    .putCompound("minecraft:digger", getPickaxeDiggerNBT(this));
        }else if(this.isAxe()) {
            data.getCompound("components")
                    .putCompound("minecraft:digger", getAxeDiggerNBT(this));
        }else if(this.isShovel()) {
            data.getCompound("components")
                    .putCompound("minecraft:digger", getShovelDiggerNBT(this));
        }

        return data;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }

    /**
     * 控制工具的挖掘速度(默认0 使用工具等级计算挖掘速度)
     *
     * @return 挖掘速度
     */
    public int getDestroySpeeds() {
        return 0;
    }

    public static CompoundTag getPickaxeDiggerNBT(ItemCustomTool item) {
        int speed = 1;
        if (item.getDestroySpeeds() > 0) {
            speed = item.getDestroySpeeds();
        }else {
            int tier = item.getTier();
            switch (tier) {
                case 5:
                    speed = 6;
                    break;
                case 4:
                    speed = 5;
                    break;
                case 3:
                    speed = 4;
                    break;
                case 2:
                    speed = 3;
                    break;
                case 1:
                    speed = 2;
                    break;
                case 0:
                    return new CompoundTag().putBoolean("use_efficiency", true);
            }
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags",
                                        "q.any_tag('stone', 'metal', 'diamond_pick_diggable', 'mob_spawner', 'rail')"))
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

    public static CompoundTag getAxeDiggerNBT(ItemCustomTool item) {
        int speed = 1;
        if (item.getDestroySpeeds() > 0) {
            speed = item.getDestroySpeeds();
        }else {
            int tier = item.getTier();
            switch (tier) {
                case 5:
                    speed = 6;
                    break;
                case 4:
                    speed = 5;
                    break;
                case 3:
                    speed = 4;
                    break;
                case 2:
                    speed = 3;
                    break;
                case 1:
                    speed = 2;
                    break;
                case 0:
                    return new CompoundTag().putBoolean("use_efficiency", true);
            }
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags", "q.any_tag('wood', 'pumpkin', 'plant')"))
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

    public static CompoundTag getShovelDiggerNBT(ItemCustomTool item){
        int speed = 1;
        if (item.getDestroySpeeds() > 0) {
            speed = item.getDestroySpeeds();
        }else {
            int tier = item.getTier();
            switch (tier) {
                case 5:
                    speed = 6;
                    break;
                case 4:
                    speed = 5;
                    break;
                case 3:
                    speed = 4;
                    break;
                case 2:
                    speed = 3;
                    break;
                case 1:
                    speed = 2;
                    break;
                case 0:
                    return new CompoundTag().putBoolean("use_efficiency", true);
            }
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags", "q.any_tag('sand', 'dirt', 'gravel', 'snow')"))
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

}
