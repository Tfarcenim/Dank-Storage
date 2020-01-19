package tfar.dankstorage.inventory;

import tfar.dankstorage.utils.Utils;
import net.minecraft.item.ItemStack;

public class PortableDankHandler extends DankHandler {

  public final ItemStack bag;

  public PortableDankHandler(ItemStack bag) {
    this(Utils.getSlotCount(bag),Utils.getStackLimit(bag),bag);
  }

  protected PortableDankHandler(int size, int stacklimit, ItemStack bag) {
    super(size,stacklimit);
    this.bag = bag;
    readItemStack();
  }

  public void writeItemStack() {
      bag.getOrCreateTag().put(Utils.INV,serializeNBT());
  }

  public void readItemStack() {
      deserializeNBT(bag.getOrCreateChildTag(Utils.INV));
  }

  @Override
  public void onContentsChanged(int slot) {
    this.writeItemStack();
  }
}
