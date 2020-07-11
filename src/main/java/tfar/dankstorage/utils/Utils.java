package tfar.dankstorage.utils;

import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.container.AbstractAbstractDankContainer;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.network.CMessageToggleUseType;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static tfar.dankstorage.network.CMessageTogglePickup.Mode;
import static tfar.dankstorage.network.CMessageTogglePickup.modes;
import static tfar.dankstorage.network.CMessageToggleUseType.useTypes;

public class Utils {

  public static final ITag.INamedTag<Item> BLACKLISTED_STORAGE = ItemTags.makeWrapperTag(new ResourceLocation(DankStorage.MODID, "blacklisted_storage").toString());
  public static final ITag.INamedTag<Item> BLACKLISTED_USAGE = ItemTags.makeWrapperTag(new ResourceLocation(DankStorage.MODID, "blacklisted_usage").toString());

  public static final ITag.INamedTag<Item> WRENCHES = ItemTags.makeWrapperTag(new ResourceLocation("forge", "wrenches").toString());

  public static final String INV = "inv";

  public static Mode getMode(ItemStack bag) {
    return modes[bag.getOrCreateTag().getInt("mode")];
  }

  public static boolean isConstruction(ItemStack bag) {
    return bag.getItem() instanceof DankItem && bag.hasTag()
            && bag.getTag().contains("construction")
            && bag.getTag().getInt("construction") == CMessageToggleUseType.UseType.construction.ordinal();
  }

  //0,1,2,3
  public static void cycleMode(ItemStack bag, PlayerEntity player) {
    int ordinal = bag.getOrCreateTag().getInt("mode");
    ordinal++;
    if (ordinal > modes.length - 1) ordinal = 0;
    bag.getOrCreateTag().putInt("mode", ordinal);
    player.sendStatusMessage(
            new TranslationTextComponent("dankstorage.mode." + modes[ordinal].name()), true);
  }

  public static CMessageToggleUseType.UseType getUseType(ItemStack bag) {
    return useTypes[bag.getOrCreateTag().getInt("construction")];
  }

  //0,1,2
  public static void cyclePlacement(ItemStack bag, PlayerEntity player) {
    int ordinal = bag.getOrCreateTag().getInt("construction");
    ordinal++;
    if (ordinal >= useTypes.length) ordinal = 0;
    bag.getOrCreateTag().putInt("construction", ordinal);
    player.sendStatusMessage(
            new TranslationTextComponent("dankstorage.usetype." + useTypes[ordinal].name()), true);
  }

  public static int getSelectedSlot(ItemStack bag) {
    return bag.getOrCreateTag().getInt("selectedSlot");
  }

  public static void setSelectedSlot(ItemStack bag, int slot) {
    bag.getOrCreateTag().putInt("selectedSlot", slot);
  }

  public static int getSlotCount(ItemStack bag) {
    return getSlotCount(getTier(bag));
  }

  public static int getSlotCount(int tier) {
    if (tier > 0 && tier < 7)
      return 9 * tier;
    if (tier == 7)
      return 81;
    throw new IndexOutOfBoundsException("tier " + tier + " is out of bounds!");
  }

  public static void sort(PlayerEntity player) {
    if (player == null) return;
    Container openContainer = player.openContainer;
    if (openContainer instanceof AbstractAbstractDankContainer) {
      List<SortingData> itemlist = new ArrayList<>();
      DankHandler handler = ((AbstractAbstractDankContainer) openContainer).getHandler();

      for (int i = 0; i < handler.getSlots(); i++) {
        ItemStack stack = handler.getStackInSlot(i);
        if (stack.isEmpty()) continue;
        boolean exists = SortingData.exists(itemlist, stack.copy());
        if (exists) {
          int rem = SortingData.addToList(itemlist, stack.copy());
          if (rem > 0) {
            ItemStack bigstack = stack.copy();
            bigstack.setCount(Integer.MAX_VALUE);
            ItemStack smallstack = stack.copy();
            smallstack.setCount(rem);
            itemlist.add(new SortingData(bigstack));
            itemlist.add(new SortingData(smallstack));
          }
        } else {
          itemlist.add(new SortingData(stack.copy()));
        }
      }
      handler.getContents().clear();
      Collections.sort(itemlist);
      for (SortingData data : itemlist) {
        ItemStack stack = data.stack.copy();
        ItemStack rem = stack.copy();
        for (int i = 0; i < handler.getSlots(); i++) {
          rem = handler.insertItem(i, rem, false);
          if (rem.isEmpty()) break;
        }
      }
    }
  }

  public static int getStackLimit(int tier) {
    switch (tier) {
      case 1:
      default:
        return DankStorage.ServerConfig.stacklimit1.get();
      case 2:
        return DankStorage.ServerConfig.stacklimit2.get();
      case 3:
        return DankStorage.ServerConfig.stacklimit3.get();
      case 4:
        return DankStorage.ServerConfig.stacklimit4.get();
      case 5:
        return DankStorage.ServerConfig.stacklimit5.get();
      case 6:
        return DankStorage.ServerConfig.stacklimit6.get();
      case 7:
        return DankStorage.ServerConfig.stacklimit7.get();
    }
  }

    public static int getStackLimit(ItemStack bag) {
    return getStackLimit(getTier(bag));
  }

  public static int getTier(ItemStack bag) {
    return ((DankItem)bag.getItem()).tier;
  }

  public static void changeSlot(ItemStack bag, boolean right) {
    bag.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
    //don't change slot if empty
      DankHandler dankHandler = (DankHandler)handler;
    if (dankHandler.noValidSlots()) return;
    int selectedSlot = getSelectedSlot(bag);
    int size = handler.getSlots();
    //keep iterating until a valid slot is found (not empty and not blacklisted from usage)
    while (true) {
      if (right) {
        selectedSlot++;
        if (selectedSlot >= size) selectedSlot = 0;
      } else {
        selectedSlot--;
        if (selectedSlot < 0) selectedSlot = size - 1;
      }
      if (!handler.getStackInSlot(selectedSlot).isEmpty() && !handler.getStackInSlot(selectedSlot).getItem().isIn(BLACKLISTED_USAGE))break;
    }
    setSelectedSlot(bag, selectedSlot);
    });
  }

  public static boolean oredict(ItemStack bag) {
    return bag.getItem() instanceof DankItem && bag.hasTag() && bag.getTag().getBoolean("tag");
  }

  public static PortableDankHandler getHandler(ItemStack bag) {
    return new PortableDankHandler(bag);
  }

  public static int getNbtSize(ItemStack stack) {
    return getNbtSize(stack.getTag());
  }

  public static DankItem getItemFromTier(int tier) {
    return (DankItem) ForgeRegistries.ITEMS.getValue(new ResourceLocation(DankStorage.MODID, "dank_" + tier));
  }

  public static int getNbtSize(@Nullable CompoundNBT nbt) {
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    buffer.writeCompoundTag(nbt);
    buffer.release();
    return buffer.writerIndex();
  }

  public static ItemStack getItemStackInSelectedSlot(ItemStack bag) {
    return bag.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            .map(iItemHandler -> {
                    ItemStack stack = iItemHandler.getStackInSlot(Utils.getSelectedSlot(bag));
                    return stack.getItem().isIn(BLACKLISTED_USAGE) ? ItemStack.EMPTY : stack;
  }).orElse(ItemStack.EMPTY);
  }

  public static final Set<ResourceLocation> taglist = new HashSet<>();

  public static boolean areItemStacksConvertible(final ItemStack stack1, final ItemStack stack2) {
    if (stack1.hasTag() || stack2.hasTag()) return false;
    Set<ResourceLocation> taglistofstack1 = stack1.getItem().getTags();
    Set<ResourceLocation> taglistofstack2 = stack2.getItem().getTags();

    Set<ResourceLocation> commontags = new HashSet<>(taglistofstack1);
    commontags.retainAll(taglistofstack2);
    commontags.retainAll(taglist);
    return !commontags.isEmpty();
  }

  /**
   * Copies the nbt compound similar to how {@link CompoundNBT#copy()} does, except it just skips the desired key instead of having to copy a potentially large value
   * which may be expensive, and then remove it from the copy.
   *
   * @implNote If the input {@link CompoundNBT} only contains the key we want to skip, we return null instead of an empty {@link CompoundNBT}.
   */
  @Nullable
  public static CompoundNBT copyNBTSkipKey(@Nonnull CompoundNBT nbt, @Nonnull String keyToSkip) {
    CompoundNBT copiedNBT = new CompoundNBT();
    for (String key : nbt.keySet()) {
      if (keyToSkip.equals(key)) {
        continue;
      }
      INBT innerNBT = nbt.get(key);
      if (innerNBT != null) {
        //Shouldn't be null but double check
        copiedNBT.put(key, innerNBT.copy());
      }
    }
    if (copiedNBT.isEmpty()) {
      return null;
    }
    return copiedNBT;
  }

  public static boolean DEV;

  static {
    try {
      Items.class.getField("field_190931_a");
      DEV = false;
    } catch (NoSuchFieldException e) {
      DEV = true;
    }
  }

  public static boolean isMixinInClasspath() {
    try {
      Class.forName("org.spongepowered.asm.launch.Phases");
      return true;
    }
    catch (ClassNotFoundException e) {
      return false;
    }
  }
}
