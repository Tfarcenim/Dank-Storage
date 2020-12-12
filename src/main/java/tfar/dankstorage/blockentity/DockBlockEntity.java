
package tfar.dankstorage.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.TranslationTextComponent;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.container.DockContainer;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.utils.DankStats;
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

public class DockBlockEntity extends TileEntity implements INameable, INamedContainerProvider {

  public int numPlayersUsing = 0;
  protected ITextComponent customName;
  public int mode = 0;
  public int selectedSlot;
  public final IIntArray array = new IIntArray() {
    @Override
    public int get(int index) {
      return DockBlockEntity.this.handler.lockedSlots[index];
    }

    @Override
    public void set(int index, int value) {
      DockBlockEntity.this.handler.lockedSlots[index] = value;
    }

    @Override
    public int size() {
      return DockBlockEntity.this.handler.lockedSlots.length;
    }
  };

  private DankHandler handler = new DankHandler(DankStats.zero) {
    @Override
    public void onContentsChanged(int slot) {
      super.onContentsChanged(slot);
      DockBlockEntity.this.markDirty();
    }
  };

  public LazyOptional<IItemHandler> optional = LazyOptional.of(() -> handler).cast();

  public DockBlockEntity() {
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

  @Override
  public void read(BlockState state,CompoundNBT compound) {
    super.read(state,compound);
    this.mode = compound.getInt("mode");
    this.selectedSlot = compound.getInt("selectedSlot");
    if (compound.contains(Utils.INV)) {
      handler.deserializeNBT(compound.getCompound(Utils.INV));
    }
    if (compound.contains("CustomName", 8)) {
      this.setCustomName(ITextComponent.Serializer.getComponentFromJson(compound.getString("CustomName")));
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

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity p_createMenu_3_) {
    switch (getBlockState().get(DockBlock.TIER)) {
      case 1:return DockContainer.dock1s(id,inv,handler,array);
      case 2:return DockContainer.dock2s(id,inv,handler,array);
      case 3:return DockContainer.dock3s(id,inv,handler,array);
      case 4:return DockContainer.dock4s(id,inv,handler,array);
      case 5:return DockContainer.dock5s(id,inv,handler,array);
      case 6:return DockContainer.dock6s(id,inv,handler,array);
      case 7:return DockContainer.dock7s(id,inv,handler,array);
    }
    return null;
  }

  public void removeTank() {
    ItemStack stack = removeTankWithoutItemSpawn();
    ItemEntity entity = new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),stack);
    world.addEntity(entity);
  }

  public ItemStack removeTankWithoutItemSpawn() {
    int tier = getBlockState().get(DockBlock.TIER);
    CompoundNBT nbt = handler.serializeNBT();
    world.setBlockState(pos,getBlockState().with(DockBlock.TIER,0));
    handler.setSize(0);
    handler.stacklimit = 0;
    optional.invalidate();
    ItemStack stack = new ItemStack(Utils.getItemFromTier(tier));
    stack.setDisplayName(getDisplayName());
    setCustomName(null);
    stack.getOrCreateTag().put(Utils.INV,nbt);
    return stack;
  }

  public void addTank(ItemStack tank) {
    if (tank.getItem() instanceof DankItem) {
      int tier = ((DankItem)tank.getItem()).tier.ordinal();
      world.setBlockState(pos,getBlockState().with(DockBlock.TIER,tier));
      handler.stacklimit = Utils.getStackLimit(tank);
      handler.setSize(Utils.getSlotCount(tier));
      handler.deserializeNBT(tank.getOrCreateTag().getCompound(Utils.INV));
      optional = LazyOptional.of(() -> handler);
      setCustomName(tank.getDisplayName());
      tank.shrink(1);
      world.notifyNeighborsOfStateChange(pos,getBlockState().getBlock());
    }
  }

  public void upgrade(int to) {
    world.setBlockState(pos,getBlockState().with(DockBlock.TIER,to));
    handler.stacklimit = DankStats.fromInt(to).stacklimit;
    handler.setSize(Utils.getSlotCount(to));
  }
}