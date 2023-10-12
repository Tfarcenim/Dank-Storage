package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.chat.Component;

public class RedGreenToggleButton extends SmallButton {

    protected boolean toggled;

    public RedGreenToggleButton(int x, int y, int widthIn, int heightIn, OnPress callback, boolean toggled) {
        super(x, y, widthIn, heightIn, Component.literal(""), callback);
        this.toggled = toggled;
    }

    public void toggle() {
        this.toggled = !this.toggled;
    }

    @Override
    public void tint() {
        if (toggled) RenderSystem.setShaderColor(0, 1, 0,1);
        else RenderSystem.setShaderColor(1, 0, 0,1);
    }
}