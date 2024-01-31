package tfar.dankstorage.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.client.DankKeybinds;
import tfar.dankstorage.client.DankTooltip;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.menu.PortableDankProvider;
import tfar.dankstorage.mixin.ItemUsageContextAccessor;
import tfar.dankstorage.network.PacketIds;
import tfar.dankstorage.network.client.S2CSyncSelectedDankItemPacket;
import tfar.dankstorage.network.server.C2SRequestContentsPacket;
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

public class CDankItem extends Item {

    public final DankStats stats;
    public CDankItem(Properties $$0, DankStats stats) {
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
                    CommonUtils.translatable("text.dankstorage.current_pickup_mode", pickupMode.translate().withStyle(ChatFormatting.YELLOW))
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

    //this is called on the client
    @Override
    public InteractionResult interactLivingEntity(ItemStack bag, Player player, LivingEntity entity, InteractionHand hand) {
        if (!CommonUtils.isConstruction(bag)) return InteractionResult.PASS;

        ItemStack toUse = CommonUtils.getSelectedItem(bag,player.level());
        EquipmentSlot hand1 = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        player.setItemSlot(hand1, toUse);
        InteractionResult result = toUse.getItem().interactLivingEntity(toUse, player, entity, hand);

        //the client doesn't have access to the full inventory
        if (!player.level().isClientSide) {
            DankInterface handler = CommonUtils.getBagInventory(bag, player.level());
            handler.setItemDank(CommonUtils.getSelectedSlot(bag), toUse);
        }

        player.setItemSlot(hand1, bag);
        return result;
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && CommonUtils.getPickupMode(stack) != PickupMode.none;
    }

    public MenuProvider createProvider(ItemStack stack) {
        return new PortableDankProvider(stack);
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
            DankInterface dankInventory = CommonUtils.getBagInventory(bag,level);
            dankInventory.setItemDank(selectedSlot, ctx2.getItemInHand());
        }
        return actionResultType;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bag = player.getItemInHand(hand);

        if (CommonUtils.getUseType(bag) == UseType.bag) {
            if (!level.isClientSide) {
                assignNextId(bag);
                player.openMenu(createProvider(bag));
            }
            return InteractionResultHolder.success(bag);
        } else {
            if (!level.isClientSide) {
                ItemStack toPlace = CommonUtils.getItemStackInSelectedSlot(bag, (ServerLevel) level);
                EquipmentSlot hand1 = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                //handle empty
                if (toPlace.isEmpty()) {
                    return InteractionResultHolder.pass(bag);
                }

                //handle food
                if (toPlace.getItem().isEdible()) {
                    if (player.canEat(false)) {
                        player.startUsingItem(hand);
                        return InteractionResultHolder.consume(bag);
                    }
                }
                //handle potion
                else if (toPlace.getItem() instanceof PotionItem) {
                    player.startUsingItem(hand);
                    return InteractionResultHolder.success(player.getItemInHand(hand));
                }

                //handle shield
                else if (toPlace.getItem() instanceof ShieldItem) {
                    player.startUsingItem(hand);
                    return InteractionResultHolder.success(player.getItemInHand(hand));
                }

                //todo support other items?
                else {
                    ItemStack bagCopy = bag.copy();
                    player.setItemSlot(hand1, toPlace);
                    InteractionResultHolder<ItemStack> actionResult = toPlace.getItem().use(level, player, hand);
                    DankInterface handler = CommonUtils.getBagInventory(bagCopy, level);
                    handler.setItemDank(CommonUtils.getSelectedSlot(bagCopy), actionResult.getObject());
                    player.setItemSlot(hand1, bagCopy);
                }
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
        }
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
               C2SRequestContentsPacket.send(id);
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

    @Override
    public void inventoryTick(ItemStack bag, Level level, Entity entity, int i, boolean equipped) {
        //there has to be a better way
        if (entity instanceof ServerPlayer player && equipped) {
            ItemStack sel = CommonUtils.getSelectedItem(bag,level);
            Services.PLATFORM.sendToClient(new S2CSyncSelectedDankItemPacket(sel), PacketIds.sync_selected_dank_item, player);
        }
    }
}
