package com.tfar.dankstorage.tile;

import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.inventory.DankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractDankStorageTile extends TileEntity implements INameable, INamedContainerProvider {

  public int numPlayersUsing = 0;
  protected String customName;
  public int renderTick = 0;
  public int mode = 0;
  public int selectedSlot;

  public DankHandler itemHandler;
  public LazyOptional<IItemHandler> optional = LazyOptional.of(() -> itemHandler).cast();

  public AbstractDankStorageTile(TileEntityType<?> tile, int rows, int stacksize) {
    super(tile);
    this.itemHandler = new DankHandler(rows * 9,stacksize) {
      @Override
      public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        AbstractDankStorageTile.this.world.addBlockEvent(AbstractDankStorageTile.this.pos, AbstractDankStorageTile.this.getBlockState().getBlock(), 1, AbstractDankStorageTile.this.numPlayersUsing);
        AbstractDankStorageTile.this.markDirty();
      }
    };
  }

  public int getRenderTick() {
    return renderTick;
  }

  public int getComparatorSignal() {
    return this.itemHandler.calcRedstone();
  }

  /*@Override
  public void tick() {

  }*/

  @Override
  public boolean receiveClientEvent(int id, int type) {
    if (id == 1) {
      this.numPlayersUsing = type;
      this.markDirty();
      return true;
    } else {
      return super.receiveClientEvent(id, type);
    }
  }

  public void openInventory(PlayerEntity player) {
    if (!player.isSpectator()) {
      if (this.numPlayersUsing < 0) {
        this.numPlayersUsing = 0;
      }

      ++this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
      this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
      markDirty();
    }
  }

  public void closeInventory(PlayerEntity player) {
    if (!player.isSpectator() && this.getBlockState().getBlock() instanceof DankBlock) {
      --this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
      this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
      markDirty();
    }
  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    this.mode = compound.getInt("mode");
    this.selectedSlot = compound.getInt("selectedSlot");
    if (compound.contains("Items")) {
      itemHandler.deserializeNBT(compound.getCompound("Items"));
    }
    if (compound.contains("CustomName", 8)) {
      this.setCustomName(compound.getString("CustomName"));
    }
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT compound) {
    super.write(compound);
    compound.putInt("mode",mode);
    compound.putInt("selectedSlot",selectedSlot);
    compound.put("Items", itemHandler.serializeNBT());
    if (this.hasCustomName()) {
      compound.putString("CustomName", this.customName);
    }
    return compound;
  }

  @Nonnull
  @Override
  public CompoundNBT getUpdateTag() {
    return write(new CompoundNBT());
  }

  @Nullable
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    read(pkt.getNbtCompound());
  }

  @Override
  public void markDirty() {
    super.markDirty();
    if (getWorld() != null) {
      getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
      this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
    }
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
    return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? optional.cast() : super.getCapability(capability, facing);
  }

  @Override
  public boolean hasCustomName() {
    return this.customName != null && !this.customName.isEmpty();
  }

  public void setCustomName(String p_190575_1_) {
    this.customName = p_190575_1_;
  }

  @Override
  public ITextComponent getDisplayName() {
    return (this.hasCustomName() ? this.getName() : this.getName());
  }

  public abstract Item getDank();

  public void setContents(CompoundNBT nbt){
    itemHandler.deserializeNBT(nbt);
  }
}