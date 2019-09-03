package com.tfar.dankstorage.tile;

import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.inventory.DankHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractDankStorageTile extends TileEntity {

  public int numPlayersUsing = 0;
  protected String customName;
  public int renderTick = 0;
  public boolean pickup;
  public boolean isVoid;
  public int selectedSlot;

  public DankHandler itemHandler;

  public AbstractDankStorageTile(int rows, int stacksize) {
    this.itemHandler = new DankHandler(rows * 9,stacksize) {
      @Override
      public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        AbstractDankStorageTile.this.world.addBlockEvent(AbstractDankStorageTile.this.pos, AbstractDankStorageTile.this.blockType, 1, AbstractDankStorageTile.this.numPlayersUsing);
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

  @Nullable
  @Override
  public ITextComponent getDisplayName() {
    return new TextComponentTranslation("container." + getDank().getRegistryName().toString());
  }

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

  public void openInventory(EntityPlayer player) {
    if (!player.isSpectator()) {
      if (this.numPlayersUsing < 0) {
        this.numPlayersUsing = 0;
      }

      ++this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, this.blockType, 1, this.numPlayersUsing);
      this.world.notifyNeighborsOfStateChange(this.pos, blockType,false);
      markDirty();
    }
  }

  public void closeInventory(EntityPlayer player) {
    if (!player.isSpectator() && this.blockType instanceof DankBlock) {
      --this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, blockType, 1, this.numPlayersUsing);
      this.world.notifyNeighborsOfStateChange(this.pos, this.blockType,false);
      markDirty();
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.isVoid = compound.getBoolean("void");
    this.pickup = compound.getBoolean("pickup");
    this.selectedSlot = compound.getInteger("selectedSlot");
    if (compound.hasKey("Items")) {
      itemHandler.deserializeNBT(compound.getCompoundTag("Items"));
    }
 //   if (compound.hasKey("CustomName", 8)) {
  //    this.setCustomName(compound.getString("CustomName"));
  //  }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    compound.setBoolean("void", isVoid);
    compound.setBoolean("pickup",pickup);
    compound.setInteger("selectedSlot",selectedSlot);
    compound.setTag("Items", itemHandler.serializeNBT());
 //   if (this.hasCustomName()) {
 //     compound.putString("CustomName", this.customName);
 //   }
    return compound;
  }

  @Override
  public NBTTagCompound getUpdateTag() {
    return writeToNBT(new NBTTagCompound());
  }

  @Nullable
  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    readFromNBT(pkt.getNbtCompound());
  }

  @Override
  public void markDirty() {
    super.markDirty();
    if (getWorld() != null) {
      getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
      this.world.notifyNeighborsOfStateChange(this.pos, blockType,false);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?  (T)itemHandler: super.getCapability(capability, facing);
  }



  public abstract Item getDank();

  public void setContents(NBTTagCompound nbt){
    itemHandler.deserializeNBT(nbt);
  }
}