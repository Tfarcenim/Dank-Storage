package tfar.dankstorage.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;

public class ModBlocks {
    public static final Block DOCK = new DockBlock(BlockBehaviour.Properties.of().strength(1, 30).setId(key("dock")));

    static ResourceKey<Block> key(String s) {
        return ResourceKey.create(Registries.BLOCK,DankStorage.id(s));
    }

    static {
        Registry.register(BuiltInRegistries.BLOCK, DankStorage.id("dock"), DOCK);
    }

    public static void init() {

    }
}
