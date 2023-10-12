package tfar.dankstorage.item;

import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.block.DockBlock;

public class UpgradeInfo {

    public final int end;
    private final int start;

    public UpgradeInfo(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean canUpgrade(BlockState dank) {
        return dank.getValue(DockBlock.TIER) == start;
    }
}