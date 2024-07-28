package tfar.dankstorage.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.platform.Services;

public class ModBlocks {
    public static final Block dock = new CDockBlock(BlockBehaviour.Properties.of().strength(1, 30), Services.PLATFORM::blockEntity);
}
