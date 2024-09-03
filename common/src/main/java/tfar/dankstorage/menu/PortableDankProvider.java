package tfar.dankstorage.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.LimitedContainerData;
import tfar.dankstorage.inventory.TierDataSlot;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankSavedData;

import javax.annotation.Nullable;

public record PortableDankProvider(ItemStack stack) implements MenuProvider {

    @Override
    public Component getDisplayName() {
        return stack.getHoverName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        DankStats stats = CommonUtils.getDefaultStats(stack);
        MinecraftServer server = player.getServer();
        if (DankItem.getFrequency(stack) == CommonUtils.INVALID) {
            DankItem.assignNextFreeId(server,stack);
            DankSavedData tankSavedData = DankSavedData.getOrCreate(DankItem.getFrequency(stack),server);
            tankSavedData.setStats(stats);
        }


        DankInventory dankInventory = DankItem.getInventoryFrom(stack, player.getServer());
        int defaults = stats.slots;


        int type = dankInventory.slotCount();

        if (defaults != type) {
            if (defaults < type) {//if the default stats are lower than what saveddata reports, abort opening
                return new ChangeFrequencyMenu(i, playerInventory, new LimitedContainerData(dankInventory, 3), new TierDataSlot(stats), stack);
                //CommonUtils.warn(player, defaults, type);
                //return null;
            }
            dankInventory.upgradeTo(stats);
        }

        return switch (stats) {
            default -> DankMenu.t1s(i, playerInventory, dankInventory,stack);
            case two -> DankMenu.t2s(i, playerInventory, dankInventory,stack);
            case three -> DankMenu.t3s(i, playerInventory, dankInventory,stack);
            case four -> DankMenu.t4s(i, playerInventory, dankInventory,stack);
            case five -> DankMenu.t5s(i, playerInventory, dankInventory,stack);
            case six -> DankMenu.t6s(i, playerInventory, dankInventory,stack);
            case seven -> DankMenu.t7s(i, playerInventory, dankInventory,stack);
        };
    }
}
