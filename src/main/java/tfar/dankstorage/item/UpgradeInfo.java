package tfar.dankstorage.item;

import net.minecraft.block.BlockState;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;

public class UpgradeInfo {

  private final int start;
  public final DankStats end;

  public UpgradeInfo(int start, DankStats end) {
    this.start = start;
    this.end = end;
  }

  public boolean canUpgrade(BlockState dank){
    return dank.get(DockBlock.TIER) == start;
  }
}