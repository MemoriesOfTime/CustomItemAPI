package cn.lanink.customitemapi.item;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
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

    @Override
    public boolean allowOffHand() {
        return false;
    }

    /**
     * 控制自定义物品在创造栏的大分类,例如建材栏,材料栏
     * <br>可选值: 2 nature 3 equipment 4 items
     *
     * @return 自定义物品的在创造栏的大分类
     * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-tabs">bedrock wiki</a>
     */
    @Override
    public int getCreativeCategory() {
        return 4;
    }

    /**
     * 控制自定义物品在创造栏的分组,例如所有的附魔书是一组
     * <p>关于填写的字符串请参阅 <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-categories">bedrock wiki</a>
     *
     * @return 自定义物品的分组
     */
    @Override
    public String getCreativeGroup() {
        return "";
    }

    @Override
    public CompoundTag getComponentsData() {
        return IItemCustom.getComponentsData(this);
    }

    @Override
    public CompoundTag getComponentsData(int protocol) {
        return IItemCustom.getComponentsData(this, protocol);
    }

}
