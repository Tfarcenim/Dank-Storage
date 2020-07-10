package tfar.dankstorage.item;

import net.minecraft.block.BlockState;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.utils.Utils;

public class UpgradeInfo {

  private final int start;
  public final int end;

  public UpgradeInfo(int start,int end) {
    this.start = start;
    this.end = end;
  }

  public boolean canUpgrade(BlockState dank){
    return dank.get(DockBlock.TIER) == start;
  }
}