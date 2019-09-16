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
      if (!world.isRemote && player.isSneaking()) {
        if (player.getHeldItem(hand).getItem() instanceof DankItemBlock) {
          player.openGui(DankStorage.instance, DankConstants.BAG_GUI_ID, world, 0, 0, 0);
        }
      } else {
        ItemStack bag = player.getHeldItem(hand);
        PortableDankHandler handler = Utils.getHandler(bag);
        ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
        EntityEquipmentSlot hand1 = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
        if (isSuperStacked(toPlace)){
          ItemStack unStack = toPlace.copy();
          handler.extractItem(Utils.getSelectedSlot(bag),1,false);
          handler.writeItemStack();
          unStack.setCount(1);
          player.setItemStackToSlot(hand1, unStack);
          ActionResult<ItemStack> actionResult = unStack.getItem().onItemRightClick(world, player, hand);
          ItemStack result = actionResult.getResult();
          for (int i = 0; i < handler.getSlots();i++) {
            ItemStack stack2 = handler.insertItem(i, result, false);
            if (stack2.isEmpty())break;
          }
          handler.writeItemStack();
          player.setItemStackToSlot(hand1, bag);
          return super.onItemRightClick(world, player, hand);
        }


        player.setItemStackToSlot(hand1, toPlace);
        ActionResult<ItemStack> actionResult = toPlace.getItem().onItemRightClick(world, player, hand);
        handler.setStackInSlot(Utils.getSelectedSlot(bag), actionResult.getResult());
        player.setItemStackToSlot(hand1, bag);
      }
    return super.onItemRightClick(world, player, hand);
  }

  public boolean isSuperStacked(ItemStack stack){
    return stack.getMaxStackSize() == 1 && stack.getCount() > 1;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack bag, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
    if (!Utils.construction(bag))return false;
    PortableDankHandler handler = Utils.getHandler(bag);
    ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
    EntityEquipmentSlot hand1 = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
    player.setItemStackToSlot(hand1, toPlace);
    boolean result = toPlace.getItem().itemInteractionForEntity(toPlace, player, entity, hand);
    handler.setStackInSlot(Utils.getSelectedSlot(bag),toPlace);
    player.setItemStackToSlot(hand1, bag);
    return result;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return stack.hasTagCompound() && Utils.construction(stack);
  }

  @Nonnull
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack bag = player.getHeldItem(hand);
    if (!Utils.construction(bag))
      return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

    PortableDankHandler handler = Utils.getHandler(bag);
    int selectedSlot = Utils.getSelectedSlot(bag);
    ItemStack usedStack = handler.getStackInSlot(selectedSlot);
    EntityEquipmentSlot hand1 = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
    if (isSuperStacked(usedStack)) {
      handler.extractItem(Utils.getSelectedSlot(bag),1,false);
      handler.writeItemStack();
      ItemStack unStack = usedStack.copy();
      unStack.setCount(1);
      player.setItemStackToSlot(hand1,unStack);
      EnumActionResult actionResultType = unStack.getItem().onItemUse(player,world,pos,hand,facing,hitX,hitY,hitZ);
      for (int i = 0; i < handler.getSlots();i++) {
        ItemStack stack2 = handler.insertItem(i, unStack, false);
        if (stack2.isEmpty())break;
      }
      handler.writeItemStack();
      player.setItemStackToSlot(hand1,bag);
      return actionResultType;
    }

    player.setItemStackToSlot(hand1,usedStack);
    EnumActionResult actionResultType = usedStack.getItem().onItemUse(player,world,pos,hand,facing,hitX,hitY,hitZ);
    handler.setStackInSlot(selectedSlot, usedStack);
    player.setItemStackToSlot(hand1,bag);
    return actionResultType;
  }
}
