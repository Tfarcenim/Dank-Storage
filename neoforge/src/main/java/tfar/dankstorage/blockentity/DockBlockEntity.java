
package tfar.dankstorage.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.world.DankInventoryForge;


public class DockBlockEntity extends CommonDockBlockEntity<DankInventoryForge> {

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }


}