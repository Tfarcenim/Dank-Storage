package tfar.dankstorage.inventory;

import net.minecraft.world.inventory.DataSlot;
import tfar.dankstorage.utils.DankStats;

public class TierDataSlot extends DataSlot {

    public DankStats stats;

    public TierDataSlot(DankStats stats){
        this.stats = stats;
    }

    @Override
    public int get() {
        return stats.ordinal();
    }

    @Override
    public void set(int value) {
        stats = DankStats.values()[value];
    }
}
