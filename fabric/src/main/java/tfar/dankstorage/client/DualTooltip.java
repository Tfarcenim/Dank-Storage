package tfar.dankstorage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.client.screens.AbstractDankStorageScreen;

import java.util.List;

public class DualTooltip extends Tooltip {

    private final Component message2;
    boolean last;
    private final AbstractDankStorageScreen<?> screen;

    public DualTooltip(Component message1, Component message2, @Nullable Component narration, AbstractDankStorageScreen<?> screen) {
        super(message1, narration);
        this.message2 = message2;
        this.screen = screen;
    }

    public void invalidate() {
        this.cachedTooltip = null;
    }

    @Override
    public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {

        boolean locked = screen.getMenu().dankInventory.frequencyLocked();
        if (locked != last) {
            invalidate();
            last = locked;
        }
        if (this.cachedTooltip == null) {
            this.cachedTooltip = Tooltip.splitTooltip(minecraft, getMessage(locked));
        }
        return this.cachedTooltip;
    }

    public Component getMessage(boolean locked) {
        return locked ? message : message2;
    }
}
