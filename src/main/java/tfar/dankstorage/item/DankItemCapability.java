package tfar.dankstorage.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

public class DankItemCapability implements ICapabilityProvider {

    private final ItemStack container;

    public DankItemCapability(ItemStack container) {
        this.container = container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, LazyOptional.of(this::lookup));
    }

    protected DankInventory lookup() {
        return Utils.getInventory(container, DankStorage.instance.server.getLevel(Level.OVERWORLD));
    }
}
