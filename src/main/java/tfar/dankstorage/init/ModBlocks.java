package tfar.dankstorage.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.dankstorage.block.DockBlock;

public class ModBlocks {
    public static final Block dock =  new DockBlock(BlockBehaviour.Properties.of().strength(1, 30));

}
