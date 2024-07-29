package tfar.dankstorage.item;

import tfar.dankstorage.utils.DankStats;

public class DankItemNeoForge extends CDankItem {

   /* public static final Rarity DARK_GRAY = Rarity.create("dark_gray", ChatFormatting.DARK_GRAY);
    public static final Rarity DARK_RED = Rarity.create("dark_red", ChatFormatting.DARK_RED);
    public static final Rarity GOLD = Rarity.create("gold", ChatFormatting.GOLD);
    public static final Rarity GREEN = Rarity.create("green", ChatFormatting.GREEN);
    public static final Rarity BLUE = Rarity.create("blue", ChatFormatting.AQUA);
    public static final Rarity DARK_PURPLE = Rarity.create("dark_purple", ChatFormatting.DARK_PURPLE);
    public static final Rarity WHITE = Rarity.create("white", ChatFormatting.WHITE);*/

    public DankItemNeoForge(Properties $$0, DankStats stats) {
        super($$0, stats);
    }

   /* @Nonnull
    @Override
    public Rarity getRarity(ItemStack stack) {
        return switch (stats) {
            case DankStats.one -> DARK_GRAY;
            case DankStats.two -> DARK_RED;
            case DankStats.three -> GOLD;
            case DankStats.four -> GREEN;
            case DankStats.five -> BLUE;
            case DankStats.six -> DARK_PURPLE;
            case DankStats.seven -> WHITE;
            default -> super.getRarity(stack);
        };
    }*/
}
