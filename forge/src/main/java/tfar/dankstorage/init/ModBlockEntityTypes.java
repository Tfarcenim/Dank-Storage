package tfar.dankstorage.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.dankstorage.blockentity.DockBlockEntity;

public class ModBlockEntityTypes {
    public static BlockEntityType<DockBlockEntity> dank_tile = BlockEntityType.Builder.of(DockBlockEntity::new, ModBlocks.dock).build(null);

}
