
package tfar.dankstorage.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.container.DankContainers;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DankBlockEntity extends TileEntity implements INameable, INamedContainerProvider {

  public int numPlayersUsing = 0;
  protected ITextComponent customName;
  public int mode = 0;
  public int selectedSlot;

  private DankHandler handler = new DankHandler(0,0) {
    @Override
    public void onContentsChanged(int slot) {
      super.onContentsChanged(slot);
      DankBlockEntity.this.world.addBlockEvent(DankBlockEntity.this.pos, DankBlockEntity.this.getBlockState().getBlock(), 1, DankBlockEntity.this.numPlayersUsing);
      DankBlockEntity.this.markDirty();
    }
  };

  public LazyOptional<IItemHandler> optional = LazyOptional.of(() -> handler).cast();

  public DankBlockEntity() {
    super(DankStorage.Objects.dank_tile);
  }

  public DankHandler getHandler(){
    return handler;
  }

  public int getComparatorSignal() {
    return this.handler.calcRedstone();
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
    if (!player.isSpectator() && this.getBlockState().getBlock() instanceof DockBlock) {
      --this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
      this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
      markDirty();
    }
  }

  @Override
  public void read(BlockState state,CompoundNBT compound) {
    super.read(state,compound);
    this.mode = compound.getInt("mode");
    this.selectedSlot = compound.getInt("selectedSlot");
    if (compound.contains(Utils.INV)) {
      handler.deserializeNBT(compound.getCompound(Utils.INV));
    }
    if (compound.contains("CustomName", 8)) {
      this.setCustomName(ITextComponent.Serializer.func_240643_a_(compound.getString("CustomName")));
    }
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    super.write(tag);
    tag.putInt("mode",mode);
    tag.putInt("selectedSlot",selectedSlot);
    tag.put(Utils.INV, handler.serializeNBT());
    if (this.hasCustomName()) {
      tag.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
    }
    return tag;
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
    read(null,pkt.getNbtCompound());
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

  public void setCustomName(ITextComponent text) {
    this.customName = text;
  }

  @Override
  public ITextComponent getName() {
    return customName != null ? customName : getDefaultName();
  }

  ITextComponent getDefaultName() {
    return new TranslationTextComponent("container.dankstorage.dank_"+getBlockState().get(DockBlock.TIER));
  }

  @Override
  public ITextComponent getDisplayName() {
    return this.getName();
  }

  @Nullable
  @Override
  public ITextComponent getCustomName() {
    return customName;
  }

  public void setContents(CompoundNBT nbt){
    handler.deserializeNBT(nbt);
  }

  @Nullable
  @Override
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    switch (getBlockState().get(DockBlock.TIER)) {
      case 1:return new DankContainers.TileDankContainer1(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
      case 2:return new DankContainers.TileDankContainer2(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
      case 3:return new DankContainers.TileDankContainer3(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
      case 4:return new DankContainers.TileDankContainer4(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
      case 5:return new DankContainers.TileDankContainer5(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
      case 6:return new DankContainers.TileDankContainer6(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
      case 7:return new DankContainers.TileDankContainer7(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
    }
    return null;
  }

  public void removeTank() {
    int tier = getBlockState().get(DockBlock.TIER);
    CompoundNBT nbt = handler.serializeNBT();
    world.setBlockState(pos,getBlockState().with(DockBlock.TIER,0));
    handler.setSize(0);
    handler.stacklimit = 0;
    optional.invalidate();
    ItemStack stack = new ItemStack(Utils.getItemFromTier(tier));
    stack.getOrCreateTag().put(Utils.INV,nbt);
    ItemEntity entity = new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),stack);
    world.addEntity(entity);
  }

  public void addTank(ItemStack tank) {
    if (tank.getItem() instanceof DankItem) {
      int tier = ((DankItem)tank.getItem()).tier;
      world.setBlockState(pos,getBlockState().with(DockBlock.TIER,tier));
      handler.stacklimit = Utils.getStackLimit(tank);
      handler.setSize(Utils.getSlotCount(tier));
      handler.deserializeNBT(tank.getOrCreateTag().getCompound(Utils.INV));
      optional = LazyOptional.of(() -> handler);
      tank.shrink(1);
    }
  }

  public void upgrade(int to) {
    world.setBlockState(pos,getBlockState().with(DockBlock.TIER,to));
    handler.stacklimit = Utils.getStackLimit(to);
    handler.setSize(Utils.getSlotCount(to));
  }
}