package tfar.dankstorage.client;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class CommonClient {

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof DankTooltip dankTooltip) {
            return new ClientDankTooltip(dankTooltip);
        }
        return null;
    }

}
