package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import tfar.dankstorage.client.Client;

public class SmallButton extends Button {
    public SmallButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress callback, OnTooltip onTooltip) {
        super(x, y, widthIn, heightIn, buttonText, callback,onTooltip);
    }

    public SmallButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress callback) {
        super(x, y, widthIn, heightIn, buttonText, callback);
    }

    public boolean shouldDrawText() {
        return !getMessage().getString().isEmpty();
    }

    public void tint() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0,WIDGETS_LOCATION);

        tint();

        int c = getYImage(isHovered);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);

        int halfwidth1 = this.width / 2;
        int halfwidth2 = this.width - halfwidth1;
        int halfheight1 = this.height / 2;
        int halfheight2 = this.height - halfheight1;
        blit(matrices, x, y, 0,
                46 + c * 20, halfwidth1, halfheight1);
        blit(matrices, x + halfwidth1, y, 200 - halfwidth2,
                46 + c * 20, halfwidth2, halfheight1);

        blit(matrices, x, y + halfheight1,
                0, 46 + c * 20 + 20 - halfheight2, halfwidth1, halfheight2);
        blit(matrices, x + halfwidth1, y + halfheight1,
                200 - halfwidth2, 46 + c * 20 + 20 - halfheight2, halfwidth2, halfheight2);
        if (shouldDrawText()) drawText(matrices, halfwidth2);

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(matrices,mouseX,mouseY);
        }
    }

    public void drawText(PoseStack stack, int halfwidth2) {
        int textColor = 0xe0e0e0;

        if (1 != 0) {
            textColor = -1;
        } else if (!this.visible) {
            textColor = 0xa0a0a0;
        } else if (this.isHovered) {
            textColor = 0xffffa0;
        }
        drawCenteredString(stack, Client.mc.font, getMessage(), x + halfwidth2, y + (this.height - 8) / 2, textColor);
    }
}