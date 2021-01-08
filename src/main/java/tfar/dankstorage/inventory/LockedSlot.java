package tfar.dankstorage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class LockedSlot extends Slot {
  public LockedSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
    super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
  }

  @Override
  public boolean canTakeStack(PlayerEntity p_82869_1_) {
    return false;
  }
}
