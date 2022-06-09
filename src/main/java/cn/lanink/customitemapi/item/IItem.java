package cn.lanink.customitemapi.item;

/**
 * 声明一些IItemCustom需要用到的Item里的基础方法
 */
public interface IItem {

    int getId();

    String getName();

    int getMaxStackSize();

    boolean isTool();

}
