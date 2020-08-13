package tfar.dankstorage.item;

import net.minecraft.util.text.Color;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.tile.DankBlockEntity;
import tfar.dankstorage.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
        player.sendStatusMessage(new TranslationTextComponent("metalbarrels.in_use")
                .setStyle(Style.EMPTY.setColor(Color.func_240743_a_(1))), true);
        return ActionResultType.PASS;
  }

    if (world.isRemote)
      return ActionResultType.SUCCESS;

    DankBlockEntity oldDank = (DankBlockEntity)world.getTileEntity(pos);
    int newTier = upgradeInfo.end;
    oldDank.upgrade(newTier);
    if (!player.abilities.isCreativeMode)
      upgradeStack.shrink(1);

    player.sendStatusMessage(new TranslationTextComponent("metalbarrels.upgrade_successful")
            .setStyle(Style.EMPTY.setColor(Color.func_240743_a_(1))), true);
    return ActionResultType.SUCCESS;
  }
}