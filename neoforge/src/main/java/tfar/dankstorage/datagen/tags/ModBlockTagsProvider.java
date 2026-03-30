package tfar.dankstorage.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.init.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput generatorIn, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generatorIn,lookupProvider, DankStorage.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.dock);
    }
}
