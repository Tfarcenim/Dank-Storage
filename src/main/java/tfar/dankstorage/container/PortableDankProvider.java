package tfar.dankstorage.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;
import tfar.dankstorage.world.DankSavedData;

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

        DankInventory dankInventory = Utils.getInventory(stack,player.level());
        DankStats defaults = Utils.getDefaultStats(stack);

        if (dankInventory == null) {//create a new one
                int next = DankStorage.instance.maxId.getMaxId();
                DankStorage.instance.maxId.increment();
                Utils.getSettings(stack).putInt(Utils.FREQ,next);
                DankSavedData dankSavedData = DankStorage.instance.getData(next);
                dankSavedData.setStats(defaults,next);
                dankInventory = dankSavedData.createFreshInventory(defaults,next);
        }

        DankStats type = dankInventory.dankStats;

        if (defaults != type) {
            if (defaults.ordinal() < type.ordinal()) {//if the default stats are lower than what saveddata reports, abort opening
                Utils.warn(player, defaults, type);
                return null;
            }
            dankInventory.upgradeTo(defaults);
        }

        return switch (defaults) {
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
