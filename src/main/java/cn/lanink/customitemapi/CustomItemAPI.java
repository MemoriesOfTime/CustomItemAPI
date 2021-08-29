package cn.lanink.customitemapi;

import cn.lanink.customitemapi.item.ItemCustom;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author LT_Name
 */
public class CustomItemAPI extends PluginBase implements Listener {

    public static final String VERSION = "?";
    private static CustomItemAPI customItemAPI;

    private final HashMap<Integer, Class<? extends Item>> customItems = new HashMap<>();

    public static CustomItemAPI getInstance() {
        return customItemAPI;
    }

    @Override
    public void onLoad() {
        if (customItemAPI != null) {
            throw new RuntimeException("重复执行onLoad方法");
        }
        customItemAPI = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        this.getLogger().info("§eCustomItemAPI §aEnabled！ §bVersion：" + VERSION);
    }

    public void registerCustomItem(int id, @NotNull Class<? extends ItemCustom> c) {
        this.registerCustomItem(id, c, ProtocolInfo.v1_17_10);
    }

    public void registerCustomItem(int id, @NotNull Class<? extends ItemCustom> c, int protocol) {
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


            ItemCustom item = (ItemCustom) Item.get(id);
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
                    paletteBuffer.putString(entry.getIdentifier());
                    paletteBuffer.putLShort(entry.getRuntimeId());
                    // Component item
                    paletteBuffer.putBoolean(true);
                }else {
                    paletteBuffer.putString(entry.getIdentifier());
                    paletteBuffer.putLShort(entry.getRuntimeId());
                    // Component item
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
            if (!(item instanceof ItemCustom)) {
                continue;
            }

            ItemCustom itemCustom = (ItemCustom) item;
            CompoundTag data = itemCustom.getComponentsData();
            data.putShort("minecraft:identifier", i);

            itemComponentPacket.entries[i] = new ItemComponentPacket.Entry(item.getName(), data);

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
