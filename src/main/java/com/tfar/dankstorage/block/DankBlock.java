package com.tfar.dankstorage.block;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.client.Client;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.util.DankConstants;
import com.tfar.dankstorage.util.Utils;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import com.tfar.dankstorage.tile.DankTiles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DankBlock extends Block {
  public DankBlock(Material p_i48440_1_) {
    super(p_i48440_1_);
  }


  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof AbstractDankStorageTile) {
      player.openGui(DankStorage.instance, DankConstants.TILE_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
      }
    }
    return true;
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Items.AIR;
  }

  @Override
  public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    final TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof AbstractDankStorageTile && !world.isRemote){
      ItemStack dank = new ItemStack(((AbstractDankStorageTile) tile).getDank());
      NBTTagCompound nbt = ((AbstractDankStorageTile) tile).itemHandler.serializeNBT();
      nbt.setBoolean("pickup",((AbstractDankStorageTile) tile).pickup);
      nbt.setBoolean("void",((AbstractDankStorageTile) tile).isVoid);
      nbt.setInteger("selectedSlot",((AbstractDankStorageTile) tile).selectedSlot);
      dank.setTagCompound(nbt);
      EntityItem itemEntity = new EntityItem(world,pos.getX()+ .5,pos.getY() + .5,pos.getZ()+.5,dank);
      world.spawnEntity(itemEntity);
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, @Nullable EntityLivingBase entity, ItemStack stack) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof AbstractDankStorageTile && !world.isRemote && entity != null) {
      if (stack.hasTagCompound()){
        ((AbstractDankStorageTile) te).setContents(stack.getTagCompound());
        ((AbstractDankStorageTile) te).pickup = stack.getTagCompound().getBoolean("pickup");
        ((AbstractDankStorageTile) te).isVoid = stack.getTagCompound().getBoolean("void");
        ((AbstractDankStorageTile) te).selectedSlot = stack.getTagCompound().getInteger("selectedSlot");
      }
    }
  }


  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    ItemStack bag = placer.getHeldItem(hand);
    if (!Utils.construction(bag))
    return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);

    Block block = Block.getBlockFromItem(bag.getItem());
    if (block instanceof DankBlock)return block.getDefaultState();
    return block.isAir(block.getDefaultState(),null,null) ? null : block.getStateForPlacement(world,pos,facing,hitX,hitY,hitZ,meta,placer,hand);
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(World world, IBlockState state) {
    int type = Utils.getTier(this.getRegistryName());
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
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack bag, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
    //if (bag.hasTag())tooltip.add(new StringTextComponent(bag.getTagCompound().toString()));

    if (!GuiScreen.isShiftKeyDown()){
      tooltip.add(I18n.format("text.dankstorage.shift", Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName()));
    }

    if (GuiScreen.isShiftKeyDown()) {
      if (Utils.autoVoid(bag)) tooltip.add(I18n.format("text.dankstorage.disablevoid", Client.AUTO_VOID.getDisplayName()));
              else tooltip.add(I18n.format("text.dankstorage.enablevoid", Client.AUTO_VOID.getDisplayName()));
      if (Utils.autoPickup(bag)) tooltip.add(
              I18n.format("text.dankstorage.disablepickup", Client.AUTO_PICKUP.getDisplayName()));
      else tooltip.add(
              I18n.format("text.dankstorage.enablepickup", Client.AUTO_PICKUP.getDisplayName()));
      DankHandler handler = Utils.getHandler(bag);

      if (handler.isEmpty()){
        tooltip.add(I18n.format("text.dankstorage.empty"));
        return;
      }

      for (int i = 0; i < handler.getSlots(); i++) {
        ItemStack item = handler.getStackInSlot(i);
        if (item.isEmpty())continue;
          String count = Integer.toString(item.getCount());
        tooltip.add(I18n.format("text.dankstorage.formatcontaineditems",TextFormatting.AQUA+count,item.getItem().getForgeRarity(item).getColor()+item.getDisplayName()));


      }
    }
  }

  public static boolean onItemPickup(EntityItemPickupEvent event, ItemStack bag) {

    if (!bag.hasTagCompound() || !bag.getTagCompound().getBoolean("pickup")) {
      return false;
    }
    ItemStack toPickup = event.getItem().getItem();
    final boolean isVoid = Utils.autoVoid(bag);

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
        PortableDankHandler inv = Utils.getHandler(bag);
        for (int i = 0; i < inv.getSlots(); i++) {
          ItemStack stackInSlot = inv.getStackInSlot(i);
          if (stackInSlot.isEmpty()  && !isVoid) {
            inv.setStackInSlot(i, toPickup.copy());
            toPickup.setCount(toPickup.getCount() - inv.stacklimit);
          } else if (canAddItemToSlot(inv,stackInSlot, toPickup,true)) {
            int fill = inv.stacklimit - stackInSlot.getCount();
            if (fill > toPickup.getCount()) {
              stackInSlot.setCount(stackInSlot.getCount() + toPickup.getCount());
            } else {
              stackInSlot.setCount(inv.stacklimit);
            }
            if (!isVoid)
            toPickup.splitStack(fill);
            else if (toPickup.isItemEqual(stackInSlot) && ItemStack.areItemStackTagsEqual(stackInSlot, toPickup))toPickup.setCount(0);
          }
          if (toPickup.isEmpty()) {
            break;
          }
        }
        if (toPickup.getCount() != count) {
          bag.setAnimationsToGo(5);
          EntityPlayer player = event.getEntityPlayer();
          player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
          inv.writeItemStack();
        }
      }
    }
    return toPickup.isEmpty();
  }

  public static boolean canAddItemToSlot(PortableDankHandler handler, ItemStack stackInSlot, ItemStack pickup, boolean stackSizeMatters) {
    boolean isEmpty = stackInSlot.isEmpty();

    if (!isEmpty && pickup.isItemEqual(stackInSlot) && ItemStack.areItemStackTagsEqual(stackInSlot, pickup)) {
      return stackInSlot.getCount() + (stackSizeMatters ? 0 : pickup.getCount()) <= handler.stacklimit;
    }

    return isEmpty;
  }
}
