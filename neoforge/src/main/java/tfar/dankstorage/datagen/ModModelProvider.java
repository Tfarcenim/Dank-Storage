package tfar.dankstorage.datagen;

import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import tfar.dankstorage.DankStorage;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, DankStorage.MODID);
    }
}
