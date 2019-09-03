package com.tfar.dankstorage.block;

import com.tfar.dankstorage.capability.CapabilityDankStorageProvider;
import com.tfar.dankstorage.container.PortableDankProvider;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
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
        int type = Utils.getTier(player.getHeldItem(hand));
        NetworkHooks.openGui((EntityPlayerMP) player, new PortableDankProvider(type), data -> data.writeItemStack(player.getHeldItem(hand)));
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
  public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    if (!Utils.construction(ctx.getItem()))
      return super.onItemUse(ctx);


    ItemStack bag = ctx.item;
    PortableDankHandler handler = Utils.getHandler(bag);
    int selectedSlot = Utils.getSelectedSlot(bag);
    ctx.item = handler.getStackInSlot(selectedSlot);
    EnumActionResult actionResultType = ctx.item.onItemUse(ctx);
    handler.setStackInSlot(selectedSlot, ctx.item);
    return actionResultType;
  }
}
