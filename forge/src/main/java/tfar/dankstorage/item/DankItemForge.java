package tfar.dankstorage.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.utils.*;

import javax.annotation.Nonnull;

public class DankItemForge extends CDankItem {

    public static final Rarity DARK_GRAY = Rarity.create("dark_gray", ChatFormatting.DARK_GRAY);
    public static final Rarity DARK_RED = Rarity.create("dark_red", ChatFormatting.DARK_RED);
    public static final Rarity GOLD = Rarity.create("gold", ChatFormatting.GOLD);
    public static final Rarity GREEN = Rarity.create("green", ChatFormatting.GREEN);
    public static final Rarity BLUE = Rarity.create("blue", ChatFormatting.AQUA);
    public static final Rarity DARK_PURPLE = Rarity.create("dark_purple", ChatFormatting.DARK_PURPLE);
    public static final Rarity WHITE = Rarity.create("white", ChatFormatting.WHITE);

    public DankItemForge(Properties $$0, DankStats stats) {
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
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (CommonUtils.getFrequency(stack) > CommonUtils.INVALID) {
            return new DankItemCapability(stack);
        }
        return super.initCapabilities(stack, nbt);
    }
}
