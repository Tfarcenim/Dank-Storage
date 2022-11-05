package tfar.dankstorage.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import tfar.dankstorage.block.DockBlock;

public class ModBlocks {
    public static final Block dock =  new DockBlock(BlockBehaviour.Properties.of(Material.METAL).strength(1, 30));

}
