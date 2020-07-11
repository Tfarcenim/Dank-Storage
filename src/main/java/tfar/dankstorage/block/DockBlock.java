package tfar.dankstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.network.CMessageToggleUseType;
import tfar.dankstorage.tile.DankBlockEntity;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static tfar.dankstorage.network.CMessageTogglePickup.Mode;

public class DockBlock extends Block {

  public static final IntegerProperty TIER = IntegerProperty.create("tier",0,7);

  public DockBlock(Properties p_i48440_1_) {
    super(p_i48440_1_);
  }

  @Nonnull
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
    if (!world.isRemote) {
      final TileEntity tile = world.getTileEntity(pos);
      if (tile instanceof DankBlockEntity) {
        ItemStack held = player.getHeldItem(hand);
        if (player.isCrouching() && held.getItem().isIn(Utils.WRENCHES)) {
          world.destroyBlock(pos, true, player);
          return ActionResultType.SUCCESS;
        }

        if (held.getItem() instanceof DankItem) {
          ((DankBlockEntity)tile).addTank(held);
          return ActionResultType.SUCCESS;
        }

        if (held.isEmpty() && player.isSneaking()) {
          ((DankBlockEntity)tile).removeTank();
          return ActionResultType.SUCCESS;
        }

        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
      }
    }
    return ActionResultType.SUCCESS;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext ctx) {
    ItemStack bag = ctx.getItem();

    Block block = Block.getBlockFromItem(bag.getItem());
    if (block instanceof DockBlock)return block.getDefaultState();
    return block.isAir(block.getDefaultState(),null,null) ? null : block.getStateForPlacement(ctx);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }



  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new DankBlockEntity();
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(TIER);
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
      player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
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