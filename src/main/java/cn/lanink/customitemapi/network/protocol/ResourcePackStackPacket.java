package cn.lanink.customitemapi.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@ToString
public class ResourcePackStackPacket extends cn.nukkit.network.protocol.ResourcePackStackPacket {

    public final List<ExperimentData> experiments = new ObjectArrayList<>();

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putUnsignedVarInt(this.behaviourPackStack.length);
        for (ResourcePack entry : this.behaviourPackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString("");
        }
        this.putUnsignedVarInt(this.resourcePackStack.length);
        for (ResourcePack entry : this.resourcePackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString("");
        }
        this.putString(this.gameVersion);
        this.putLInt(this.experiments.size());
        for (ExperimentData experimentData : this.experiments) {
            this.putString(experimentData.getName());
            this.putBoolean(experimentData.isEnabled());
        }
        this.putBoolean(false); // Were experiments previously toggled
    }

    @Value
    public static class ExperimentData {
        String name;
        boolean enabled;
    }
}
