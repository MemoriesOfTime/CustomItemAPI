package cn.lanink.customitemapi;

import cn.lanink.customitemapi.item.IItemCustom;
import cn.lanink.customitemapi.network.protocol.ItemComponentPacket;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.BinaryStream;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author LT_Name
 */
public class CustomItemAPI extends PluginBase implements Listener {

    public static final String VERSION = "1.0.4-PM1E git-6c2126b";
    private static CustomItemAPI customItemAPI;

    private final HashMap<Integer, Class<? extends IItemCustom>> customItems = new HashMap<>();

    private final List<Integer> supportedProtocol = Arrays.asList(
            ProtocolInfo.v1_16_100,
            ProtocolInfo.v1_17_0,
            ProtocolInfo.v1_17_10,
            ProtocolInfo.v1_18_0,
            ProtocolInfo.v1_18_10_26,
            ProtocolInfo.v1_18_30,
            ProtocolInfo.v1_19_0
    );

    public static CustomItemAPI getInstance() {
        return customItemAPI;
    }

    @Override
    public void onLoad() {
        if (customItemAPI != null) {
            throw new RuntimeException("重复执行onLoad方法");
        }
        customItemAPI = this;

        //自动支持最新协议（可能会导致重复注册但理论无影响）
        //只能做到支持最新，所以中间的仍需要手动添加
        if (ProtocolInfo.CURRENT_PROTOCOL > ProtocolInfo.v1_16_100 && !supportedProtocol.contains(ProtocolInfo.CURRENT_PROTOCOL)) {
            supportedProtocol.add(ProtocolInfo.CURRENT_PROTOCOL);
        }
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        this.getLogger().info("§eCustomItemAPI §aEnabled！ §bVersion：" + VERSION);
    }

    public void registerCustomItem(int id, @NotNull Class<? extends IItemCustom> c) {
        for (int protocol : supportedProtocol) {
            this.registerCustomItem(id, c, protocol);
        }
    }

    public void registerCustomItem(int id, @NotNull Class<? extends IItemCustom> c, int protocol) {
        this.customItems.put(id, c);
        Item.list[id] = c;

        try {
            Class<RuntimeItemMapping> runtimeItemMappingClass = RuntimeItemMapping.class;

            Field runtime2LegacyField = runtimeItemMappingClass.getDeclaredField("runtime2Legacy");
            runtime2LegacyField.setAccessible(true);
            Int2ObjectMap<RuntimeItemMapping.LegacyEntry> runtime2Legacy = (Int2ObjectMap<RuntimeItemMapping.LegacyEntry>) runtime2LegacyField.get(RuntimeItems.getMapping(protocol));

            Field legacy2RuntimeField = runtimeItemMappingClass.getDeclaredField("legacy2Runtime");
            legacy2RuntimeField.setAccessible(true);
            Int2ObjectMap<RuntimeItemMapping.RuntimeEntry> legacy2Runtime = (Int2ObjectMap<RuntimeItemMapping.RuntimeEntry>) legacy2RuntimeField.get(RuntimeItems.getMapping(protocol));

            Field identifier2LegacyField = runtimeItemMappingClass.getDeclaredField("identifier2Legacy");
            identifier2LegacyField.setAccessible(true);
            Map<String, RuntimeItemMapping.LegacyEntry> identifier2Legacy = (Map<String, RuntimeItemMapping.LegacyEntry>) identifier2LegacyField.get(RuntimeItems.getMapping(protocol));


            IItemCustom item = (IItemCustom) Item.get(id);
            int fullId = RuntimeItems.getMapping(protocol).getFullId(item.getId(), 0);

            RuntimeItemMapping.LegacyEntry legacyEntry = new RuntimeItemMapping.LegacyEntry(item.getId(), false, 0);
            runtime2Legacy.put(item.getId(), legacyEntry);
            identifier2Legacy.put(item.getName(), legacyEntry);
            legacy2Runtime.put(fullId, new RuntimeItemMapping.RuntimeEntry(item.getName(), item.getId(), false));


        } catch (Exception e) {
            this.getLogger().error("register custom item error!", e);
        }
        this.generatePalette(protocol);
    }

    private void generatePalette(int protocol) {
        try {
            Class<RuntimeItemMapping> runtimeItemMappingClass = RuntimeItemMapping.class;
            Field legacy2RuntimeField = runtimeItemMappingClass.getDeclaredField("legacy2Runtime");
            legacy2RuntimeField.setAccessible(true);
            Int2ObjectMap<RuntimeItemMapping.RuntimeEntry> legacy2Runtime =
                    (Int2ObjectMap<RuntimeItemMapping.RuntimeEntry>) legacy2RuntimeField.get(RuntimeItems.getMapping(protocol));

            BinaryStream paletteBuffer = new BinaryStream();
            paletteBuffer.putUnsignedVarInt(legacy2Runtime.size());

            for (RuntimeItemMapping.RuntimeEntry entry : legacy2Runtime.values()) {
                if (this.customItems.containsKey(entry.getRuntimeId())) {
                    paletteBuffer.putString(("customitem:" + entry.getIdentifier()).toLowerCase());
                    paletteBuffer.putLShort(entry.getRuntimeId());
                    paletteBuffer.putBoolean(true);
                }else {
                    paletteBuffer.putString(entry.getIdentifier());
                    paletteBuffer.putLShort(entry.getRuntimeId());
                    paletteBuffer.putBoolean(false);
                }
            }

            Field itemPaletteField = runtimeItemMappingClass.getDeclaredField("itemPalette");
            itemPaletteField.setAccessible(true);
            itemPaletteField.set(RuntimeItems.getMapping(protocol), paletteBuffer.getBuffer());
        } catch (Exception e) {
            this.getLogger().error("generate palette error!", e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemComponentPacket itemComponentPacket = new ItemComponentPacket();
        itemComponentPacket.entries = new ItemComponentPacket.Entry[this.customItems.size()];

        int i = 0;
        for (Integer id : this.customItems.keySet()) {
            Item item = Item.get(id);
            if (!(item instanceof IItemCustom)) {
                continue;
            }

            IItemCustom itemCustom = (IItemCustom) item;
            CompoundTag data = itemCustom.getComponentsData(player.protocol);
            data.putShort("minecraft:identifier", i);

            itemComponentPacket.entries[i] = new ItemComponentPacket.Entry(("customitem:" + item.getName()).toLowerCase(), data);

            i++;
        }

        player.dataPacket(itemComponentPacket);
    }

    @EventHandler
    public void onDataPacketSend(DataPacketSendEvent event) {
        if (event.getPacket() instanceof ResourcePackStackPacket) {
            ResourcePackStackPacket pk = (ResourcePackStackPacket) event.getPacket();
            pk.experiments.add(
                    new ResourcePackStackPacket.ExperimentData("data_driven_items", true)
            );
            pk.experiments.add(
                    new ResourcePackStackPacket.ExperimentData("experimental_custom_ui", true)
            );
            pk.encode();
        }

    }

}
