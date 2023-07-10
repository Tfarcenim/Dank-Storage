package tfar.dankstorage.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.client.DankTooltip;
import tfar.dankstorage.container.PortableDankProvider;
import tfar.dankstorage.mixin.ItemUsageContextAccessor;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.server.C2SRequestContentsPacket;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.ClientData;
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.world.DankSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DankItem extends Item {
    public final DankStats stats;

    public DankItem(Properties p_i48527_2_, DankStats stats) {
        super(p_i48527_2_);
        this.stats = stats;
    }

  public static final Rarity DARK_GRAY = Rarity.create("dark_gray", ChatFormatting.DARK_GRAY);
  public static final Rarity DARK_RED = Rarity.create("dark_red", ChatFormatting.DARK_RED);
  public static final Rarity GOLD = Rarity.create("gold", ChatFormatting.GOLD);
  public static final Rarity GREEN = Rarity.create("green", ChatFormatting.GREEN);
  public static final Rarity BLUE = Rarity.create("blue", ChatFormatting.AQUA);
  public static final Rarity DARK_PURPLE = Rarity.create("dark_purple", ChatFormatting.DARK_PURPLE);
  public static final Rarity WHITE = Rarity.create("white", ChatFormatting.WHITE);

  @Nonnull
  @Override
  public Rarity getRarity(ItemStack stack) {
      return switch (stats) {
          case one -> DARK_GRAY;
          case two -> DARK_RED;
          case three -> GOLD;
          case four -> GREEN;
          case five -> BLUE;
          case six -> DARK_PURPLE;
          case seven -> WHITE;
          default -> super.getRarity(stack);
      };
  }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bag = player.getItemInHand(hand);

            if (Utils.getUseType(bag) == Utils.UseType.bag) {
                if (!level.isClientSide) {
                    assignNextId(bag);
                    player.openMenu(new PortableDankProvider(bag));
                }
                return InteractionResultHolder.success(bag);
            } else {
                if (!level.isClientSide) {
                    ItemStack toPlace = Utils.getItemStackInSelectedSlot(bag, (ServerLevel) level);
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
                        DankInventory handler = Utils.getOrCreateInventory(bagCopy, level);
                        handler.setStackInSlot(Utils.getSelectedSlot(bagCopy), actionResult.getObject());
                        player.setItemSlot(hand1, bagCopy);
                    }
                }
                return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
            }
    }

    //this is called on the client
    @Override
    public InteractionResult interactLivingEntity(ItemStack bag, Player player, LivingEntity entity, InteractionHand hand) {
        if (!Utils.isConstruction(bag)) return InteractionResult.PASS;

        ItemStack toUse = Utils.getSelectedItem(bag,player.level());
        EquipmentSlot hand1 = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        player.setItemSlot(hand1, toUse);
        InteractionResult result = toUse.getItem().interactLivingEntity(toUse, player, entity, hand);

        //the client doesn't have access to the full inventory
        if (!player.level().isClientSide) {
            DankInventory handler = Utils.getOrCreateInventory(bag, player.level());
            handler.setStackInSlot(Utils.getSelectedSlot(bag), toUse);
        }

        player.setItemSlot(hand1, bag);
        return result;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && Utils.getPickupMode(stack) != PickupMode.none;
    }

    @Override
    public void appendHoverText(ItemStack bag, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (bag.hasTag()) {

            if (Utils.DEV) {
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

                bits.forEach(s1 -> tooltip.add(Utils.literal(s1)));

            }
        }

        int id = Utils.getFrequency(bag);
        tooltip.add(Utils.literal("ID: "+id));

        if (!Screen.hasShiftDown()) {
            tooltip.add(Utils.translatable("text.dankstorage.shift",
                    Utils.literal("Shift").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
        } else {

            tooltip.add(Utils.translatable("text.dankstorage.change_pickup_mode", Client.PICKUP_MODE.getTranslatedKeyMessage().copy()
                    .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
            PickupMode pickupMode = Utils.getPickupMode(bag);
            tooltip.add(
                    Utils.translatable("text.dankstorage.current_pickup_mode", Utils.translatable(
                            "dankstorage.mode." + pickupMode.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.YELLOW))
                            .withStyle(ChatFormatting.GRAY));


            tooltip.add(Utils.translatable("text.dankstorage.changeusetype", Client.CONSTRUCTION.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
            Utils.UseType useType = Utils.getUseType(bag);
            tooltip.add(
                    Utils.translatable("text.dankstorage.currentusetype", Utils.translatable(
                            "dankstorage.usetype." + useType.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
            tooltip.add(
                    Utils.translatable("text.dankstorage.stacklimit", Utils.literal(stats.stacklimit + "").withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));
        }
    }

    public int getGlintColor(ItemStack stack) {
        PickupMode pickupMode = Utils.getPickupMode(stack);
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
        Utils.UseType useType = Utils.getUseType(bag);

        if (useType == Utils.UseType.bag) {
            return InteractionResult.PASS;
        }

        int selectedSlot = Utils.getSelectedSlot(bag);

        //invalid slot
        if (selectedSlot == Utils.INVALID) {
            return InteractionResult.PASS;
        }

        ItemStack toPlace = Utils.getSelectedItem(bag,level);
        //todo: sync locked slots to client?
        if (/*toPlace.getCount() == 1 && handler.isLocked(selectedSlot)*/ false)
            return InteractionResult.PASS;

        UseOnContext ctx2 = new ItemUseContextExt(ctx.getLevel(), ctx.getPlayer(), ctx.getHand(), toPlace, ((ItemUsageContextAccessor) ctx).getHitResult());
        InteractionResult actionResultType = toPlace.getItem().useOn(ctx2);//ctx2.getItem().onItemUse(ctx);
        if (!level.isClientSide) {
            DankInventory dankInventory = Utils.getInventory(bag,level);
            dankInventory.setStackInSlot(selectedSlot, ctx2.getItemInHand());
        }
        return actionResultType;
    }

    @Override
    public void inventoryTick(ItemStack bag, Level level, Entity entity, int i, boolean equipped) {
        //there has to be a better way
        if (entity instanceof ServerPlayer player && equipped) {
            ItemStack sel = Utils.getSelectedItem(bag,level);
            DankPacketHandler.sendSelectedItem(player, sel);
        }
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    public static class ItemUseContextExt extends UseOnContext {
        protected ItemUseContextExt(Level level, @Nullable Player player, InteractionHand hand, ItemStack stack, BlockHitResult result) {
            super(level, player, hand, stack, result);
        }
    }

    public static void assignNextId(ItemStack dank) {
        CompoundTag settings = Utils.getSettings(dank);
        if (settings == null || !settings.contains(Utils.ID, Tag.TAG_INT)) {
            DankSavedData dankSavedData = DankStorage.instance.data;
            DankStats stats = Utils.getStats(dank);
            int next = dankSavedData.getNextID();
            dankSavedData.getOrCreateInventory(next,stats);
            Utils.getOrCreateSettings(dank).putInt(Utils.ID,next);
        }
    }

    private static final ThreadLocal<Integer> cache = ThreadLocal.withInitial(() -> Utils.INVALID);

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {

        int id = Utils.getFrequency(itemStack);

        if (id > Utils.INVALID) {
            //don't spam the server with requests
            C2SRequestContentsPacket.send(id);
            cache.set(id);

            if (ClientData.cachedItems != null) {
                return Optional.of(new DankTooltip(ClientData.cachedItems,Utils.getSelectedSlot(itemStack)));
            }
        }
        return Optional.empty();
    }

    @Override
    public @org.jetbrains.annotations.Nullable ICapabilityProvider initCapabilities(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundTag nbt) {
        if (Utils.getFrequency(stack)!= Utils.INVALID) {
            return new DankItemCapability(stack);
        }
        return super.initCapabilities(stack, nbt);
    }
}
