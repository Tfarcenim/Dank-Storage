package tfar.dankstorage.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.dankstorage.blockentity.CommonDockBlockEntity;
import tfar.dankstorage.platform.Services;

public class ModBlockEntityTypes {
    public static final BlockEntityType<? extends CommonDockBlockEntity<?>> dank_tile = BlockEntityType.Builder.of(Services.PLATFORM::blockEntity, ModBlocks.dock).build(null);

}
