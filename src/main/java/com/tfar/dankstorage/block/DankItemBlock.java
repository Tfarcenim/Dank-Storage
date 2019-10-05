package com.tfar.dankstorage.block;

import com.tfar.dankstorage.capability.CapabilityDankStorageProvider;
import com.tfar.dankstorage.container.PortableDankProvider;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.CMessageTogglePickup;
import com.tfar.dankstorage.network.CMessageTogglePlacement;
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
import net.minecraftforge.common.ForgeHooks;
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

  @Override
  public int getUseDuration(ItemStack bag) {
    if (!Utils.isConstruction(bag))return 0;
    ItemStack stack = Utils.getItemStackInSelectedSlot(bag);
    return stack.getItem().getUseDuration(stack);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack bag = player.getHeldItem(hand);
    if (!world.isRemote)
      if (Utils.getUseType(bag) == CMessageTogglePlacement.UseType.bag) {
        int type = Utils.getTier(player.getHeldItem(hand));
        NetworkHooks.openGui((ServerPlayerEntity) player, new PortableDankProvider(type), data -> data.writeItemStack(player.getHeldItem(hand)));
        return super.onItemRightClick(world,player,hand);
      } else {
        ItemStack toPlace = Utils.getItemStackInSelectedSlot(bag);
        EquipmentSlotType hand1 = hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
        //handle food
        if (toPlace.getItem().isFood()) {
          if (player.canEat(false)) {
            player.setActiveHand(hand);
            return ActionResult.newResult(ActionResultType.PASS, bag);
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
          //stack overflow issues
          if (toPlace.getItem() instanceof DankItemBlock) return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
          player.setItemStackToSlot(hand1, toPlace);
          ActionResult<ItemStack> actionResult = toPlace.getItem().onItemRightClick(world, player, hand);
          PortableDankHandler handler = Utils.getHandler(newBag,true);
          handler.setStackInSlot(Utils.getSelectedSlot(newBag), actionResult.getResult());
          player.setItemStackToSlot(hand1, newBag);
        }
      }
    return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack bag, PlayerEntity player, LivingEntity entity, Hand hand) {
    if (!Utils.isConstruction(bag))return false;
    PortableDankHandler handler = Utils.getHandler(bag,false);
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
     PortableDankHandler handler = Utils.getHandler(stack,false);
     handler.setStackInSlot(Utils.getSelectedSlot(stack), food);
     return stack;
    }

    if (internal.getItem() instanceof PotionItem){
      ItemStack potion = internal.onItemUseFinish(world,entity);
      PortableDankHandler handler = Utils.getHandler(stack,false);
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

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext ctx) {
    ItemStack bag = ctx.getItem();
    CMessageTogglePlacement.UseType useType = Utils.getUseType(bag);

    if (useType == CMessageTogglePlacement.UseType.chest)
      return super.onItemUse(ctx);

    if(useType == CMessageTogglePlacement.UseType.bag){
      return ActionResultType.PASS;
    }

    PortableDankHandler handler = Utils.getHandler(bag,false);
    int selectedSlot = Utils.getSelectedSlot(bag);
    ItemUseContext ctx2 = new ItemUseContextExt(ctx.getWorld(),ctx.getPlayer(),ctx.getHand(),handler.getStackInSlot(selectedSlot),ctx.rayTraceResult);
    ActionResultType actionResultType = ForgeHooks.onPlaceItemIntoWorld(ctx2);//ctx2.getItem().onItemUse(ctx);
    handler.setStackInSlot(selectedSlot, ctx2.getItem());
    return actionResultType;
  }
}
