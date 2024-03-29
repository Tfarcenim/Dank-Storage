package tfar.dankstorage.datagen.tags;

//import mekanism.common.item.block.ItemBlockMultipartAble;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.ModTags;

import javax.annotation.Nullable;

public class ModItemTagsProvider extends ItemTagsProvider {


    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, DankStorage.MODID, existingFileHelper);
    }
    @Override
    protected void addTags() {

        tag(ModTags.UNSTACKABLE).add(Items.BUNDLE);

     //   List<Item> mek_cables = new ArrayList<>();
    //    for (Item item : Registry.ITEM) {
    //        if (item instanceof ItemBlockMultipartAble) {
    //            mek_cables.add(item);
      //      }
    //    }

     //   for (Item item : mek_cables) {
     //       getOrCreateBuilder(ModTags.MEKANISM_CABLES).addOptional(item.getRegistryName());
     //   }
     //   getOrCreateBuilder(ModTags.BLACKLISTED_USAGE).addOptionalTag(ModTags.MEKANISM_CABLES.getName());
     //   for (int i = 1; i < 5;i++) {
            //getOrCreateRawBuilder(ModTags.BLACKLISTED_USAGE).addOptionalTag(new ResourceLocation("pocketstorage","psu_"+i));
      //  }
    }
}
