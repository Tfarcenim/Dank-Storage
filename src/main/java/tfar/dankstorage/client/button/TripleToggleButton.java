package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.chat.Component;
import tfar.dankstorage.client.screens.DankStorageScreen;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.utils.PickupMode;

public class TripleToggleButton<T extends AbstractDankMenu> extends SmallButton {

    protected DankStorageScreen<T> screen;

    public TripleToggleButton(int x, int y, int widthIn, int heightIn, Component component, OnPress onPress, DankStorageScreen<T> screen) {
        super(x, y, widthIn, heightIn,component,onPress);
        this.screen = screen;
        //setFGColor(screen.getMenu().getMode().getColor());
    }

    @Override
    public void tint() {
        PickupMode mode = screen.getMenu().getMode();
        RenderSystem.setShaderColor(mode.r(), mode.g(), mode.b(), 1);
    }
}