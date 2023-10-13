package tfar.dankstorage.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class CommonDockBlock extends Block implements EntityBlock {

    private final BiFunction<BlockPos,BlockState,BlockEntity> function;
    public CommonDockBlock(Properties $$0, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super($$0);
        this.function = function;
    }

    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 7);

    public static final VoxelShape EMPTY;

    public static final VoxelShape DOCKED;

    static {
        VoxelShape a1 = Block.box(0, 0, 0, 16, 4, 16);
        VoxelShape b1 = Block.box(4, 0, 4, 12, 4, 12);
        VoxelShape shape1 = Shapes.joinUnoptimized(a1, b1, BooleanOp.NOT_SAME);

        VoxelShape a2 = Block.box(0, 12, 0, 16, 16, 16);
        VoxelShape b2 = Block.box(4, 12, 4, 12, 16, 12);
        VoxelShape shape2 = Shapes.joinUnoptimized(a2, b2, BooleanOp.NOT_SAME);

        VoxelShape p1 = Block.box(0, 4, 0, 4, 12, 4);

        VoxelShape p2 = Block.box(12, 4, 0, 16, 12, 4);

        VoxelShape p3 = Block.box(0, 4, 12, 4, 12, 16);

        VoxelShape p4 = Block.box(12, 4, 12, 12, 12, 16);

        EMPTY = Shapes.or(shape1, shape2, p1, p2, p3, p4);

        DOCKED = Shapes.or(EMPTY, Block.box(4, 4, 4, 12, 12, 12));

    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(TIER) > 0 ? DOCKED : EMPTY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return function.apply(blockPos, blockState);
    }

    @javax.annotation.Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        ItemStack bag = ctx.getItemInHand();

        Block block = Block.byItem(bag.getItem());
        if (block instanceof CommonDockBlock) return block.defaultBlockState();
        return block.getStateForPlacement(ctx);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TIER);
    }

}
