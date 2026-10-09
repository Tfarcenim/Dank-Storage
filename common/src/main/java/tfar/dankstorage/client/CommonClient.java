package tfar.dankstorage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import tfar.dankstorage.client.screens.DankScreen;
import tfar.dankstorage.client.screens.ChangeFrequencyScreen;
import tfar.dankstorage.init.ModMenuTypes;

public class CommonClient {

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof DankTooltip dankTooltip) {
            return new ClientDankTooltip(dankTooltip);
        }
        return null;
    }

    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void setup() {
        MenuScreens.register(ModMenuTypes.dank_1, DankScreen::t1);
        MenuScreens.register(ModMenuTypes.dank_2, DankScreen::t2);
        MenuScreens.register(ModMenuTypes.dank_3, DankScreen::t3);
        MenuScreens.register(ModMenuTypes.dank_4, DankScreen::t4);
        MenuScreens.register(ModMenuTypes.dank_5, DankScreen::t5);
        MenuScreens.register(ModMenuTypes.dank_6, DankScreen::t6);
        MenuScreens.register(ModMenuTypes.dank_7, DankScreen::t7);

        MenuScreens.register(ModMenuTypes.change_frequency, ChangeFrequencyScreen::new);
    }
}
