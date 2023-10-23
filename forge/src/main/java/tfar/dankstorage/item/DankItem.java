package tfar.dankstorage.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.container.PortableDankProvider;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.*;
import tfar.dankstorage.world.DankInventoryForge;

import javax.annotation.Nonnull;

public class DankItem extends CDankItem {

  public static final Rarity DARK_GRAY = Rarity.create("dark_gray", ChatFormatting.DARK_GRAY);
  public static final Rarity DARK_RED = Rarity.create("dark_red", ChatFormatting.DARK_RED);
  public static final Rarity GOLD = Rarity.create("gold", ChatFormatting.GOLD);
  public static final Rarity GREEN = Rarity.create("green", ChatFormatting.GREEN);
  public static final Rarity BLUE = Rarity.create("blue", ChatFormatting.AQUA);
  public static final Rarity DARK_PURPLE = Rarity.create("dark_purple", ChatFormatting.DARK_PURPLE);
  public static final Rarity WHITE = Rarity.create("white", ChatFormatting.WHITE);

    public DankItem(Properties $$0, DankStats stats) {
        super($$0, stats);
    }

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

    @Override
    public void inventoryTick(ItemStack bag, Level level, Entity entity, int i, boolean equipped) {
        //there has to be a better way
        if (entity instanceof ServerPlayer player && equipped) {
            ItemStack sel = Utils.getSelectedItem(bag,level);
            DankPacketHandler.sendSelectedItem(player, sel);
        }
    }

    @Override
    public MenuProvider createProvider(ItemStack stack) {
        return new PortableDankProvider(stack);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (Utils.getFrequency(stack)!= Utils.INVALID) {
            return new DankItemCapability(stack);
        }
        return super.initCapabilities(stack, nbt);
    }
}
