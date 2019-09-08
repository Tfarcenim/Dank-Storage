package com.tfar.dankstorage.block;

import com.tfar.dankstorage.capability.CapabilityDankStorageProvider;
import com.tfar.dankstorage.container.PortableDankProvider;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class DankItemBlock extends BlockItem {
  public DankItemBlock(Block p_i48527_1_, Properties p_i48527_2_) {
    super(p_i48527_1_, p_i48527_2_);
  }

  public static final Rarity GRAY = Rarity.create("dark_gray", TextFormatting.GRAY);
  public static final Rarity RED = Rarity.create("red", TextFormatting.RED);
  public static final Rarity GOLD = Rarity.create("gold", TextFormatting.GOLD);
  public static final Rarity GREEN = Rarity.create("green", TextFormatting.GREEN);
  public static final Rarity BLUE = Rarity.create("blue", TextFormatting.AQUA);
  public static final Rarity PURPLE = Rarity.create("purple", TextFormatting.DARK_PURPLE);
  public static final Rarity WHITE = Rarity.create("white", TextFormatting.WHITE);

  @Override
  public ICapabilityProvider initCapabilities(final ItemStack stack, final CompoundNBT nbt) {
    return new CapabilityDankStorageProvider(stack);
  }

  @Nonnull
  @Override
  public Rarity getRarity(ItemStack stack) {
    int type = Utils.getTier(stack);
    switch (type) {
      case 1:
        return GRAY;
      case 2:
        return RED;
      case 3:
        return GOLD;
      case 4:
        return GREEN;
      case 5:
        return BLUE;
      case 6:
        return PURPLE;
      case 7:
        return WHITE;
    }
    return super.getRarity(stack);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    if (!world.isRemote)
      if (player.isSneaking()) {
        int type = Utils.getTier(player.getHeldItem(hand));
        NetworkHooks.openGui((ServerPlayerEntity) player, new PortableDankProvider(type), data -> data.writeItemStack(player.getHeldItem(hand)));
      } else {
        ItemStack bag = player.getHeldItem(hand);
        PortableDankHandler handler = Utils.getHandler(bag);
        ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
        EquipmentSlotType hand1 = hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
        player.setItemStackToSlot(hand1, toPlace);
        ActionResult<ItemStack> actionResultType = toPlace.getItem().onItemRightClick(world, player, hand);
        handler.setStackInSlot(Utils.getSelectedSlot(bag), actionResultType.getResult());
        player.setItemStackToSlot(hand1, bag);
      }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack bag, PlayerEntity player, LivingEntity entity, Hand hand) {
    if (!Utils.construction(bag))return false;
    PortableDankHandler handler = Utils.getHandler(bag);
    ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
    EquipmentSlotType hand1 = hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
    player.setItemStackToSlot(hand1, toPlace);
    boolean result = toPlace.getItem().itemInteractionForEntity(toPlace, player, entity, hand);
    handler.setStackInSlot(Utils.getSelectedSlot(bag),toPlace);
    player.setItemStackToSlot(hand1, bag);
    return result;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return stack.hasTag() && Utils.construction(stack);
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext ctx) {
    if (!Utils.construction(ctx.getItem()))
      return super.onItemUse(ctx);


    ItemStack bag = ctx.getItem();
    PortableDankHandler handler = Utils.getHandler(bag);
    int selectedSlot = Utils.getSelectedSlot(bag);
    ItemUseContext ctx2 = new ItemUseContextExt(ctx.getWorld(),ctx.getPlayer(),ctx.getHand(),handler.getStackInSlot(selectedSlot),ctx.rayTraceResult);
    ActionResultType actionResultType = ctx2.getItem().onItemUse(ctx);
    handler.setStackInSlot(selectedSlot, ctx2.getItem());
    return actionResultType;
  }
}
