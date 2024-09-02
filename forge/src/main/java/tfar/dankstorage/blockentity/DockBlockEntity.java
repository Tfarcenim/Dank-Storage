
package tfar.dankstorage.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.world.DankInventoryForge;


public class DockBlockEntity extends CommonDockBlockEntity<DankInventoryForge> {

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    //item api

    //do not cache
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? LazyOptional.of(this::getInventory).cast() : super.getCapability(cap, side);
    }
}