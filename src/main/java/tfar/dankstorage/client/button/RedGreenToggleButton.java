package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import tfar.dankstorage.utils.Utils;

public class RedGreenToggleButton extends SmallButton {

    protected boolean toggled;

    public RedGreenToggleButton(int x, int y, int widthIn, int heightIn, OnPress callback, boolean toggled) {
        super(x, y, widthIn, heightIn, Utils.literal(""), callback);
        this.toggled = toggled;
    }

    public void toggle() {
        this.toggled = !this.toggled;
    }

    public void tint() {
        if (toggled) RenderSystem.setShaderColor(0, 1, 0,1);
        else RenderSystem.setShaderColor(1, 0, 0,1);
    }
}