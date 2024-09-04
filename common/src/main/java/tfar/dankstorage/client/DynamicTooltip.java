package tfar.dankstorage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DynamicTooltip extends Tooltip {
    public boolean dirty = true;

    public Supplier<Component> messageSupplier;
    public Supplier<Component> narrationSupplier;

    public Component lastMessage;
    public Component lastNarration;

    public DynamicTooltip(Supplier<Component> messageSupplier, Supplier<Component> narrationSupplier) {
        super(null, null);
        this.messageSupplier = messageSupplier;
        this.narrationSupplier = narrationSupplier;
    }

    public static DynamicTooltip dynamic(Supplier<Component> componentSupplier) {
        return new DynamicTooltip(componentSupplier,componentSupplier);
    }

    public List<FormattedCharSequence> toCharSequence(Minecraft $$0) {

        if (dirty) {
            if (!Objects.equals(lastMessage,messageSupplier.get())) {
                lastMessage = messageSupplier.get();
                lastNarration = narrationSupplier.get();
                cachedTooltip = null;
                dirty = false;
            }
        }

        if (this.cachedTooltip == null) {
            this.cachedTooltip = splitTooltip($$0, this.lastMessage);
        }

        return this.cachedTooltip;
    }

    @Override
    public void updateNarration(NarrationElementOutput $$0) {
        if (lastNarration != null) {
            $$0.add(NarratedElementType.HINT, lastNarration);
        }
    }
}
