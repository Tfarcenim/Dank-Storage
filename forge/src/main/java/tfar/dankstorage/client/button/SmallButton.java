package tfar.dankstorage.client.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SmallButton extends Button {


    public SmallButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, Button.DEFAULT_NARRATION);
    }

    protected SmallButton(Builder builder) {
        super(builder);
    }

    public boolean shouldDrawText() {
        return !getMessage().getString().isEmpty();
    }

}