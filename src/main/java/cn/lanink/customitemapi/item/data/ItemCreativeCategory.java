package cn.lanink.customitemapi.item.data;

/**
 * 控制自定义物品在创造栏的大分类,例如建材栏,材料栏
 * <br>可选值:1 Unknown 2 nature 3 equipment 4 items
 *
 * @return 自定义物品的在创造栏的大分类
 * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-tabs">bedrock wiki</a>
 */
public enum ItemCreativeCategory {
    CONSTRUCTOR,
    NATURE,
    EQUIPMENT,
    ITEMS;

    public static ItemCreativeCategory fromID(int num) {
        switch (num) {
            case 1:
                return CONSTRUCTOR;
            case 3:
                return EQUIPMENT;
            case 4:
                return ITEMS;
            default:
                return NATURE;
        }
    }
}
