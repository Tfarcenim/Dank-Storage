package tfar.dankstorage.client.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SmallButton extends Button {

    public SmallButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress callback) {
        super(x, y, widthIn, heightIn, buttonText, callback,DEFAULT_NARRATION);
    }
}