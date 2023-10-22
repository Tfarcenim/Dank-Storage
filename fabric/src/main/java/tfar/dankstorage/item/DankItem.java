package tfar.dankstorage.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import tfar.dankstorage.container.PortableDankProvider;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.UseType;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventoryFabric;

import javax.annotation.Nonnull;

public class DankItem extends CoDankItem {

    public DankItem(Properties p_i48527_2_, DankStats stats) {
        super(p_i48527_2_,stats);
    }

  /*public static final Rarity GRAY = Rarity.create("dark_gray", Formatting.GRAY);
  public static final Rarity RED = Rarity.create("red", Formatting.RED);
  public static final Rarity GOLD = Rarity.create("gold", Formatting.GOLD);
  public static final Rarity GREEN = Rarity.create("green", Formatting.GREEN);
  public static final Rarity BLUE = Rarity.create("blue", Formatting.AQUA);
  public static final Rarity PURPLE = Rarity.create("purple", Formatting.DARK_PURPLE);
  public static final Rarity WHITE = Rarity.create("white", Formatting.WHITE);

  @Nonnull
  @Override
  public Rarity getRarity(ItemStack stack) {
    switch (tier) {
      case 1:
        return GRAY;
      case 2:
        return RED;
      case 3:
        return GOLD;
      case 4:
        return GREEN;
      case 5:
        return BLUE;
      case 6:
        return PURPLE;
      case 7:
        return WHITE;
    }
    return super.getRarity(stack);
  }*/

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bag = player.getItemInHand(hand);

            if (Utils.getUseType(bag) == UseType.bag) {
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
                        DankInventoryFabric handler = Utils.getInventory(bagCopy, level);
                        handler.setItem(Utils.getSelectedSlot(bagCopy), actionResult.getObject());
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
            DankInventoryFabric handler = Utils.getInventory(bag, player.level());
            handler.setItem(Utils.getSelectedSlot(bag), toUse);
        }

        player.setItemSlot(hand1, bag);
        return result;
    }



    @Override
    public void inventoryTick(ItemStack bag, Level level, Entity entity, int i, boolean equipped) {
        //there has to be a better way
        if (entity instanceof ServerPlayer player && equipped) {
            ItemStack sel = Utils.getSelectedItem(bag,level);
            DankPacketHandler.sendSelectedItem(player, sel);
        }
    }
}
