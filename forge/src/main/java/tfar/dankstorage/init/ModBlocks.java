package tfar.dankstorage.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.dankstorage.block.CommonDockBlock;
import tfar.dankstorage.blockentity.DockBlockEntity;

public class ModBlocks {
    public static final Block dock =  new CommonDockBlock(BlockBehaviour.Properties.of().strength(1, 30), DockBlockEntity::new);
}
