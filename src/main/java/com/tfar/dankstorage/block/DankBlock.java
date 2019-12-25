package com.tfar.dankstorage.block;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.client.Client;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.CMessageToggleUseType;
import com.tfar.dankstorage.utils.Utils;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import com.tfar.dankstorage.tile.DankTiles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.tfar.dankstorage.network.CMessageTogglePickup.*;

public class DankBlock extends Block {
  public DankBlock(Properties p_i48440_1_) {
    super(p_i48440_1_);
  }

  @Override
  public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
    if (!world.isRemote) {
      final TileEntity tile = world.getTileEntity(pos);

      if (player.isCrouching() && player.getHeldItem(hand).getItem().isIn(Utils.WRENCHES)){
        if (tile instanceof AbstractDankStorageTile){
          world.func_225521_a_(pos,true,player);
          return ActionResultType.SUCCESS;
        }
      }

      if (tile instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());

      }
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof AbstractDankStorageTile && !world.isRemote) {
      if (stack.hasTag()){
        ((AbstractDankStorageTile) te).setContents(stack.getOrCreateChildTag(Utils.INV));
        ((AbstractDankStorageTile) te).mode = stack.getTag().getInt("mode");
        ((AbstractDankStorageTile) te).selectedSlot = stack.getTag().getInt("selectedSlot");
        if (stack.hasDisplayName()) {
          ((AbstractDankStorageTile) te).setCustomName(stack.getDisplayName());
        }
      }
    }
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext ctx) {
    ItemStack bag = ctx.getItem();

    Block block = Block.getBlockFromItem(bag.getItem());
    if (block instanceof DankBlock)return block.getDefaultState();
    return block.isAir(block.getDefaultState(),null,null) ? null : block.getStateForPlacement(ctx);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack bag, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
    if (bag.hasTag())tooltip.add(new StringTextComponent(bag.getTag().toString()));

    if (!Screen.hasShiftDown()){
      tooltip.add(new TranslationTextComponent("text.dankstorage.shift",
              new StringTextComponent("Shift").applyTextStyle(TextFormatting.YELLOW)).applyTextStyle(TextFormatting.GRAY));
    }

    if (Screen.hasShiftDown()) {
      tooltip.add(new TranslationTextComponent("text.dankstorage.changemode",new StringTextComponent(Client.CONSTRUCTION.getLocalizedName()).applyTextStyle(TextFormatting.YELLOW)).applyTextStyle(TextFormatting.GRAY));
      CMessageToggleUseType.UseType mode = Utils.getUseType(bag);
      tooltip.add(
              new TranslationTextComponent("text.dankstorage.currentusetype",new TranslationTextComponent(
                      "dankstorage.usetype."+mode.name().toLowerCase(Locale.ROOT)).applyTextStyle(TextFormatting.YELLOW)).applyTextStyle(TextFormatting.GRAY));
      tooltip.add(
              new TranslationTextComponent("text.dankstorage.stacklimit",new StringTextComponent(Utils.getStackLimit(getRegistryName())+"").applyTextStyle(TextFormatting.GREEN)).applyTextStyle(TextFormatting.GRAY));

      DankHandler handler = Utils.getHandler(bag);

      if (handler.isEmpty()){
        tooltip.add(
                new TranslationTextComponent("text.dankstorage.empty").applyTextStyle(TextFormatting.ITALIC));
        return;
      }
      int count1 = 0;
      for (int i = 0; i < handler.getSlots(); i++) {
        if (count1 > 10)break;
        ItemStack item = handler.getStackInSlot(i);
        if (item.isEmpty())continue;
        ITextComponent count = new StringTextComponent(Integer.toString(item.getCount())).applyTextStyle(TextFormatting.AQUA);
        tooltip.add(new TranslationTextComponent("text.dankstorage.formatcontaineditems", count, item.getDisplayName().applyTextStyle(item.getRarity().color)));
        count1++;
      }
    }
  }

  @Nonnull
  @Override
  public ITextComponent getNameTextComponent() {
    int tier = Utils.getTier(this.getRegistryName());
    switch (tier){
      case 1:return super.getNameTextComponent().applyTextStyle(TextFormatting.DARK_GRAY);
      case 2:return super.getNameTextComponent().applyTextStyle(TextFormatting.RED);
      case 3:return super.getNameTextComponent().applyTextStyle(TextFormatting.GOLD);
      case 4:return super.getNameTextComponent().applyTextStyle(TextFormatting.GREEN);
      case 5:return super.getNameTextComponent().applyTextStyle(TextFormatting.AQUA);
      case 6:return super.getNameTextComponent().applyTextStyle(TextFormatting.DARK_PURPLE);
      case 7:return super.getNameTextComponent().applyTextStyle(TextFormatting.WHITE);
    }
    return super.getNameTextComponent();
  }

  public static boolean onItemPickup(EntityItemPickupEvent event, ItemStack bag) {

    Mode mode = Utils.getMode(bag);
    if (mode == Mode.NORMAL)return false;
    PortableDankHandler inv = Utils.getHandler(bag);
    ItemStack toPickup = event.getItem().getItem();
    int count = toPickup.getCount();
    ItemStack rem = toPickup.copy();
    boolean oredict = Utils.oredict(bag);

        //stack with existing items
        List<Integer> emptyslots = new ArrayList<>();
        for (int i = 0; i < inv.getSlots(); i++){
          if (inv.getStackInSlot(i).isEmpty()){
            emptyslots.add(i);
            continue;
          }
          rem = insertIntoHandler(mode,inv,i,rem,false,oredict);
          if (rem.isEmpty())break;
        }
        //only iterate empty slots
        if (!rem.isEmpty())
          for (int slot : emptyslots) {
            rem = insertIntoHandler(mode,inv,slot,rem,false,oredict);
            if (rem.isEmpty())break;
          }
    //leftovers
    toPickup.setCount(rem.getCount());
    if (rem.getCount() != count) {
      bag.setAnimationsToGo(5);
      PlayerEntity player = event.getPlayer();
      player.world.playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
      inv.writeItemStack();
    }
    return toPickup.isEmpty();
  }



  public static ItemStack insertIntoHandler(Mode mode, PortableDankHandler inv, int slot, ItemStack toInsert, boolean simulate, boolean oredict){

    ItemStack existing = inv.getStackInSlot(slot);
    if (ItemHandlerHelper.canItemStacksStack(toInsert,existing) || (oredict && Utils.areItemStacksConvertible(toInsert,existing))){
      int stackLimit = inv.stacklimit;
      int total = toInsert.getCount() + existing.getCount();
      int remainder = total - stackLimit;
      if (remainder <= 0) {
        if (!simulate)inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
        return ItemStack.EMPTY;
      }
      else {
        if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
        if (mode == Mode.VOID_PICKUP) return ItemStack.EMPTY;
        return ItemHandlerHelper.copyStackWithSize(toInsert, remainder);
      }
    } else if (existing.isEmpty() && mode == Mode.FILTERED_PICKUP && toInsert.isItemEqual(existing) && ItemStack.areItemStackTagsEqual(existing, toInsert)){
      if (!simulate)inv.getContents().set(slot, toInsert);
      return ItemHandlerHelper.copyStackWithSize(toInsert,toInsert.getCount() - inv.getStackLimit(slot,toInsert));
    } else return toInsert;
  }
}