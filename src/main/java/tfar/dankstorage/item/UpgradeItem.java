package tfar.dankstorage.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.utils.DankStats;

import javax.annotation.Nonnull;

public class UpgradeItem extends Item {

  protected final UpgradeInfo upgradeInfo;

  public UpgradeItem(Properties properties, UpgradeInfo info) {
    super(properties);
    this.upgradeInfo = info;
  }

  @Nonnull
  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity player = context.getPlayer();
    BlockPos pos = context.getPos();
    World world = context.getWorld();
    ItemStack upgradeStack = context.getItem();
    BlockState state = world.getBlockState(pos);

    if (player == null || !(state.getBlock() instanceof DockBlock) || !upgradeInfo.canUpgrade(state)) {
      return ActionResultType.FAIL;
    }

      if (false) {
        player.sendStatusMessage(new TranslationTextComponent("dankstorage.in_use").mergeStyle(TextFormatting.RED), true);
        return ActionResultType.PASS;
  }

    if (world.isRemote)
      return ActionResultType.SUCCESS;

    DockBlockEntity oldDank = (DockBlockEntity)world.getTileEntity(pos);
    DankStats newTier = upgradeInfo.end;
    oldDank.upgrade(newTier);
    if (!player.abilities.isCreativeMode)
      upgradeStack.shrink(1);

    player.sendStatusMessage(new TranslationTextComponent("dankstorage.upgrade_successful").mergeStyle(TextFormatting.GREEN), true);
    return ActionResultType.SUCCESS;
  }
}