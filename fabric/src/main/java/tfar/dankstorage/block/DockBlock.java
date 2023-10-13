package tfar.dankstorage.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.blockentity.DockBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class DockBlock extends CommonDockBlock {


    public DockBlock(Properties $$0, BiFunction<BlockPos, BlockState, BlockEntity> function) {
        super($$0, function);
    }

    @Nonnull
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult p_225533_6_) {
        if (!world.isClientSide) {
            final BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof DockBlockEntity dockBlockEntity) {
                ItemStack held = player.getItemInHand(hand);
                if (player.isCrouching() && held.is(ModTags.WRENCHES)) {
                    world.destroyBlock(pos, true, player);
                    return InteractionResult.SUCCESS;
                }

                if (held.getItem() instanceof DankItem) {

                    if (state.getValue(TIER) > 0) {
                        dockBlockEntity.giveToPlayer(player);
                    }
                    dockBlockEntity.addDank(held);
                    return InteractionResult.SUCCESS;
                }

                if (player.isShiftKeyDown() && state.getValue(TIER) > 0) {
                    dockBlockEntity.giveToPlayer(player);
                    return InteractionResult.SUCCESS;
                }

                player.openMenu((MenuProvider) tile);
            }
        }
        return InteractionResult.SUCCESS;
    }
}