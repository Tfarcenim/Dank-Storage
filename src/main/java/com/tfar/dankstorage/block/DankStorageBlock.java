package com.tfar.dankstorage.block;

import com.tfar.dankstorage.container.AbstractDankContainer;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.NetworkUtils;
import com.tfar.dankstorage.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class DankStorageBlock extends Block {
  public DankStorageBlock(Properties p_i48440_1_) {
    super(p_i48440_1_);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public boolean onBlockActivated(BlockState p_220051_1_, World world, BlockPos pos, PlayerEntity player, Hand p_220051_5_, BlockRayTraceResult p_220051_6_) {
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());

      } else throw new IllegalStateException("Our named container provider is missing!");
    }
    return true;
  }

  @Override
  public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    final TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof AbstractDankStorageTile && !world.isRemote){
      ItemStack dank = new ItemStack(((AbstractDankStorageTile) tile).getDank());
      CompoundNBT nbt = ((AbstractDankStorageTile) tile).itemHandler.serializeNBT();
      dank.setTag(nbt);
      ItemEntity itemEntity = new ItemEntity(world,pos.getX()+ .5,pos.getY() + .5,pos.getZ()+.5,dank);
      world.addEntity(itemEntity);
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof AbstractDankStorageTile && !world.isRemote && entity != null) {
      if (stack.hasTag()){
        ((AbstractDankStorageTile) te).setContents(stack.getTag());
      }
    }
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    int type = Integer.parseInt(state.getBlock().getRegistryName().getPath().substring(5));
    switch (type) {
      case 1:
      default:
        return new DankTiles.DankStorageTile1();
      case 2:
        return new DankTiles.DankStorageTile2();
      case 3:
        return new DankTiles.DankStorageTile3();
      case 4:
        return new DankTiles.DankStorageTile4();
      case 5:
        return new DankTiles.DankStorageTile5();
      case 6:
        return new DankTiles.DankStorageTile6();
      case 7:
        return new DankTiles.DankStorageTile7();
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
    //if (stack.hasTag())tooltip.add(new StringTextComponent(stack.getTag().toString()));
    if (stack.hasTag())System.out.println(stack.getTag().toString());
    if (NetworkUtils.autoPickup(stack))tooltip.add(
            new TranslationTextComponent("dankstorage.pickup.enabled"));
    if (NetworkUtils.autoVoid(stack))tooltip.add(
            new TranslationTextComponent("dankstorage.void.enabled"));
  }

  public static boolean onItemPickup(EntityItemPickupEvent event, ItemStack bag) {

    if (!bag.getOrCreateTag().getBoolean("pickup")) {
      return false;
    }
    ItemStack toPickup = event.getItem().getItem();
    final boolean Void = NetworkUtils.autoVoid(bag);

    if (true) {
      if (false) {
     //   toPickup.setCount(0);
     //   bag.setAnimationsToGo(5);
    //    PlayerEntity player = event.getEntityPlayer();
      //  player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (MathHelper.RANDOM.nextFloat() - MathHelper.RANDOM.nextFloat()) * 0.7F + 1.0F);
        return true;
        //todo watch for dupes
      } else if (true) {
        int count = toPickup.getCount();
        PortableDankHandler inv = getHandler(bag);
        for (int i = 0; i < inv.getSlots(); i++) {
          ItemStack stackInSlot = inv.getStackInSlot(i);
          if (stackInSlot.isEmpty()  && !Void) {
            inv.setStackInSlot(i, toPickup.copy());
            toPickup.setCount(0);
          } else if (canAddItemToSlot(inv,stackInSlot, toPickup,true)) {
            int fill = inv.stacklimit - stackInSlot.getCount();
            if (fill > toPickup.getCount()) {
              stackInSlot.setCount(stackInSlot.getCount() + toPickup.getCount());
            } else {
              stackInSlot.setCount(inv.stacklimit);
            }
            if (!Void)
            toPickup.split(fill);
            else if (toPickup.isItemEqual(stackInSlot) && ItemStack.areItemStackTagsEqual(stackInSlot, toPickup))toPickup.setCount(0);
          }
          if (toPickup.isEmpty()) {
            break;
          }
        }
        if (toPickup.getCount() != count) {
          bag.setAnimationsToGo(5);
          PlayerEntity player = event.getPlayer();
          player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
          inv.writeItemStack();
        }
      }
    }
    return toPickup.isEmpty();
  }

//} else if (ItemHandlerHelper.canItemStacksStack(eventItem, slot)) {


public static PortableDankHandler getHandler(ItemStack bag){
    int type = Integer.parseInt(bag.getItem().getRegistryName().getPath().substring(5));
    switch (type){
      case 1:default:
        return new PortableDankHandler(9,256,bag);
      case 2:
        return new PortableDankHandler(18,1024,bag);
      case 3:
        return new PortableDankHandler(27,4096,bag);
      case 4:
        return new PortableDankHandler(36,16384,bag);
      case 5:
        return new PortableDankHandler(45,65536,bag);
      case 6:
        return new PortableDankHandler(54, 262144, bag);
      case 7:
        return new PortableDankHandler(81,Integer.MAX_VALUE,bag);
    }
  }

  public static boolean canAddItemToSlot(PortableDankHandler handler, ItemStack stackInSlot, ItemStack pickup, boolean stackSizeMatters) {
    boolean isEmpty = stackInSlot.isEmpty();

    if (!isEmpty && pickup.isItemEqual(stackInSlot) && ItemStack.areItemStackTagsEqual(stackInSlot, pickup)) {
      return stackInSlot.getCount() + (stackSizeMatters ? 0 : pickup.getCount()) <= handler.stacklimit;
    }

    return isEmpty;
  }
}
