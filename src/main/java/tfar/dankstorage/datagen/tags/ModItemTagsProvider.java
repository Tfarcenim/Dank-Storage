package tfar.dankstorage.datagen.tags;

import mekanism.common.item.block.ItemBlockMultipartAble;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.ModTags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModItemTagsProvider extends ItemTagsProvider {


    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, DankStorage.MODID, existingFileHelper);
    }

    @Override
    protected void registerTags() {
        List<Item> mek_cables = new ArrayList<>();
        for (Item item : Registry.ITEM) {
            if (item instanceof ItemBlockMultipartAble) {
                mek_cables.add(item);
            }
        }

        for (Item item : mek_cables) {
            getOrCreateBuilder(ModTags.MEKANISM_CABLES).addOptional(item.getRegistryName());
        }
        getOrCreateBuilder(ModTags.BLACKLISTED_USAGE).addOptionalTag(ModTags.MEKANISM_CABLES.getName());
        for (int i = 1; i < 5;i++) {
            getOrCreateBuilder(ModTags.BLACKLISTED_USAGE).addOptional(new ResourceLocation("pocketstorage","psu_"+i));
        }
    }
}
