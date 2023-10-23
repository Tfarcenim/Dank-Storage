
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
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.menu.DockMenu;
import tfar.dankstorage.init.ModBlockEntityTypes;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.CDankSavedData;
import tfar.dankstorage.world.DankInventoryForge;


public class DockBlockEntity extends CommonDockBlockEntity implements MenuProvider {

    public DockBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityTypes.dank_tile, blockPos, blockState);
    }

    public static final DankInventoryForge DUMMY = new DankInventoryForge(DankStats.zero, Utils.INVALID);

    public DankInventoryForge getInventory() {
        if (settings != null && settings.contains(Utils.FREQ)) {
            int frequency = settings.getInt(Utils.FREQ);
            CDankSavedData savedData = DankStorage.getData(frequency,level.getServer());
            DankInventoryForge dankInventoryForge = (DankInventoryForge) savedData.createInventory(frequency);

            if (!dankInventoryForge.valid()) {
                savedData.setStats(DankStats.values()[getBlockState().getValue(CDockBlock.TIER)],frequency);
                dankInventoryForge = (DankInventoryForge) savedData.createInventory(frequency);
            }

            return dankInventoryForge;
        }
        return DUMMY;
    }

    public int getComparatorSignal() {
        return this.getInventory().calcRedstone();
    }

    @Nullable
    @Override
    public DockMenu createMenu(int syncId, Inventory inventory, Player player) {

        int tier = getBlockState().getValue(CDockBlock.TIER);

        DankInventoryForge dankInventoryForge = getInventory();

        DankStats defaults = DankStats.values()[tier];
        if (defaults != dankInventoryForge.dankStats) {
            if (defaults.ordinal() < dankInventoryForge.dankStats.ordinal()) {
                Utils.warn(player, defaults, dankInventoryForge.dankStats);
                return null;
            }
            dankInventoryForge.upgradeTo(defaults);
        }

        return switch (getBlockState().getValue(CDockBlock.TIER)) {
            case 1 -> DockMenu.t1s(syncId, inventory, dankInventoryForge, this);
            case 2 -> DockMenu.t2s(syncId, inventory, dankInventoryForge, this);
            case 3 -> DockMenu.t3s(syncId, inventory, dankInventoryForge, this);
            case 4 -> DockMenu.t4s(syncId, inventory, dankInventoryForge, this);
            case 5 -> DockMenu.t5s(syncId, inventory, dankInventoryForge, this);
            case 6 -> DockMenu.t6s(syncId, inventory, dankInventoryForge, this);
            case 7 -> DockMenu.t7s(syncId, inventory, dankInventoryForge, this);
            default -> null;
        };
    }



    public void upgradeTo(DankStats stats) {
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CDockBlock.TIER, stats.ordinal()));
        DankInventoryForge dankInventoryForge = getInventory();
        dankInventoryForge.upgradeTo(stats);
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