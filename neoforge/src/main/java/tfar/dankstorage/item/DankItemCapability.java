package tfar.dankstorage.item;

import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankInventoryForge;

public class DankItemCapability implements ICapabilityProvider {

    private final ItemStack container;

    public DankItemCapability(ItemStack container) {
        this.container = container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, LazyOptional.of(this::lookup));
    }

    protected DankInventoryForge lookup() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        //this can be called clientside, functional storage does so for some reason
        //this should be replaced with a proper inventory at some point
        if (server != null) {
            return (DankInventoryForge) CommonUtils.getBagInventory(container, server.getLevel(Level.OVERWORLD));
        }
        else {
            return new DankInventoryForge(DankStats.zero, -1);
        }
    }
}
