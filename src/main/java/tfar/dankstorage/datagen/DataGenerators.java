package tfar.dankstorage.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tfar.dankstorage.datagen.tags.ModBlockTagsProvider;
import tfar.dankstorage.datagen.tags.ModItemTagsProvider;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void setupDataGenerator(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();
        if (e.includeServer()) {
            BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(generator,helper);
            generator.addProvider(true,new ModItemTagsProvider(generator,blockTagsProvider,helper));
        }
        if (e.includeClient()) {
        }
    }
}
