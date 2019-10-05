package com.tfar.dankstorage.block;

import com.tfar.dankstorage.client.Client;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.CMessageTogglePlacement;
import com.tfar.dankstorage.network.Utils;
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
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.tfar.dankstorage.network.CMessageTogglePickup.*;

public class DankBlock extends Block {
  public DankBlock(Properties p_i48440_1_) {
    super(p_i48440_1_);
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
      nbt.putInt("mode",((AbstractDankStorageTile) tile).mode);
      nbt.putInt("selectedSlot",((AbstractDankStorageTile) tile).selectedSlot);
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
        ((AbstractDankStorageTile) te).mode = stack.getTag().getInt("mode");
        ((AbstractDankStorageTile) te).selectedSlot = stack.getTag().getInt("selectedSlot");
      }
    }
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext ctx) {
    ItemStack bag = ctx.getItem();

    Block block = Block.getBlockFromItem(bag.getItem());
    if (block instanceof DankBlock)return block.getDefaultState();
    return block.isAir(block.getDefaultState()) ? null : block.getStateForPlacement(ctx);
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
    //if (bag.hasTag())tooltip.add(new StringTextComponent(bag.getTag().toString()));

    if (!Screen.hasShiftDown()){
      tooltip.add(new TranslationTextComponent("text.dankstorage.shift",
              new StringTextComponent("Shift").applyTextStyle(TextFormatting.YELLOW)).applyTextStyle(TextFormatting.GRAY));
    }

    if (Screen.hasShiftDown()) {
      tooltip.add(new TranslationTextComponent("text.dankstorage.changemode",new StringTextComponent(Client.CONSTRUCTION.getLocalizedName()).applyTextStyle(TextFormatting.YELLOW)).applyTextStyle(TextFormatting.GRAY));
      CMessageTogglePlacement.UseType mode = Utils.getUseType(bag);
      tooltip.add(
              new TranslationTextComponent("text.dankstorage.currentusemode",new TranslationTextComponent(
                      "dankstorage.usemode."+mode.name().toLowerCase(Locale.ROOT)).applyTextStyle(TextFormatting.YELLOW)).applyTextStyle(TextFormatting.GRAY));
      DankHandler handler = Utils.getHandler(bag,false);

      if (handler.isEmpty()){
        tooltip.add(
                new TranslationTextComponent("text.dankstorage.empty").applyTextStyle(TextFormatting.ITALIC));
        return;
      }

      for (int i = 0; i < handler.getSlots(); i++) {
        ItemStack item = handler.getStackInSlot(i);
        if (item.isEmpty())continue;
          ITextComponent count = new StringTextComponent(Integer.toString(item.getCount())).applyTextStyle(TextFormatting.AQUA);
        tooltip.add(new TranslationTextComponent("text.dankstorage.formatcontaineditems", count, item.getDisplayName().applyTextStyle(item.getRarity().color)));


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

    if (mode == Mode.NORMAL) {
      return false;
    }
    ItemStack toPickup = event.getItem().getItem();
    int count = toPickup.getCount();
    PortableDankHandler inv = Utils.getHandler(bag,false);
    ItemStack rem = toPickup.copy();
    //stack with existing items
    List<Integer> emptyslots = new LinkedList<>();
    for (int i = 0; i < inv.getSlots(); i++){
      if (inv.getStackInSlot(i).isEmpty()){
        emptyslots.add(i);
        continue;
      }
      rem = inv.insertItem(i,rem,false);
      if (rem.isEmpty())break;
    }
    //only iterate empty slots
    if (!rem.isEmpty())
    for (int slot : emptyslots) {
      rem = inv.insertItem(slot,rem,false);
      if (rem.isEmpty())break;
    }
    //leftovers
    toPickup.setCount(rem.getCount());
    if (rem.getCount() != count) {
      bag.setAnimationsToGo(5);
      PlayerEntity player = event.getPlayer();
      player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
      inv.writeItemStack();
    }
    return toPickup.isEmpty();
  }
}
