package tfar.dankstorage.datagen.tags;

//import mekanism.common.item.block.ItemBlockMultipartAble;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.ModTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {


    public ModItemTagsProvider(PackOutput dataGenerator, CompletableFuture<HolderLookup.Provider> pLookupProvider, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, pLookupProvider,blockTagProvider, DankStorage.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

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
