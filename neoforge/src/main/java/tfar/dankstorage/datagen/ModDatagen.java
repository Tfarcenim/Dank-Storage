package tfar.dankstorage.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tfar.dankstorage.datagen.tags.ModBlockTagsProvider;
import tfar.dankstorage.datagen.tags.ModItemTagsProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class ModDatagen {

    public static void setup(IEventBus bus) {
        bus.addListener(ModDatagen::clientDataGen);
    }

    public static void clientDataGen(GatherDataEvent.Client e) {
        DataGenerator generator = e.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();
            BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider);
            generator.addProvider(true,blockTagsProvider);
            generator.addProvider(true,new ModItemTagsProvider(packOutput,lookupProvider));

        generator.addProvider(true,bindRegistries(ModRecipeProvider.Runner::new, lookupProvider));
        generator.addProvider(true,ModLootTableProvider.create(packOutput,lookupProvider));
        generator.addProvider(true,new ModModelProvider(packOutput));
    }

    public static void setupDataGenerator(GatherDataEvent.Server e) {
        DataGenerator generator = e.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider);
        generator.addProvider(true,blockTagsProvider);
        generator.addProvider(true,new ModItemTagsProvider(packOutput,lookupProvider));

        generator.addProvider(true,bindRegistries(ModRecipeProvider.Runner::new, lookupProvider));
        generator.addProvider(true,ModLootTableProvider.create(packOutput,lookupProvider));
    }

    private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
            BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> target, CompletableFuture<HolderLookup.Provider> registries
    ) {
        return output -> target.apply(output, registries);
    }
}
