package com.tfar.dankstorage.block;

import com.google.common.collect.Sets;
import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.container.PortableDankProvider;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.CMessageTogglePickup;
import com.tfar.dankstorage.network.CMessageToggleUseType;
import com.tfar.dankstorage.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

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
    return new ICapabilityProvider() {
      @Nonnull
      @Override
      public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? LazyOptional.of(() -> Utils.getHandler(stack)).cast() : LazyOptional.empty();
      }
    };
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

  @Override
  public int getUseDuration(ItemStack bag) {
    if (!Utils.isConstruction(bag))return 0;
    ItemStack stack = Utils.getItemStackInSelectedSlot(bag);
    return stack.getItem().getUseDuration(stack);
  }

  @Override
  public Set<ToolType> getToolTypes(ItemStack bag) {
    if (!Utils.isConstruction(bag))return Sets.newHashSet();
    ItemStack tool = Utils.getItemStackInSelectedSlot(bag);
    return tool.getItem().getToolTypes(tool);
  }

  @Override
  public int getHarvestLevel(ItemStack bag, ToolType p_getHarvestLevel_2_, @Nullable PlayerEntity p_getHarvestLevel_3_, @Nullable BlockState p_getHarvestLevel_4_) {
    if (!Utils.isConstruction(bag))return -1;

    ItemStack tool = Utils.getItemStackInSelectedSlot(bag);
    return tool.getItem().getHarvestLevel(tool, p_getHarvestLevel_2_, p_getHarvestLevel_3_, p_getHarvestLevel_4_);
  }

  @Override
  public float getDestroySpeed(ItemStack bag, BlockState p_150893_2_) {
    if (!Utils.isConstruction(bag))return 1;
    ItemStack tool = Utils.getItemStackInSelectedSlot(bag);
    return tool.getItem().getDestroySpeed(tool, p_150893_2_);
  }

  @Override
  public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
    return super.onBlockDestroyed(p_179218_1_, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack bag = player.getHeldItem(hand);
    if (!world.isRemote){

      if (Utils.getUseType(bag) == CMessageToggleUseType.UseType.bag) {
        int type = Utils.getTier(player.getHeldItem(hand));
        NetworkHooks.openGui((ServerPlayerEntity) player, new PortableDankProvider(type));
        return super.onItemRightClick(world,player,hand);
      } else {
        ItemStack toPlace = Utils.getItemStackInSelectedSlot(bag);
        EquipmentSlotType hand1 = hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
        //handle empty
        if (toPlace.isEmpty()){
          return ActionResult.func_226250_c_(bag);
        }

        //handle food
        if (toPlace.getItem().isFood()) {
          if (player.canEat(false)) {
            player.setActiveHand(hand);
            return ActionResult.func_226250_c_(bag);
          }
        }
        //handle potion
        else if (toPlace.getItem() instanceof PotionItem){
          player.setActiveHand(hand);
          return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
        }
        //todo support other items?
        else {
          ItemStack newBag = bag.copy();
          player.setItemStackToSlot(hand1, toPlace);
          ActionResult<ItemStack> actionResult = toPlace.getItem().onItemRightClick(world, player, hand);
          PortableDankHandler handler = Utils.getHandler(newBag);
          handler.setStackInSlot(Utils.getSelectedSlot(newBag), actionResult.getResult());
          player.setItemStackToSlot(hand1, newBag);
        }
        }
      }
    return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack bag, PlayerEntity player, LivingEntity entity, Hand hand) {
    if (!Utils.isConstruction(bag))return false;
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
    return stack.hasTag() && Utils.getMode(stack) != CMessageTogglePickup.Mode.NORMAL;
  }

  @Nonnull
  @Override
  public UseAction getUseAction(ItemStack stack) {
    if (!Utils.isConstruction(stack))return UseAction.NONE;
    ItemStack internal = Utils.getItemStackInSelectedSlot(stack);
    return internal.getItem().getUseAction(stack);
  }

  @Override
  public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
    return true;
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return !oldStack.equals(newStack);
  }

  //called for stuff like food and potions
  @Nonnull
  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
    if (!Utils.isConstruction(stack))return stack;

    ItemStack internal = Utils.getItemStackInSelectedSlot(stack);

    if (internal.getItem().isFood()){
     ItemStack food = entity.onFoodEaten(world, internal);
     PortableDankHandler handler = Utils.getHandler(stack);
     handler.setStackInSlot(Utils.getSelectedSlot(stack), food);
     return stack;
    }

    if (internal.getItem() instanceof PotionItem){
      ItemStack potion = internal.onItemUseFinish(world,entity);
      PortableDankHandler handler = Utils.getHandler(stack);
      handler.setStackInSlot(Utils.getSelectedSlot(stack), potion);
      return stack;
    }

    return super.onItemUseFinish(stack, world, entity);
  }

  @Override
  public void onUsingTick(ItemStack stack, LivingEntity living, int count) {
  }

  public int getGlintColor(ItemStack stack){
    CMessageTogglePickup.Mode mode = Utils.getMode(stack);
    switch (mode){
      case NORMAL:default:return 0xffffffff;
      case PICKUP_ALL:return 0xff00ff00;
      case FILTERED_PICKUP:return 0xffffff00;
      case VOID_PICKUP:return 0xffff0000;
    }
  }

  @Nullable
  @Override
  public CompoundNBT getShareTag(ItemStack stack) {
    if(DankStorage.ServerConfig.useShareTag.get()){
      //Double check it is actually a stack of the correct type
      CompoundNBT nbt = stack.getTag();
      if (nbt == null || !nbt.contains(Utils.INV, Constants.NBT.TAG_LIST)) {
        //If we don't have any NBT or already don't have the key just return the NBT as is
        return nbt;
      }
      //Don't sync the list of consumed stacks to the client to make sure it doesn't overflow the packet
      return Utils.copyNBTSkipKey(nbt, Utils.INV);
    }
    return super.getShareTag(stack);
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext ctx) {
    ItemStack bag = ctx.getItem();
    CMessageToggleUseType.UseType useType = Utils.getUseType(bag);

    if (useType == CMessageToggleUseType.UseType.chest)
      return super.onItemUse(ctx);

    if(useType == CMessageToggleUseType.UseType.bag){
      return ActionResultType.PASS;
    }

    PortableDankHandler handler = Utils.getHandler(bag);
    int selectedSlot = Utils.getSelectedSlot(bag);

    ItemStack toPlace = handler.getStackInSlot(selectedSlot).copy();
    if (toPlace.getCount() == 1 && handler.isLocked(selectedSlot))
      return ActionResultType.PASS;

    ItemUseContext ctx2 = new ItemUseContextExt(ctx.getWorld(),ctx.getPlayer(),ctx.getHand(),toPlace,ctx.rayTraceResult);
    ActionResultType actionResultType = toPlace.getItem().onItemUse(ctx2);//ctx2.getItem().onItemUse(ctx);
    handler.setStackInSlot(selectedSlot, ctx2.getItem());
    return actionResultType;
  }

  public static class ItemUseContextExt extends ItemUseContext {
    protected ItemUseContextExt(World p_i50034_1_, @Nullable PlayerEntity p_i50034_2_, Hand p_i50034_3_, ItemStack p_i50034_4_, BlockRayTraceResult p_i50034_5_) {
      super(p_i50034_1_, p_i50034_2_, p_i50034_3_, p_i50034_4_, p_i50034_5_);
    }
  }
}
