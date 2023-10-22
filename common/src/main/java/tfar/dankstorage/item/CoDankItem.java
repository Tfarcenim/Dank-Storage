package tfar.dankstorage.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.client.DankKeybinds;
import tfar.dankstorage.client.DankTooltip;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.mixin.ItemUsageContextAccessor;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.UseType;
import tfar.dankstorage.world.ClientData;
import tfar.dankstorage.world.MaxId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public abstract class CoDankItem extends Item {

    public final DankStats stats;
    public CoDankItem(Properties $$0, DankStats stats) {
        super($$0);
        this.stats = stats;
    }

    @Override
    public void appendHoverText(ItemStack bag, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (bag.hasTag()) {

            if (Services.PLATFORM.isDevelopmentEnvironment()) {
                String s = bag.getTag().toString();

                List<String> bits = new ArrayList<>();

                int length = s.length();

                if (s.length() > 10000) return;

                int itr = (int) Math.ceil(length / 40d);

                for (int i = 0; i < itr; i++) {

                    int end = (i + 1) * 40;

                    if ((i + 1) * 40 - 1 >= length) {
                        end = length;
                    }

                    String s1 = s.substring(i * 40, end);
                    bits.add(s1);
                }

                bits.forEach(s1 -> tooltip.add(CommonUtils.literal(s1)));

            }
        }

        int id = CommonUtils.getFrequency(bag);
        tooltip.add(CommonUtils.literal("ID: "+id));

        if (!Screen.hasShiftDown()) {
            tooltip.add(CommonUtils.translatable("text.dankstorage.shift",
                    CommonUtils.literal("Shift").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
        } else {

            tooltip.add(CommonUtils.translatable("text.dankstorage.change_pickup_mode", DankKeybinds.PICKUP_MODE.getTranslatedKeyMessage().copy()
                    .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
            PickupMode pickupMode = CommonUtils.getPickupMode(bag);
            tooltip.add(
                    CommonUtils.translatable("text.dankstorage.current_pickup_mode", CommonUtils.translatable(
                                    "dankstorage.mode." + pickupMode.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.YELLOW))
                            .withStyle(ChatFormatting.GRAY));


            tooltip.add(CommonUtils.translatable("text.dankstorage.changeusetype", DankKeybinds.CONSTRUCTION.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
            UseType useType = CommonUtils.getUseType(bag);
            tooltip.add(
                    CommonUtils.translatable("text.dankstorage.currentusetype", CommonUtils.translatable(
                            "dankstorage.usetype." + useType.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
            tooltip.add(
                    CommonUtils.translatable("text.dankstorage.stacklimit", CommonUtils.literal(stats.stacklimit + "").withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && CommonUtils.getPickupMode(stack) != PickupMode.none;
    }

    public int getGlintColor(ItemStack stack) {
        PickupMode pickupMode = CommonUtils.getPickupMode(stack);
        switch (pickupMode) {
            case none:
            default:
                return 0xffffffff;
            case pickup_all:
                return 0xff00ff00;
            case filtered_pickup:
                return 0xffffff00;
            case void_pickup:
                return 0xffff0000;
        }
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack bag = ctx.getItemInHand();
        Level level = ctx.getLevel();
        UseType useType = CommonUtils.getUseType(bag);

        if (useType == UseType.bag) {
            return InteractionResult.PASS;
        }

        int selectedSlot = CommonUtils.getSelectedSlot(bag);

        //invalid slot
        if (selectedSlot == CommonUtils.INVALID) {
            return InteractionResult.PASS;
        }

        ItemStack toPlace = CommonUtils.getSelectedItem(bag,level);
        //todo: sync locked slots to client?
        if (/*toPlace.getCount() == 1 && handler.isLocked(selectedSlot)*/ false)
            return InteractionResult.PASS;

        UseOnContext ctx2 = new UseOnContext2(ctx.getLevel(), ctx.getPlayer(), ctx.getHand(), toPlace, ((ItemUsageContextAccessor) ctx).getHitResult());
        InteractionResult actionResultType = toPlace.getItem().useOn(ctx2);//ctx2.getItem().onItemUse(ctx);
        if (!level.isClientSide) {
            DankInterface dankInventory = Services.PLATFORM.getInventoryCommon(bag,level);
            dankInventory.setItemDank(selectedSlot, ctx2.getItemInHand());
        }
        return actionResultType;
    }

    static class UseOnContext2 extends UseOnContext {

        protected UseOnContext2(Level $$0, @org.jetbrains.annotations.Nullable Player $$1, InteractionHand $$2, ItemStack $$3, BlockHitResult $$4) {
            super($$0, $$1, $$2, $$3, $$4);
        }
    }

    private static final ThreadLocal<Integer> cache = ThreadLocal.withInitial(() -> CommonUtils.INVALID);

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {

        int id = CommonUtils.getFrequency(itemStack);

        if (id > CommonUtils.INVALID) {
            //don't spam the server with requests
            if (cache.get() != id || true) {
               Services.PLATFORM.sendRequestContentsPacket(id);
                cache.set(id);
            }

            if (ClientData.cachedItems != null) {
                NonNullList<ItemStack> nonNullList = NonNullList.create();
                nonNullList.addAll(ClientData.cachedItems);
                return Optional.of(new DankTooltip(nonNullList, CommonUtils.getSelectedSlot(itemStack)));
            }
        }
        return Optional.empty();
    }

    public static void assignNextId(ItemStack dank) {
        CompoundTag settings = CommonUtils.getSettings(dank);
        if (settings == null || !settings.contains(CommonUtils.FREQ, Tag.TAG_INT)) {
            MaxId maxId = DankStorage.maxId;
            int next = maxId.getMaxId();
            maxId.increment();
            CommonUtils.getOrCreateSettings(dank).putInt(CommonUtils.FREQ,next);
        }
    }


}
