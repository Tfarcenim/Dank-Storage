package tfar.dankstorage.item;

import com.google.common.collect.Sets;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.container.PortableDankProvider;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.network.CMessageToggleUseType;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DankItem extends Item {
  public final DankStats tier;

  public DankItem(Properties p_i48527_2_, DankStats stats) {
    super( p_i48527_2_);
    this.tier = stats;
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
    switch (tier) {
      case one:
        return GRAY;
      case two:
        return RED;
      case three:
        return GOLD;
      case four:
        return GREEN;
      case five:
        return BLUE;
      case six:
        return PURPLE;
      case seven:
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
    return tool.getHarvestLevel(p_getHarvestLevel_2_, p_getHarvestLevel_3_, p_getHarvestLevel_4_);
  }

  @Override
  public float getDestroySpeed(ItemStack bag, BlockState p_150893_2_) {
    if (!Utils.isConstruction(bag))return 1;
    ItemStack tool = Utils.getItemStackInSelectedSlot(bag);
    return tool.getItem().getDestroySpeed(tool, p_150893_2_);
  }

  //this is used to damage tools and stuff, we use it here to damage the internal item instead
  @Override
  public boolean onBlockDestroyed(ItemStack s, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
    if (!Utils.isConstruction(s))return super.onBlockDestroyed(s, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);

    ItemStack tool = Utils.getItemStackInSelectedSlot(s);

    return tool.getItem().onBlockDestroyed(tool, p_179218_2_, p_179218_3_, p_179218_4_, p_179218_5_);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack bag = player.getHeldItem(hand);
    if (!world.isRemote){

      if (Utils.getUseType(bag) == CMessageToggleUseType.UseType.bag) {
        NetworkHooks.openGui((ServerPlayerEntity) player, new PortableDankProvider(bag,hand));
        return super.onItemRightClick(world,player,hand);
      } else {
        ItemStack toPlace = Utils.getItemStackInSelectedSlot(bag);
        EquipmentSlotType hand1 = hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
        //handle empty
        if (toPlace.isEmpty()){
          return ActionResult.resultPass(bag);
        }

        //handle food
        if (toPlace.getItem().isFood()) {
          if (player.canEat(false)) {
            player.setActiveHand(hand);
            return ActionResult.resultConsume(bag);
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
  public ActionResultType itemInteractionForEntity(ItemStack bag, PlayerEntity player, LivingEntity entity, Hand hand) {
    if (!Utils.isConstruction(bag))return ActionResultType.FAIL;
    PortableDankHandler handler = Utils.getHandler(bag);
    ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
    EquipmentSlotType hand1 = hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
    player.setItemStackToSlot(hand1, toPlace);
    ActionResultType result = toPlace.getItem().itemInteractionForEntity(toPlace, player, entity, hand);
    handler.setStackInSlot(Utils.getSelectedSlot(bag),toPlace);
    player.setItemStackToSlot(hand1, bag);
    return result;
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return stack.hasTag() && Utils.getMode(stack) != Mode.NORMAL;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack bag, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
    if (bag.hasTag() && Utils.DEV)tooltip.add(new StringTextComponent(bag.getTag().toString()));

    if (!Screen.hasShiftDown()){
      tooltip.add(new TranslationTextComponent("text.dankstorage.shift",
              new StringTextComponent("Shift").mergeStyle(TextFormatting.YELLOW)).mergeStyle(TextFormatting.GRAY));
    }

    if (Screen.hasShiftDown()) {
      tooltip.add(new TranslationTextComponent("text.dankstorage.changemode",new StringTextComponent(Client.CONSTRUCTION.getTranslationKey()).mergeStyle(TextFormatting.YELLOW)).mergeStyle(TextFormatting.GRAY));
      CMessageToggleUseType.UseType mode = Utils.getUseType(bag);
      tooltip.add(
              new TranslationTextComponent("text.dankstorage.currentusetype",new TranslationTextComponent(
                      "dankstorage.usetype."+mode.name().toLowerCase(Locale.ROOT)).mergeStyle(TextFormatting.YELLOW)).mergeStyle(TextFormatting.GRAY));
      tooltip.add(
              new TranslationTextComponent("text.dankstorage.stacklimit",new StringTextComponent(Utils.getStackLimit(bag)+"")
                      .mergeStyle(TextFormatting.GREEN)).mergeStyle(TextFormatting.GRAY));

      DankHandler handler = Utils.getHandler(bag);

      if (handler.isEmpty()){
        tooltip.add(
                new TranslationTextComponent("text.dankstorage.empty").mergeStyle(TextFormatting.ITALIC));
        return;
      }
      int count1 = 0;
      for (int i = 0; i < handler.getSlots(); i++) {
        if (count1 > 10)break;
        ItemStack item = handler.getStackInSlot(i);
        if (item.isEmpty())continue;
        ITextComponent count = new StringTextComponent(Integer.toString(item.getCount())).mergeStyle(TextFormatting.AQUA);
        //tooltip.add(new TranslationTextComponent("text.dankstorage.formatcontaineditems", count, item.getDisplayName().(item.getRarity().color)));
        count1++;
      }
    }
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
    Mode mode = Utils.getMode(stack);
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

    if(useType == CMessageToggleUseType.UseType.bag) {
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
