package tfar.dankstorage.item;

import net.minecraft.ChatFormatting;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.blockentity.CommonDockBlockEntity;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.UpgradeInfo;

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

        if (player == null || !(state.getBlock() instanceof CDockBlock) || !upgradeInfo.canUpgrade(state)) {
            return InteractionResult.FAIL;
        }
        //else {
        //    player.displayClientMessage(Component.translatable("dankstorage.in_use").withStyle(ChatFormatting.RED), true);
        // }

        CommonDockBlockEntity<?> oldDank = (CommonDockBlockEntity<?>) world.getBlockEntity(pos);

        if (!world.isClientSide) {
            if (oldDank != null) {
                oldDank.upgradeTo(DankStats.values()[upgradeInfo.end()]);
                if (!player.getAbilities().instabuild)
                    upgradeStack.shrink(1);
            }
            player.displayClientMessage(Component.translatable("text.dankstorage.upgrade_successful").withStyle(ChatFormatting.GREEN), true);
        }
        return InteractionResult.SUCCESS;
    }
}