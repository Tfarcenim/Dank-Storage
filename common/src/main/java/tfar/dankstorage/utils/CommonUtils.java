package tfar.dankstorage.utils;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.init.ModDataComponents;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.inventory.LimitedContainerData;
import tfar.dankstorage.item.CDankItem;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.menu.ChangeFrequencyMenu;
import tfar.dankstorage.mixin.MinecraftServerAccess;
import tfar.dankstorage.network.client.S2CSyncSelectedDankItemPacket;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.world.ClientData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommonUtils {

    public static final int INVALID = -1;

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public static String formatLargeNumber(int number) {

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
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

    private static List<CraftingRecipe> REVERSIBLE3x3 = new ArrayList<>();
    private static List<CraftingRecipe> REVERSIBLE2x2 = new ArrayList<>();
    private static boolean cached = false;

    public static void uncacheRecipes() {
        cached = false;
    }

    public static Pair<ItemStack, Integer> compress(ItemStack stack, RegistryAccess registryAccess) {

        for (CraftingRecipe recipe : REVERSIBLE3x3) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(registryAccess), 9);
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return Pair.of(recipe.getResultItem(registryAccess), 4);
            }
        }
        return Pair.of(ItemStack.EMPTY, 0);
    }

    public static boolean canCompress(ServerLevel level, ItemStack stack) {
        if (!cached) {
            REVERSIBLE3x3 = findReversibles(level, 3);
            REVERSIBLE2x2 = findReversibles(level, 2);
            cached = true;
        }

        for (CraftingRecipe recipe : REVERSIBLE3x3) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return stack.getCount() >= 9;
            }
        }

        for (CraftingRecipe recipe : REVERSIBLE2x2) {
            if (recipe.getIngredients().get(0).test(stack)) {
                return stack.getCount() >= 4;
            }
        }

        return false;
    }


    public static void setPickSlot(Level level, ItemStack bag, ItemStack stack) {

        DankInterface dankInterface = getBagInventory(bag, level);

        if (dankInterface != null) {
            int slot = findSlotMatchingItem(dankInterface, stack);
            if (slot != INVALID) setSelectedSlot(bag, slot);
        }
    }

    public static int findSlotMatchingItem(DankInterface dankInventory, ItemStack itemStack) {
        for (int i = 0; i < dankInventory.getContainerSizeDank(); ++i) {
            ItemStack stack = dankInventory.getItemDank(i);
            if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(itemStack, stack)) continue;
            return i;
        }
        return INVALID;
    }

    public static void changeSelectedSlot(ItemStack bag, boolean right, ServerPlayer player) {
        DankInterface handler = getBagInventory(bag, player.serverLevel());
        //don't change slot if empty
        if (handler == null || handler.noValidSlots()) return;
        int selectedSlot = getSelectedSlot(bag);
        int size = handler.getContainerSizeDank();
        //keep iterating until a valid slot is found (not empty and not blacklisted from usage)
        if (right) {
            selectedSlot++;
            if (selectedSlot >= size) selectedSlot = 0;
        } else {
            selectedSlot--;
            if (selectedSlot < 0) selectedSlot = size - 1;
        }
        ItemStack selected = handler.getItemDank(selectedSlot);

        while (selected.isEmpty() || selected.is(ModTags.BLACKLISTED_USAGE)) {
            if (right) {
                selectedSlot++;
                if (selectedSlot >= size) selectedSlot = 0;
            } else {
                selectedSlot--;
                if (selectedSlot < 0) selectedSlot = size - 1;
            }
            selected = handler.getItemDank(selectedSlot);
        }
        if (selectedSlot != INVALID) {
            setSelectedSlot(bag, selectedSlot);
            Services.PLATFORM.sendToClient(new S2CSyncSelectedDankItemPacket(selected), player);
            player.displayClientMessage(selected.getHoverName(), true);
        }
    }

    public static List<CraftingRecipe> findReversibles(ServerLevel level, int size) {
        List<CraftingRecipe> compactingRecipes = new ArrayList<>();
        List<RecipeHolder<CraftingRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);

        for (RecipeHolder<CraftingRecipe> recipe : recipes) {
            if (recipe.value() instanceof ShapedRecipe shapedRecipe) {
                int x = shapedRecipe.getWidth();
                int y = shapedRecipe.getHeight();
                if (x == size && x == y) {

                    List<Ingredient> inputs = shapedRecipe.getIngredients();

                    Ingredient first = inputs.get(0);
                    if (first != Ingredient.EMPTY) {
                        boolean same = true;
                        for (int i = 1; i < x * y; i++) {
                            Ingredient next = inputs.get(i);
                            if (next != first) {
                                same = false;
                                break;
                            }
                        }
                        if (same && shapedRecipe.getResultItem(level.registryAccess()).getCount() == 1) {
                            ItemStack stack = shapedRecipe.getResultItem(level.registryAccess());

                            level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, makeCraftInput(stack), level).ifPresent(reverseRecipe -> {
                                if (reverseRecipe.value().getResultItem(level.registryAccess()).getCount() == size * size) {
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

    private static CraftingInput makeCraftInput(ItemStack stack) {
        return CraftingInput.of(1, 1, List.of(stack));
    }
    public static int getFrequency(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FREQUENCY, INVALID);
    }

    public static class DummyCraftingContainer implements CraftingContainer {
        private final NonNullList<ItemStack> items;
        private final int width;
        private final int height;

        public DummyCraftingContainer(int p_287629_, int p_287593_) {
            this(p_287629_, p_287593_, NonNullList.withSize(p_287629_ * p_287593_, ItemStack.EMPTY));
        }

        public DummyCraftingContainer(int p_287591_, int p_287609_, NonNullList<ItemStack> p_287695_) {
            this.items = p_287695_;
            this.width = p_287591_;
            this.height = p_287609_;
        }

        public int getContainerSize() {
            return this.items.size();
        }

        public boolean isEmpty() {
            for (ItemStack itemstack : this.items) {
                if (!itemstack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        public ItemStack getItem(int slot) {
            return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(slot);
        }

        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(this.items, slot);
        }

        public ItemStack removeItem(int i, int i1) {
            return ContainerHelper.removeItem(this.items, i, i1);
        }

        public void setItem(int slot, ItemStack stack) {
            this.items.set(slot, stack);
        }

        public void setChanged() {
        }

        public boolean stillValid(Player player) {
            return true;
        }

        public void clearContent() {
            this.items.clear();
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        public List<ItemStack> getItems() {
            return List.copyOf(this.items);
        }

        public void fillStackedContents(StackedContents contents) {
            for (ItemStack itemstack : this.items) {
                contents.accountSimpleStack(itemstack);
            }
        }
    }

    public static DankInterface getBagInventory(ItemStack bag, Level level) {
        if (!level.isClientSide) {
            int id = getFrequency(bag);
            if (id != INVALID) {
                Path path = ((MinecraftServerAccess) level.getServer()).getStorageSource()
                        .getDimensionPath(level.getServer().getLevel(Level.OVERWORLD).dimension())
                        .resolve("data/" + DankStorage.MODID + "/" + id + ".dat");

                if (path.toFile().isFile()) {
                    return DankStorage.getData(id, level.getServer()).createInventory(id);
                } else {
                    return DankStorage.getData(id, level.getServer()).createFreshInventory(getDefaultStats(bag), id);
                }
            } else {
                return null;
            }
        }
        throw new RuntimeException("Attempted to get inventory on client");
    }


    public static ItemStack getItemStackInSelectedSlot(ItemStack bag, ServerLevel level) {
        DankInterface inv = getBagInventory(bag, level);
        if (inv == null) return ItemStack.EMPTY;
        int slot = getSelectedSlot(bag);
        if (slot == INVALID) return ItemStack.EMPTY;
        ItemStack stack = inv.getItemDank(slot);
        return stack.is(ModTags.BLACKLISTED_USAGE) ? ItemStack.EMPTY : stack;
    }


    public static void merge(List<ItemStack> stacks, ItemStack toMerge) {
        for (ItemStack stack : stacks) {
            if (ItemStack.isSameItemSameComponents(stack, toMerge)) {
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

    public static PickupMode getPickupMode(ItemStack bag) {
        return bag.getOrDefault(ModDataComponents.PICKUP_MODE, PickupMode.none);
    }

    public static void setPickupMode(ItemStack bag, PickupMode mode) {
        bag.set(ModDataComponents.PICKUP_MODE, mode);
    }

    public static boolean isConstruction(ItemStack bag) {
        return getUseType(bag) == UseType.construction;
    }

    //0,1,2,3
    public static void cyclePickupMode(ItemStack bag, Player player) {
        int ordinal = getPickupMode(bag).ordinal();
        ordinal++;
        if (ordinal > PickupMode.VALUES.length - 1) ordinal = 0;
        PickupMode mode = PickupMode.VALUES[ordinal];
        setPickupMode(bag, mode);
        player.displayClientMessage(translatable("dankstorage.mode." + mode), true);
    }

    public static UseType getUseType(ItemStack bag) {
        return bag.getOrDefault(ModDataComponents.USE_TYPE, UseType.bag);
    }

    //0,1,2
    public static void cyclePlacement(ItemStack bag, Player player) {
        int ordinal = getUseType(bag).ordinal();
        ordinal++;
        if (ordinal >= UseType.VALUES.length) ordinal = 0;
        UseType useType = UseType.VALUES[ordinal];
        setUseType(bag, useType);
        player.displayClientMessage(translatable("dankstorage.usetype." + useType), true);
    }

    public static void setUseType(ItemStack bag, UseType useType) {
        bag.set(ModDataComponents.USE_TYPE, useType);
    }

    //this can be 0 - 80
    public static int getSelectedSlot(ItemStack bag) {
        return bag.getOrDefault(ModDataComponents.SELECTED, INVALID);
    }

    public static void setSelectedSlot(ItemStack bag, int slot) {
        bag.set(ModDataComponents.SELECTED, slot);
    }

    public static void setFrequency(ItemStack bag, int frequency) {
        bag.set(ModDataComponents.FREQUENCY, frequency);
    }

    public static MutableComponent translatable(String s) {
        return Component.translatable(s);
    }

    public static MutableComponent translatable(String string, Object... objects) {
        return Component.translatable(string, objects);
    }

    public static MutableComponent literal(String s) {
        return Component.literal(s);
    }

    public static boolean oredict(ItemStack bag) {
        return bag.getOrDefault(ModDataComponents.OREDICT, false);
    }

    public static void warn(Player player, DankStats item, DankStats inventory) {
        player.sendSystemMessage(literal("Dank Item Level " + item.ordinal() + " cannot open Dank Inventory Level " + inventory.ordinal()));
    }

    public static int getNbtSize(@Nullable CompoundTag nbt) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeNbt(nbt);
        buffer.release();
        return buffer.writerIndex();
    }

    public static List<ItemStackWrapper> wrap(List<ItemStack> stacks) {
        return stacks.stream().map(ItemStackWrapper::new).collect(Collectors.toList());
    }

    public static CDankItem getItemFromTier(int tier) {
        return (CDankItem) BuiltInRegistries.ITEM.get(DankStorage.id("dank_" + tier));
    }

    public static boolean isHoldingDank(@Nullable Player player) {

        if (player == null) return false;

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof CDankItem) return true;
        stack = player.getOffhandItem();
        return stack.getItem() instanceof CDankItem;
    }

    public static ItemStack getSelectedItem(ItemStack bag, Level level) {
        int selected = getSelectedSlot(bag);
        if (selected == INVALID) return ItemStack.EMPTY;
        if (!level.isClientSide) {
            DankInterface dankInventory = getBagInventory(bag, level);
            if (dankInventory != null) {
                return dankInventory.getItemDank(selected);
            } else {
                //    System.out.println("Attempted to access a selected item from a null inventory");
            }
        } else {
            return ClientData.selectedItem;
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    public static InteractionHand getHandWithDank(Player player) {
        if (player.getMainHandItem().getItem() instanceof CDankItem) return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() instanceof CDankItem) return InteractionHand.OFF_HAND;
        return null;
    }

    public static ItemStack getDank(Player player) {
        InteractionHand hand = getHandWithDank(player);
        return hand == null ? ItemStack.EMPTY : player.getItemInHand(hand);
    }

    public static void toggleTagMode(ServerPlayer player) {
        ItemStack dank = getDank(player);
        if (!dank.isEmpty()) {
            boolean toggle = oredict(dank);
            setOredictMode(dank,!toggle);
        }
    }

    public static void setOredictMode(ItemStack bag,boolean active) {
        bag.set(ModDataComponents.OREDICT,active);
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
            cyclePlacement(dank, player);
        }
    }

    public static void setTxtColor(ServerPlayer player, int frequency, boolean set) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu abstractDankMenu) {
            DankInterface inventory = abstractDankMenu.dankInventory;

            TxtColor textColor;

            if (frequency > INVALID) {
                if (frequency < DankStorage.maxId.getMaxId()) {
                    DankInterface targetInventory = DankStorage.getData(frequency, player.server).createInventory(frequency);

                    if (targetInventory.valid() && targetInventory.getDankStats() == inventory.getDankStats()) {

                        if (targetInventory.frequencyLocked()) {
                            textColor = TxtColor.LOCKED;
                        } else {
                            textColor = TxtColor.GOOD;
                            if (set) {
                                abstractDankMenu.setFrequency(frequency);
                                player.closeContainer();
                            }
                        }
                    } else {
                        textColor = TxtColor.DIFFERENT_TIER;
                    }
                } else {
                    //orange if it doesn't exist, yellow if it does but wrong tier
                    textColor = TxtColor.TOO_HIGH;
                }
            } else {
                textColor = TxtColor.INVALID;
            }
            inventory.setTextColor(textColor.color);
        } else if (container instanceof ChangeFrequencyMenu changeFrequencyMenu) {
            DankInterface inventory = (DankInterface) ((LimitedContainerData) changeFrequencyMenu.getContainerData()).getWrapped();

            TxtColor textColor;

            if (frequency > INVALID) {
                if (frequency < DankStorage.maxId.getMaxId()) {
                    DankInterface targetInventory = DankStorage.getData(frequency, player.server).createInventory(frequency);

                    if (targetInventory.valid() && targetInventory.getDankStats() == DankStats.values()[changeFrequencyMenu.getCurrentTier()]) {

                        if (targetInventory.frequencyLocked()) {
                            textColor = TxtColor.LOCKED;
                        } else {
                            textColor = TxtColor.GOOD;
                            if (set) {
                                changeFrequencyMenu.setLinkedFrequency(frequency);
                                player.closeContainer();
                            }
                        }
                    } else {
                        textColor = TxtColor.DIFFERENT_TIER;
                    }
                } else {
                    //orange if it doesn't exist, yellow if it does but wrong tier
                    textColor = TxtColor.TOO_HIGH;
                }
            } else {
                textColor = TxtColor.INVALID;
            }
            inventory.setTextColor(textColor.color);
        }
    }

    public static DankStats getDefaultStats(ItemStack bag) {
        return ((CDankItem) bag.getItem()).stats;
    }

    @Nonnull
    public static ItemStack copyStackWithSize(@Nonnull ItemStack itemStack, int size) {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
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
