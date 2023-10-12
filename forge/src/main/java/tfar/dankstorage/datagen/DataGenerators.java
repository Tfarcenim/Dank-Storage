package tfar.dankstorage.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tfar.dankstorage.datagen.tags.ModBlockTagsProvider;
import tfar.dankstorage.datagen.tags.ModItemTagsProvider;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void setupDataGenerator(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();
        if (e.includeServer()) {
            BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider,helper);
            generator.addProvider(true,new ModItemTagsProvider(packOutput,lookupProvider,blockTagsProvider.contentsGetter(),helper));
        }
        if (e.includeClient()) {
        }
    }
}
