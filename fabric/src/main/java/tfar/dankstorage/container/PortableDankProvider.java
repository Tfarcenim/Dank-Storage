package tfar.dankstorage.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventoryFabric;
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

        DankInventoryFabric dankInventoryFabric = Utils.getInventory(stack,player.level());
        DankStats defaults = CommonUtils.getDefaultStats(stack);

        if (dankInventoryFabric == null) {//create a new one
            int next = DankStorage.maxId.getMaxId();
            DankStorage.maxId.increment();
            CommonUtils.getSettings(stack).putInt(Utils.FREQ,next);
            DankSavedData dankSavedData = DankStorageFabric.getData(next,player.level().getServer());
            dankSavedData.setStats(defaults,next);
            dankInventoryFabric = dankSavedData.createFreshInventory(defaults,next);
        }

        DankStats type = dankInventoryFabric.dankStats;

        if (defaults != type) {
            if (defaults.ordinal() < type.ordinal()) {//if the default stats are lower than what saveddata reports, abort opening
                CommonUtils.warn(player, defaults, type);
                return null;
            }
            dankInventoryFabric.upgradeTo(defaults);
        }

        return switch (defaults) {
            default -> DankMenu.t1s(i, playerInventory, dankInventoryFabric);
            case two -> DankMenu.t2s(i, playerInventory, dankInventoryFabric);
            case three -> DankMenu.t3s(i, playerInventory, dankInventoryFabric);
            case four -> DankMenu.t4s(i, playerInventory, dankInventoryFabric);
            case five -> DankMenu.t5s(i, playerInventory, dankInventoryFabric);
            case six -> DankMenu.t6s(i, playerInventory, dankInventoryFabric);
            case seven -> DankMenu.t7s(i, playerInventory, dankInventoryFabric);
        };
    }
}
