package tfar.dankstorage.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tfar.dankstorage.datagen.tags.ModBlockTagsProvider;
import tfar.dankstorage.datagen.tags.ModItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModDatagen {

    public static void setupDataGenerator(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();
        if (e.includeServer()) {
            BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider,helper);
            generator.addProvider(true,blockTagsProvider);
            generator.addProvider(true,new ModItemTagsProvider(packOutput,lookupProvider,blockTagsProvider.contentsGetter(),helper));
            generator.addProvider(true,new ModRecipeProvider(packOutput,lookupProvider));
        }
        if (e.includeClient()) {
        }
    }
}
