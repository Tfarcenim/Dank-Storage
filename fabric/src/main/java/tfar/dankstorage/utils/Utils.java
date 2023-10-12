package tfar.dankstorage.utils;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.mixin.CraftingContainerAccess;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;
import tfar.dankstorage.world.ClientData;
import tfar.dankstorage.world.DankInventory;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static tfar.dankstorage.network.server.C2SMessageToggleUseType.useTypes;

public class Utils {

    public static final TagKey<Item> BLACKLISTED_STORAGE = bind(new ResourceLocation(DankStorageFabric.MODID, "blacklisted_storage"));
    public static final TagKey<Item> BLACKLISTED_USAGE = bind(new ResourceLocation(DankStorageFabric.MODID, "blacklisted_usage"));

    public static final TagKey<Item> WRENCHES = bind(new ResourceLocation("forge", "wrenches"));

    private static TagKey<Item> bind(ResourceLocation string) {
        return TagKey.create(Registries.ITEM, string);
    }

    public static final String ID = "dankstorage:id";
    public static final Set<ResourceLocation> taglist = new HashSet<>();
    public static boolean DEV = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static final int INVALID = -1;

    public static final String SET = "settings";

    @Nullable
    public static CompoundTag getSettings(ItemStack bag) {
        return hasSettings(bag) ? bag.getTag().getCompound(SET) : null;
    }

    public static CompoundTag getOrCreateSettings(ItemStack bag) {
        if (hasSettings(bag)) {
            return bag.getTag().getCompound(SET);
        } else {
            bag.getOrCreateTag().put(SET, new CompoundTag());
            return getSettings(bag);
        }
    }

    public static PickupMode getPickupMode(ItemStack bag) {
        CompoundTag tag = getSettings(bag);
        if (tag != null) {
            return PickupMode.PICKUP_MODES[tag.getInt("mode")];
        }
        return PickupMode.NONE;
    }

    public static boolean isConstruction(ItemStack bag) {
        CompoundTag settings = Utils.getSettings(bag);
        return settings != null && settings.getInt("construction") == C2SMessageToggleUseType.UseType.construction.ordinal();
    }

    public static DankStats getStatsfromRows(int rows) {
        switch (rows) {
            case 1:
                return DankStats.one;
            case 2:
                return DankStats.two;
            case 3:
                return DankStats.three;
            case 4:
                return DankStats.four;
            case 5:
                return DankStats.five;
            case 6:
                return DankStats.six;
            case 9:
                return DankStats.seven;
        }
        throw new IllegalStateException(String.valueOf(rows));
    }

    //0,1,2,3
    public static void cyclePickupMode(ItemStack bag, Player player) {
        int ordinal = getOrCreateSettings(bag).getInt("mode");
        ordinal++;
        if (ordinal > PickupMode.PICKUP_MODES.length - 1) ordinal = 0;
        getOrCreateSettings(bag).putInt("mode", ordinal);
        player.displayClientMessage(
                Component.translatable("dankstorage.mode." + PickupMode.PICKUP_MODES[ordinal].name()), true);
    }

    public static C2SMessageToggleUseType.UseType getUseType(ItemStack bag) {
        CompoundTag settings = getSettings(bag);
        return settings != null ? useTypes[settings.getInt("construction")] : C2SMessageToggleUseType.UseType.bag;
    }

    //0,1,2
    public static void cyclePlacement(ItemStack bag, Player player) {
        CompoundTag tag = getOrCreateSettings(bag);
        int ordinal = tag.getInt("construction");
        ordinal++;
        if (ordinal >= useTypes.length) ordinal = 0;
        tag.putInt("construction", ordinal);
        player.displayClientMessage(
                Component.translatable("dankstorage.usetype." + useTypes[ordinal].name()), true);
    }

    //this can be 0 - 80
    public static int getSelectedSlot(ItemStack bag) {
        CompoundTag settings = Utils.getSettings(bag);
        return settings != null && settings.contains("selectedSlot") ? settings.getInt("selectedSlot") : INVALID;
    }

    public static void setSelectedSlot(ItemStack bag, int slot) {
        getOrCreateSettings(bag).putInt("selectedSlot",slot);
    }

    public static void setPickSlot(Level level,ItemStack bag, ItemStack stack) {

        DankInventory dankInventory = getInventory(bag,level);

        if (dankInventory != null) {
            int slot = findSlotMatchingItem(dankInventory, stack);
            if (slot != INVALID) setSelectedSlot(bag, slot);
        }
    }

    public static int findSlotMatchingItem(DankInventory dankInventory,ItemStack itemStack) {
        for (int i = 0; i < dankInventory.getContainerSize(); ++i) {
            ItemStack stack = dankInventory.getItem(i);
            if (stack.isEmpty() || !ItemStack.isSameItemSameTags(itemStack,stack)) continue;
            return i;
        }
        return INVALID;
    }

    public static ItemStack getSelectedItem(ItemStack bag,Level level) {
        if (bag.hasTag()) {
            int selected = getSelectedSlot(bag);
            if (!level.isClientSide) {
                DankInventory dankInventory = getInventory(bag, level);
                if (dankInventory != null) {
                    return dankInventory.getItem(selected);
                } else {
                //    System.out.println("Attempted to access a selected item from a null inventory");
                }
            } else {
                return ClientData.selectedItem;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void merge(List<ItemStack> stacks, ItemStack toMerge) {
        for (ItemStack stack : stacks) {
            if (ItemHandlerHelper.canItemStacksStack(stack, toMerge)) {
                int grow = Math.min(Integer.MAX_VALUE - stack.getCount(), toMerge.getCount());
                if (grow > 0) {
                    stack.grow(grow);
                    toMerge.shrink(grow);
                }
            }
        }
        if (!toMerge.isEmpty()) {
            stacks.add(toMerge);
        }
    }

    public static List<ItemStackWrapper> wrap(List<ItemStack> stacks) {
        return stacks.stream().map(ItemStackWrapper::new).collect(Collectors.toList());
    }

    public static DankStats getStats(ItemStack bag) {
        return ((DankItem) bag.getItem()).stats;
    }

    public static void changeSelectedSlot(ItemStack bag, boolean right, ServerPlayer player) {
        DankInventory handler = getInventory(bag,player.getLevel());
        //don't change slot if empty
        if (handler == null || handler.noValidSlots()) return;
        int selectedSlot = getSelectedSlot(bag);
        int size = handler.getContainerSize();
        //keep iterating until a valid slot is found (not empty and not blacklisted from usage)
        if (right) {
            selectedSlot++;
            if (selectedSlot >= size) selectedSlot = 0;
        } else {
            selectedSlot--;
            if (selectedSlot < 0) selectedSlot = size - 1;
        }
        ItemStack selected = handler.getItem(selectedSlot);

        while (selected.isEmpty() || selected.is(BLACKLISTED_USAGE)) {
            if (right) {
                selectedSlot++;
                if (selectedSlot >= size) selectedSlot = 0;
            } else {
                selectedSlot--;
                if (selectedSlot < 0) selectedSlot = size - 1;
            }
            selected = handler.getItem(selectedSlot);
        }
        if (selectedSlot != INVALID) {
            setSelectedSlot(bag, selectedSlot);
            DankPacketHandler.sendSelectedItem(player,selected);
            player.displayClientMessage(selected.getHoverName(),true);
        }
    }

    //make sure to return an invalid ID for unassigned danks
    public static int getFrequency(ItemStack bag) {
        CompoundTag settings = getSettings(bag);
        if (settings != null && settings.contains(ID)) {
            return settings.getInt(ID);
        }
        return INVALID;
    }

    public static void setFrequency(ItemStack bag,int frequency) {
        getOrCreateSettings(bag).putInt(ID,frequency);
    }

    private static boolean hasSettings(ItemStack bag) {
        return bag.hasTag() && bag.getTag().contains(SET);
    }

    public static boolean oredict(ItemStack bag) {
        return bag.getItem() instanceof DankItem && bag.hasTag() && getSettings(bag).getBoolean("tag");
    }

    public static DankInventory getOrCreateInventory(ItemStack bag, Level level) {
        if (!level.isClientSide) {
            int id = getFrequency(bag);
            return DankStorageFabric.instance.data.getOrCreateInventory(id,getStats(bag));
        }
        throw new RuntimeException("Attempted to get inventory on client");
    }

    public static DankInventory getInventory(ItemStack bag, Level level) {
        if (!level.isClientSide) {
            int id = getFrequency(bag);
            return DankStorageFabric.instance.data.getInventory(id);
        }
        throw new RuntimeException("Attempted to get inventory on client");
    }

    public static int getNbtSize(ItemStack stack) {
        return getNbtSize(stack.getTag());
    }

    public static DankItem getItemFromTier(int tier) {
        return (DankItem) BuiltInRegistries.ITEM.get(new ResourceLocation(DankStorageFabric.MODID, "dank_" + tier));
    }

    public static int getNbtSize(@Nullable CompoundTag nbt) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeNbt(nbt);
        buffer.release();
        return buffer.writerIndex();
    }

    public static ItemStack getItemStackInSelectedSlot(ItemStack bag,ServerLevel level) {
        DankInventory inv = getInventory(bag,level);
        if (inv == null) return ItemStack.EMPTY;
        ItemStack stack = inv.getItem(Utils.getSelectedSlot(bag));
        return stack.is(BLACKLISTED_USAGE) ? ItemStack.EMPTY : stack;
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

    public static boolean isHoldingDank(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof DankItem) return true;
        stack = player.getOffhandItem();
        return stack.getItem() instanceof DankItem;
    }

    public static boolean canMerge(ItemStack first, ItemStack second, Container inventory) {
        if (first.getItem() != second.getItem()) {
            return false;
        } else if (first.getDamageValue() != second.getDamageValue()) {
            return false;
        } else if (first.getCount() > inventory.getMaxStackSize()) {
            return false;
        } else {
            return ItemStack.tagMatches(first, second);
        }
    }

    public static void warn(Player player, DankStats item, DankStats inventory) {
        player.sendSystemMessage(Component.literal("Dank Item Level "+item.ordinal() +" cannot open Dank Inventory Level "+inventory.ordinal()));
    }

    @Nullable
    public static InteractionHand getHandWithDank(Player player) {
        if (player.getMainHandItem().getItem() instanceof DankItem) return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() instanceof DankItem) return InteractionHand.OFF_HAND;
        return null;
    }

    private static List<CraftingRecipe> REVERSIBLE3x3 = new ArrayList<>();
    private static List<CraftingRecipe> REVERSIBLE2x2 = new ArrayList<>();
    private static boolean cached = false;

    public static void uncacheRecipes(RecipeManager manager) {
        cached = false;
    }

    public static Pair<ItemStack,Integer> compress(ServerLevel level, ItemStack stack) {

        for (CraftingRecipe recipe : REVERSIBLE3x3) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(level.registryAccess()),9);
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(level.registryAccess()),4);
            }
        }

        return Pair.of(ItemStack.EMPTY,0);
    }

    public static boolean canCompress(ServerLevel level, ItemStack stack) {
        if (!cached) {
            REVERSIBLE3x3 = findReversibles(level,3);
            REVERSIBLE2x2 = findReversibles(level,2);
            cached = true;
        }

        for (CraftingRecipe recipe : REVERSIBLE3x3) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return true;
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return true;
            }
        }

        return false;
    }
    public static List<CraftingRecipe> findReversibles(ServerLevel level,int size) {
        List<CraftingRecipe> compactingRecipes = new ArrayList<>();
        List<CraftingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);

        for (CraftingRecipe recipe : recipes) {
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                int x = shapedRecipe.getWidth();
                int y = shapedRecipe.getHeight();
                if (x == size && x == y) {

                    List<Ingredient> inputs = shapedRecipe.getIngredients();

                    Ingredient first = inputs.get(0);
                    if (first != Ingredient.EMPTY) {
                        boolean same = true;
                        for (int i = 1; i < x * y;i++) {
                            Ingredient next = inputs.get(i);
                            if (next != first) {
                                same = false;
                                break;
                            }
                        }
                        if (same && shapedRecipe.getResultItem(level.registryAccess()).getCount() == 1) {
                            DUMMY.setItem(0,shapedRecipe.getResultItem(level.registryAccess()));

                            level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, DUMMY, level).ifPresent(rrecipe -> {
                                if (rrecipe.getResultItem(level.registryAccess()).getCount() == size * size) {
                                    compactingRecipes.add(shapedRecipe);
                                }
                            });
                        }
                    }
                }
            }
        }
        return compactingRecipes;
    }


    private static final CraftingContainer DUMMY = new CraftingContainer(null,1,1) {
        @Override
        public void setItem(int i, ItemStack itemStack) {
            ((CraftingContainerAccess)this).getItems().set(i, itemStack);
        }

        @Override
        public ItemStack removeItem(int i, int j) {
            return ContainerHelper.removeItem(((CraftingContainerAccess)this).getItems(), i, j);
        }
    };

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public static String formatLargeNumber(int number) {

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

}
