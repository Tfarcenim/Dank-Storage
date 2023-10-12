package tfar.dankstorage.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class NumberEditBox extends EditBox {
    public NumberEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
    }

    public NumberEditBox(Font font, int i, int j, int k, int l, @Nullable EditBox editBox, Component component) {
        super(font, i, j, k, l, editBox, component);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return Character.isDigit(c) && super.charTyped(c, i);
    }
}
