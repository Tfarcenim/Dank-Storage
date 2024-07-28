package tfar.dankstorage.utils;

import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.block.CDockBlock;

public record UpgradeInfo(int end,int start) {
    public boolean canUpgrade(BlockState dank) {
        return dank.getValue(CDockBlock.TIER) == start;
    }
}