package com.tfar.dankstorage.block;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.capability.CapabilityDankStorageProvider;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.util.DankConstants;
import com.tfar.dankstorage.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public class DankItemBlock extends ItemBlock {
  public DankItemBlock(Block p_i48527_1_) {
    super(p_i48527_1_);
  }

  @Override
  public ICapabilityProvider initCapabilities(final ItemStack stack, final NBTTagCompound nbt) {
    return new CapabilityDankStorageProvider(stack);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (!world.isRemote)
      if (player.isSneaking()) {
        if (player.getHeldItem(hand).getItem() instanceof DankItemBlock) {
          player.openGui(DankStorage.instance, DankConstants.BAG_GUI_ID, world, 0, 0, 0);
        }
      } else {
        ItemStack bag = player.getHeldItem(hand);
        PortableDankHandler handler = Utils.getHandler(bag);
        ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, toPlace);
        ActionResult<ItemStack> actionResultType = toPlace.getItem().onItemRightClick(world, player, hand);
        handler.setStackInSlot(Utils.getSelectedSlot(bag), actionResultType.getResult());
        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, bag);
      }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack bag, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
    if (!Utils.construction(bag))return false;
    PortableDankHandler handler = Utils.getHandler(bag);
    ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, toPlace);
    boolean result = toPlace.getItem().itemInteractionForEntity(toPlace, player, entity, hand);
    handler.setStackInSlot(Utils.getSelectedSlot(bag),toPlace);
    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, bag);
    return result;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return stack.hasTagCompound() && Utils.construction(stack);
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack bag = player.getHeldItem(hand);
    if (!Utils.construction(bag))
      return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

    PortableDankHandler handler = Utils.getHandler(bag);
    int selectedSlot = Utils.getSelectedSlot(bag);
    ItemStack stack = handler.getStackInSlot(selectedSlot);
    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,stack);
    EnumActionResult actionResultType = stack.getItem().onItemUse(player,world,pos,hand,facing,hitX,hitY,hitZ);
    handler.setStackInSlot(selectedSlot, stack);
    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,bag);
    return actionResultType;
  }
}
