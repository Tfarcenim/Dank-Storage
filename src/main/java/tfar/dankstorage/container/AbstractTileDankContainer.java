package tfar.dankstorage.container;

import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.tile.AbstractDankStorageTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class AbstractTileDankContainer extends AbstractAbstractDankContainer {

  public AbstractDankStorageTile te;

  public AbstractTileDankContainer(ContainerType<?> type, int p_i50105_2_, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, int rows) {
    super(type, p_i50105_2_, playerInventory,rows);
    te = (AbstractDankStorageTile) world.getTileEntity(pos);
    te.openInventory(player);
    addOwnSlots();
    addPlayerSlots(new InvWrapper(playerInventory));
  }

  @Override
  public DankHandler getHandler() {
    return te.getHandler();
  }

  @Override
  public void onContainerClosed(PlayerEntity playerIn) {
    super.onContainerClosed(playerIn);
    this.te.closeInventory(playerIn);
  }
}

