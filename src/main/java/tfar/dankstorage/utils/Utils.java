package tfar.dankstorage.utils;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.mixin.MinecraftServerAccess;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.world.ClientData;
import tfar.dankstorage.world.DankInventory;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static tfar.dankstorage.DankStorage.MODID;
import static tfar.dankstorage.ModTags.BLACKLISTED_USAGE;

public class Utils {
    private static TagKey<Item> bind(ResourceLocation string) {
        return TagKey.create(Registries.ITEM, string);
    }

    public static final String FREQ = "dankstorage:frequency";
    public static final Set<ResourceLocation> taglist = new HashSet<>();
    public static boolean DEV = false;//FabricLoader.getInstance().isDevelopmentEnvironment();
    public static final int INVALID = -1;

    public static final String SET = "settings";
    public static final String SELECTED = "selectedSlot";
    public static final String CON = "construction";
    public static final String MODE = "mode";

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
            return PickupMode.PICKUP_MODES[tag.getInt(MODE)];
        }
        return PickupMode.none;
    }

    public static void setPickupMode(ItemStack bag, PickupMode mode) {
        CompoundTag tag = getOrCreateSettings(bag);
        tag.putInt(MODE,mode.ordinal());
    }

    public static boolean isConstruction(ItemStack bag) {
        CompoundTag settings = getSettings(bag);
        return settings != null && settings.getInt(CON) == UseType.construction.ordinal();
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
        int ordinal = getOrCreateSettings(bag).getInt(MODE);
        ordinal++;
        if (ordinal > PickupMode.PICKUP_MODES.length - 1) ordinal = 0;
        getOrCreateSettings(bag).putInt(MODE, ordinal);
        player.displayClientMessage(
                translatable("dankstorage.mode." + PickupMode.PICKUP_MODES[ordinal].name()), true);
    }

    public static UseType getUseType(ItemStack bag) {
        CompoundTag settings = getSettings(bag);
        return settings != null ? useTypes[settings.getInt(CON)] : UseType.bag;
    }

    //0,1,2
    private static void cyclePlacement(ItemStack bag, Player player) {
        CompoundTag tag = getOrCreateSettings(bag);
        int ordinal = tag.getInt(CON);
        ordinal++;
        if (ordinal >= useTypes.length) ordinal = 0;
        tag.putInt(CON, ordinal);
        player.displayClientMessage(
                translatable("dankstorage.usetype." + useTypes[ordinal].name()), true);
    }

    //this can be 0 - 80
    public static int getSelectedSlot(ItemStack bag) {
        CompoundTag settings = getSettings(bag);
        return settings != null && settings.contains(SELECTED) ? settings.getInt(SELECTED) : INVALID;
    }

    public static void setSelectedSlot(ItemStack bag, int slot) {
        getOrCreateSettings(bag).putInt(SELECTED,slot);
    }

    public static void setPickSlot(Level level,ItemStack bag, ItemStack stack) {

        DankInventory dankInventory = getInventory(bag,level);

        if (dankInventory != null) {
            int slot = findSlotMatchingItem(dankInventory, stack);
            if (slot != INVALID) setSelectedSlot(bag, slot);
        }
    }

    public static int findSlotMatchingItem(DankInventory dankInventory,ItemStack itemStack) {
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

    public static DankStats getDefaultStats(ItemStack bag) {
        return ((DankItem) bag.getItem()).stats;
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

        while (selected.isEmpty() || selected.is(BLACKLISTED_USAGE)) {
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

    //make sure to return an invalid ID for unassigned danks
    public static int getFrequency(ItemStack bag) {
        CompoundTag settings = getSettings(bag);
        if (settings != null && settings.contains(FREQ)) {
            return settings.getInt(FREQ);
        }
        return INVALID;
    }

    public static void setFrequency(ItemStack bag,int frequency) {
        getOrCreateSettings(bag).putInt(FREQ,frequency);
    }

    private static boolean hasSettings(ItemStack bag) {
        return bag.hasTag() && bag.getTag().contains(SET);
    }

    public static boolean oredict(ItemStack bag) {
        return bag.getItem() instanceof DankItem && bag.hasTag() && getSettings(bag).getBoolean("tag");
    }

    public static DankInventory getInventory(ItemStack bag, Level level) {
        if (!level.isClientSide) {
            int id = getFrequency(bag);
            if (id != INVALID) {

                Path path = ((MinecraftServerAccess)level.getServer()).getStorageSource()
                        .getDimensionPath(level.getServer().getLevel(Level.OVERWORLD).dimension())
                        .resolve("data/"+MODID+"/"+id+".dat");

                if (path.toFile().isFile()) {
                    return DankStorage.instance.getData(id).createInventory(id);
                } else {
                    return DankStorage.instance.getData(id).createFreshInventory(getDefaultStats(bag),id);
                }
            } else {
                return null;
            }
        }
        throw new RuntimeException("Attempted to get inventory on client");
    }

    public static int getNbtSize(ItemStack stack) {
        return getNbtSize(stack.getTag());
    }

    public static DankItem getItemFromTier(int tier) {
        return (DankItem) BuiltInRegistries.ITEM.get(new ResourceLocation(MODID, "dank_" + tier));
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
        int slot = getSelectedSlot(bag);
        if (slot == INVALID) return ItemStack.EMPTY;
        ItemStack stack = inv.getStackInSlot(slot);
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

    public static boolean isHoldingDank(@Nullable Player player) {

        if (player == null) return false;

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
            return false;//ItemStack.tagMatches(first, second);
        }
    }

    public static void warn(Player player, DankStats item, DankStats inventory) {
        player.sendSystemMessage(literal("Dank Item Level "+item.ordinal() +" cannot open Dank Inventory Level "+inventory.ordinal()));
    }

    @Nullable
    private static InteractionHand getHandWithDank(Player player) {
        if (player.getMainHandItem().getItem() instanceof DankItem) return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() instanceof DankItem) return InteractionHand.OFF_HAND;
        return null;
    }

    public static ItemStack getDank(Player player) {
        InteractionHand hand = getHandWithDank(player);
        return hand == null ? ItemStack.EMPTY : player.getItemInHand(hand);
    }

    private static List<CraftingRecipe> REVERSIBLE3x3 = new ArrayList<>();
    private static List<CraftingRecipe> REVERSIBLE2x2 = new ArrayList<>();
    private static boolean cached = false;

    public static void uncacheRecipes(RecipeManager manager) {
        cached = false;
    }

    public static Pair<ItemStack,Integer> compress(ItemStack stack, RegistryAccess registryAccess) {

        for (CraftingRecipe recipe : REVERSIBLE3x3) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(registryAccess),9);
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(registryAccess),4);
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
                return stack.getCount() >=9;
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return stack.getCount()>=4;
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

                            level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, DUMMY, level).ifPresent(reverseRecipe -> {
                                if (reverseRecipe.getResultItem(level.registryAccess()).getCount() == size * size) {
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


    @SuppressWarnings("ConstantConditions")
    private static final CraftingContainer DUMMY = new TransientCraftingContainer(null,1,1) {
        @Override
        public void setItem(int i, ItemStack itemStack) {
            getItems().set(i, itemStack);
        }

        @Override
        public ItemStack removeItem(int i, int j) {
            return ContainerHelper.removeItem(getItems(), i, j);
        }
    };

    public static MutableComponent translatable(String s) {
        return Component.translatable(s);
    }

    public static MutableComponent translatable(String string, Object... objects) {
        return Component.translatable(string, objects);
    }

    public static MutableComponent literal(String s) {
        return Component.literal(s);
    }

    public static void toggleTagMode(ServerPlayer player) {
        ItemStack dank = getDank(player);
        if (!dank.isEmpty()) {
            boolean toggle = oredict(dank);
            player.getMainHandItem().getOrCreateTag().putBoolean("tag", !toggle);
        }
    }

    public static void togglePickupMode(ServerPlayer player) {
        ItemStack bag = getDank(player);
        if (!bag.isEmpty()) {
            cyclePickupMode(bag, player);
        }
    }

    public static void toggleUseType(ServerPlayer player) {
        ItemStack dank = getDank(player);
        if (!dank.isEmpty()) {
            cyclePlacement(dank,player);
        }
    }

    public static final UseType[] useTypes = UseType.values();
    public enum UseType {
        bag, construction
    }
}
