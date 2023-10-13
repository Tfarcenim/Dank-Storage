package tfar.dankstorage.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.block.CommonDockBlock;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nonnull;

public class UpgradeItem extends Item {

    protected final UpgradeInfo upgradeInfo;

    public UpgradeItem(Properties properties, UpgradeInfo info) {
        super(properties);
        this.upgradeInfo = info;
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        ItemStack upgradeStack = context.getItemInHand();
        BlockState state = world.getBlockState(pos);

        if (player == null || !(state.getBlock() instanceof CommonDockBlock) || !upgradeInfo.canUpgrade(state)) {
            return InteractionResult.FAIL;
        }
        //else {
        //    player.displayClientMessage(Utils.translatable("dankstorage.in_use").withStyle(ChatFormatting.RED), true);
        // }

        DockBlockEntity oldDank = (DockBlockEntity) world.getBlockEntity(pos);

        if (!world.isClientSide) {
            if (oldDank != null) {
                oldDank.upgradeTo(DankStats.values()[upgradeInfo.end]);
                if (!player.getAbilities().instabuild)
                    upgradeStack.shrink(1);
            }
            player.displayClientMessage(Utils.translatable("text.dankstorage.upgrade_successful").withStyle(ChatFormatting.GREEN), true);
        }
        return InteractionResult.SUCCESS;
    }
}