package tfar.dankstorage.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.Constants;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import net.minecraft.item.ItemStack;

public class PortableDankHandler extends DankHandler {

  public final ItemStack bag;
  private final Hand hand;

  public PortableDankHandler(ItemStack bag, Hand hand) {
    this(Utils.getStats(bag),bag,hand);
  }

  protected PortableDankHandler(DankStats stats, ItemStack bag,Hand hand) {
    super(stats);
    this.bag = bag;
    this.hand = hand;
    load();
  }

  public void save() {
      bag.getOrCreateTag().put(Utils.INV,serializeNBT());
  }

  public void load() {
      deserializeNBT(bag.getOrCreateChildTag(Utils.INV));
  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return player.getHeldItem(hand) == bag;
  }

  @Override
  public void onContentsChanged(int slot) {
    this.save();
  }
}
