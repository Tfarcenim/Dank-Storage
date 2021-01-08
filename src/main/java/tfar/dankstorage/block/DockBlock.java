package tfar.dankstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.blockentity.DockBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DockBlock extends Block {

  public static final IntegerProperty TIER = IntegerProperty.create("tier",0,7);

  public static final VoxelShape EMPTY;

  public static final VoxelShape DOCKED;

  static {
    VoxelShape a1 = Block.makeCuboidShape(0,0,0,16,4,16);
    VoxelShape b1 = Block.makeCuboidShape(4,0,4,12,4,12);
    VoxelShape shape1 = VoxelShapes.combine(a1,b1, IBooleanFunction.NOT_SAME);

    VoxelShape a2 = Block.makeCuboidShape(0,12,0,16,16,16);
    VoxelShape b2 = Block.makeCuboidShape(4,12,4,12,16,12);
    VoxelShape shape2 = VoxelShapes.combine(a2,b2, IBooleanFunction.NOT_SAME);

    VoxelShape p1 = Block.makeCuboidShape(0,4,0,4,12,4);

    VoxelShape p2 = Block.makeCuboidShape(12,4,0,16,12,4);

    VoxelShape p3 = Block.makeCuboidShape(0,4,12,4,12,16);

    VoxelShape p4 = Block.makeCuboidShape(12,4,12,12,12,16);

    EMPTY = VoxelShapes.or(shape1,shape2,p1,p2,p3,p4);

    DOCKED = VoxelShapes.or(EMPTY,Block.makeCuboidShape(4,4,4,12,12,12));

  }

  public DockBlock(Properties p_i48440_1_) {
    super(p_i48440_1_);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    if (context.getEntity() instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity)context.getEntity();
      if (player.getHeldItemMainhand().getItem() instanceof DankItem)
        return DOCKED;
    }
    return state.get(TIER) > 0 ? DOCKED : EMPTY;
  }

  @Nonnull
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_) {
    if (!world.isRemote) {
      final TileEntity tile = world.getTileEntity(pos);
      if (tile instanceof DockBlockEntity) {
        ItemStack held = player.getHeldItem(hand);
        if (player.isCrouching() && held.getItem().isIn(ModTags.WRENCHES)) {
          world.destroyBlock(pos, true, player);
          return ActionResultType.SUCCESS;
        }

        if (held.getItem() instanceof DankItem) {

          if (state.get(TIER) > 0) {
            ((DockBlockEntity) tile).removeTank();
          }
          ((DockBlockEntity) tile).addTank(held);
          return ActionResultType.SUCCESS;
        }

        if (held.isEmpty() && player.isSneaking()) {
          ((DockBlockEntity)tile).removeTank();
          return ActionResultType.SUCCESS;
        }
        if (state.get(TIER) > 0) {
          player.openContainer((INamedContainerProvider) tile);
        }
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
    return new DockBlockEntity();
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(TIER);
  }


}