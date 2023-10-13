
package tfar.dankstorage.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.DankStorageForge;
import tfar.dankstorage.block.CommonDockBlock;
import tfar.dankstorage.container.DockMenu;
import tfar.dankstorage.init.ModBlockEntityTypes;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.world.DankSavedData;


public class DockBlockEntity extends CommonDockBlockEntity implements MenuProvider {

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityTypes.dank_tile, blockPos, blockState);
    }

    public static final DankInventory DUMMY = new DankInventory(DankStats.zero, Utils.INVALID);

    public DankInventory getInventory() {
        if (settings != null && settings.contains(Utils.FREQ)) {
            int frequency = settings.getInt(Utils.FREQ);
            DankSavedData savedData = DankStorageForge.getData(frequency,level.getServer());
            DankInventory dankInventory = savedData.createInventory(frequency);

            if (!dankInventory.valid()) {
                savedData.setStats(DankStats.values()[getBlockState().getValue(CommonDockBlock.TIER)],frequency);
                dankInventory = savedData.createInventory(frequency);
            }

            return dankInventory;
        }
        return DUMMY;
    }

    public int getComparatorSignal() {
        return this.getInventory().calcRedstone();
    }

    @Nullable
    @Override
    public DockMenu createMenu(int syncId, Inventory inventory, Player player) {

        int tier = getBlockState().getValue(CommonDockBlock.TIER);

        DankInventory dankInventory = getInventory();

        DankStats defaults = DankStats.values()[tier];
        if (defaults != dankInventory.dankStats) {
            if (defaults.ordinal() < dankInventory.dankStats.ordinal()) {
                Utils.warn(player, defaults, dankInventory.dankStats);
                return null;
            }
            dankInventory.upgradeTo(defaults);
        }

        return switch (getBlockState().getValue(CommonDockBlock.TIER)) {
            case 1 -> DockMenu.t1s(syncId, inventory, dankInventory, this);
            case 2 -> DockMenu.t2s(syncId, inventory, dankInventory, this);
            case 3 -> DockMenu.t3s(syncId, inventory, dankInventory, this);
            case 4 -> DockMenu.t4s(syncId, inventory, dankInventory, this);
            case 5 -> DockMenu.t5s(syncId, inventory, dankInventory, this);
            case 6 -> DockMenu.t6s(syncId, inventory, dankInventory, this);
            case 7 -> DockMenu.t7s(syncId, inventory, dankInventory, this);
            default -> null;
        };
    }



    public void upgradeTo(DankStats stats) {
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CommonDockBlock.TIER, stats.ordinal()));
        DankInventory dankInventory = getInventory();
        dankInventory.upgradeTo(stats);
    }
    //item api

    //do not cache
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? LazyOptional.of(this::getInventory).cast() : super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }
}