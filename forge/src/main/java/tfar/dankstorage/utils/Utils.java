package tfar.dankstorage.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.DankStorageForge;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.mixin.MinecraftServerAccess;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.world.ClientData;
import tfar.dankstorage.world.DankInventory;

import java.nio.file.Path;


public class Utils extends CommonUtils{

    public static void setPickSlot(Level level,ItemStack bag, ItemStack stack) {

        DankInventory dankInventory = getInventory(bag,level);

        if (dankInventory != null) {
            int slot = findSlotMatchingItem(dankInventory, stack);
            if (slot != INVALID) setSelectedSlot(bag, slot);
        }
    }

    public static int findSlotMatchingItem(DankInventory dankInventory, ItemStack itemStack) {
        for (int i = 0; i < dankInventory.getSlots(); ++i) {
            ItemStack stack = dankInventory.getStackInSlot(i);
            if (stack.isEmpty() || !ItemStack.isSameItemSameTags(itemStack,stack)) continue;
            return i;
        }
        return INVALID;
    }

    public static ItemStack getSelectedItem(ItemStack bag,Level level) {
        if (bag.hasTag()) {
            int selected = getSelectedSlot(bag);
            if (selected == INVALID) return ItemStack.EMPTY;
            if (!level.isClientSide) {
                DankInventory dankInventory = getInventory(bag, level);
                if (dankInventory != null) {
                    return dankInventory.getStackInSlot(selected);
                } else {
                //    System.out.println("Attempted to access a selected item from a null inventory");
                }
            } else {
                return ClientData.selectedItem;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void changeSelectedSlot(ItemStack bag, boolean right, ServerPlayer player) {
        DankInventory handler = getInventory(bag,player.serverLevel());
        //don't change slot if empty
        if (handler == null || handler.noValidSlots()) return;
        int selectedSlot = getSelectedSlot(bag);
        int size = handler.getSlots();
        //keep iterating until a valid slot is found (not empty and not blacklisted from usage)
        if (right) {
            selectedSlot++;
            if (selectedSlot >= size) selectedSlot = 0;
        } else {
            selectedSlot--;
            if (selectedSlot < 0) selectedSlot = size - 1;
        }
        ItemStack selected = handler.getStackInSlot(selectedSlot);

        while (selected.isEmpty() || selected.is(ModTags.BLACKLISTED_USAGE)) {
            if (right) {
                selectedSlot++;
                if (selectedSlot >= size) selectedSlot = 0;
            } else {
                selectedSlot--;
                if (selectedSlot < 0) selectedSlot = size - 1;
            }
            selected = handler.getStackInSlot(selectedSlot);
        }
        if (selectedSlot != INVALID) {
            setSelectedSlot(bag, selectedSlot);
            DankPacketHandler.sendSelectedItem(player,selected);
            player.displayClientMessage(selected.getHoverName(),true);
        }
    }



    public static DankInventory getInventory(ItemStack bag, Level level) {
        if (!level.isClientSide) {
            int id = getFrequency(bag);
            if (id != INVALID) {

                Path path = ((MinecraftServerAccess)level.getServer()).getStorageSource()
                        .getDimensionPath(level.getServer().getLevel(Level.OVERWORLD).dimension())
                        .resolve("data/"+DankStorage.MODID+"/"+id+".dat");

                if (path.toFile().isFile()) {
                    return DankStorageForge.getData(id,level.getServer()).createInventory(id);
                } else {
                    return DankStorageForge.getData(id,level.getServer()).createFreshInventory(getDefaultStats(bag),id);
                }
            } else {
                return null;
            }
        }
        throw new RuntimeException("Attempted to get inventory on client");
    }

    public static ItemStack getItemStackInSelectedSlot(ItemStack bag,ServerLevel level) {
        DankInventory inv = getInventory(bag,level);
        if (inv == null) return ItemStack.EMPTY;
        int slot = getSelectedSlot(bag);
        if (slot == INVALID) return ItemStack.EMPTY;
        ItemStack stack = inv.getStackInSlot(slot);
        return stack.is(ModTags.BLACKLISTED_USAGE) ? ItemStack.EMPTY : stack;
    }

    /*public static boolean areItemStacksConvertible(final ItemStack stack1, final ItemStack stack2) {
        if (stack1.hasTag() || stack2.hasTag()) return false;
        Collection<ResourceLocation> taglistofstack1 = getTags(stack1.getItem());
        Collection<ResourceLocation> taglistofstack2 = getTags(stack2.getItem());

        Set<ResourceLocation> commontags = new HashSet<>(taglistofstack1);
        commontags.retainAll(taglistofstack2);
        commontags.retainAll(taglist);
        return !commontags.isEmpty();
    }

    private static Collection<ResourceLocation> getTags(Item item) {
        return getTagsFor(ItemTags.getAllTags(), item);
    }

    /**
     * can't use TagGroup#getTagsFor because it's client only

    private static Collection<ResourceLocation> getTagsFor(TagCollection<Item> tagGroup, Item item) {
        return tagGroup.getAllTags().entrySet().stream()
                .filter(identifierTagEntry -> identifierTagEntry.getValue().contains(item))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }*/


}
