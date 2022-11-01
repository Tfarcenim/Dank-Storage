package tfar.dankstorage.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.dankstorage.client.button.TripleToggleButton;
import tfar.dankstorage.container.DankMenu;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.utils.Utils;

import static tfar.dankstorage.client.screens.DockScreen.*;

public class PortableDankStorageScreen extends AbstractDankStorageScreen<DankMenu> {

    public PortableDankStorageScreen(DankMenu container, Inventory playerinventory, Component component, ResourceLocation background) {
        super(container, playerinventory, component, background);
    }

    public static PortableDankStorageScreen t1(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background1);
    }

    public static PortableDankStorageScreen t2(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background2);
    }

    public static PortableDankStorageScreen t3(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background3);
    }

    public static PortableDankStorageScreen t4(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background4);
    }

    public static PortableDankStorageScreen t5(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background5);
    }

    public static PortableDankStorageScreen t6(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background6);
    }

    public static PortableDankStorageScreen t7(DankMenu container, Inventory playerinventory, Component component) {
        return new PortableDankStorageScreen(container, playerinventory, component, background7);
    }

    @Override
    protected void init() {
        super.init();

        //    this.addRenderableWidget(new RedGreenToggleButton(leftPos + (start += 8), topPos + 6, 8, 8, b -> {
     //       ((RedGreenToggleButton) b).toggle();
    //        C2SMessageTagMode.send();
    //    }, false));

        Button.OnTooltip onTooltip = (button, poseStack, x, y) -> {

            this.renderTooltip(poseStack,
                    this.minecraft.font.split(
                            Utils.literal("Pickup"), Math.max(this.width / 2 - 43, 170)), x, y);

        };

        this.addRenderableWidget(new TripleToggleButton(leftPos + 101, topPos + 4, 12, 12,Utils.literal("P"), b -> {
            Utils.cyclePickupMode(menu.bag, Minecraft.getInstance().player);
            C2SButtonPacket.send(C2SButtonPacket.Action.TOGGLE_PICKUP);
        },onTooltip,this));
    }
}