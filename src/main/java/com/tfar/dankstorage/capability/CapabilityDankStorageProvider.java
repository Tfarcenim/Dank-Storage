package com.tfar.dankstorage.capability;

import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityDankStorageProvider implements ICapabilityProvider {

  private PortableDankHandler handler;

  LazyOptional<CapabilityItemHandler> itemHandlerLazyOptional;

  public CapabilityDankStorageProvider(ItemStack stack) {

    this.handler = new PortableDankHandler(Utils.getTier(stack),Utils.getStackLimit(stack),stack,false) {
      @Override
      public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        stack.setTag((CompoundNBT) CapabilityDankStorage.DANK_STORAGE_CAPABILITY.writeNBT(this, null));
      }
    };
    this.itemHandlerLazyOptional = LazyOptional.of(() -> handler).cast();
    if (stack.hasTag())
      CapabilityDankStorage.DANK_STORAGE_CAPABILITY.readNBT(this.handler, null, stack.getTag());
  }

  /**
   * Retrieves the Optional handler for the capability requested on the specific side.
   * The return value <strong>CAN</strong> be the same for multiple faces.
   * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
   * be notified if the requested capability get lost.
   *
   * @param cap
   * @param side
   * @return The requested an optional holding the requested capability.
   */
  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      return LazyOptional.of(() -> handler).cast();
    else if (cap == CapabilityDankStorage.DANK_STORAGE_CAPABILITY)
      return LazyOptional.of(() -> CapabilityDankStorage.DANK_STORAGE_CAPABILITY).cast();
    return LazyOptional.empty();
  }
}
