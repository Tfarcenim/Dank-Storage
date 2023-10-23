package tfar.dankstorage.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.menu.DankMenu;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.CDankSavedData;
import tfar.dankstorage.world.DankInventoryForge;

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

        DankInventoryForge dankInventoryForge = Utils.getInventory(stack,player.level());
        DankStats defaults = CommonUtils.getDefaultStats(stack);

        if (dankInventoryForge == null) {//create a new one
                int next = DankStorage.maxId.getMaxId();
                DankStorage.maxId.increment();
                Utils.getSettings(stack).putInt(Utils.FREQ,next);
                CDankSavedData dankSavedData = DankStorage.getData(next,player.level().getServer());
                dankSavedData.setStats(defaults,next);
                dankInventoryForge = (DankInventoryForge) dankSavedData.createFreshInventory(defaults,next);
        }

        DankStats type = dankInventoryForge.dankStats;

        if (defaults != type) {
            if (defaults.ordinal() < type.ordinal()) {//if the default stats are lower than what saveddata reports, abort opening
                Utils.warn(player, defaults, type);
                return null;
            }
            dankInventoryForge.upgradeTo(defaults);
        }

        return switch (defaults) {
            default -> DankMenu.t1s(i, playerInventory, dankInventoryForge);
            case two -> DankMenu.t2s(i, playerInventory, dankInventoryForge);
            case three -> DankMenu.t3s(i, playerInventory, dankInventoryForge);
            case four -> DankMenu.t4s(i, playerInventory, dankInventoryForge);
            case five -> DankMenu.t5s(i, playerInventory, dankInventoryForge);
            case six -> DankMenu.t6s(i, playerInventory, dankInventoryForge);
            case seven -> DankMenu.t7s(i, playerInventory, dankInventoryForge);
        };
    }
}
