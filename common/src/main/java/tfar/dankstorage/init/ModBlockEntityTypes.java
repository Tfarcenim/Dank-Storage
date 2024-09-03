package tfar.dankstorage.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.platform.Services;

public class ModBlockEntityTypes {
    public static final BlockEntityType<DockBlockEntity> dock = BlockEntityType.Builder.of(DockBlockEntity::new, ModBlocks.dock).build(null);

}
