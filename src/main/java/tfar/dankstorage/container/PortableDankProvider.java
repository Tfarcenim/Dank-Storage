package tfar.dankstorage.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

import javax.annotation.Nullable;

public class PortableDankProvider implements MenuProvider {

    public final ItemStack stack;

    public PortableDankProvider(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public Component getDisplayName() {
        return stack.getHoverName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {

        InteractionHand hand = Utils.getHandWithDank(player);
        if (hand == null) return null;
        ItemStack bag = player.getItemInHand(hand);
        DankInventory dankInventory = Utils.getInventory(bag,player.level);
        DankStats type = Utils.getStats(bag);

        if (dankInventory == null) {
                int next = DankStorage.instance.data.getNextID();
                dankInventory = DankStorage.instance.data
                        .getOrCreateInventory(next,type);
                Utils.getSettings(bag).putInt(Utils.ID,next);
        } else if (type != dankInventory.dankStats) {
            if (type.ordinal() < dankInventory.dankStats.ordinal()) {
                Utils.warn(player, type, dankInventory.dankStats);
                return null;
            }
            dankInventory.upgradeTo(type);
        }

        return switch (type) {
            default -> DankMenu.t1s(i, playerInventory, dankInventory);
            case two -> DankMenu.t2s(i, playerInventory, dankInventory);
            case three -> DankMenu.t3s(i, playerInventory, dankInventory);
            case four -> DankMenu.t4s(i, playerInventory, dankInventory);
            case five -> DankMenu.t5s(i, playerInventory, dankInventory);
            case six -> DankMenu.t6s(i, playerInventory, dankInventory);
            case seven -> DankMenu.t7s(i, playerInventory, dankInventory);
        };
    }
}
