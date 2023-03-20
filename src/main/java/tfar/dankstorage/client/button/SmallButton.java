package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import tfar.dankstorage.client.Client;

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

    public void tint() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0,WIDGETS_LOCATION);
        tint();
        int c = getYImage(isHovered);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int halfwidth1 = this.width / 2;
        int halfwidth2 = this.width - halfwidth1;
        int halfheight1 = this.height / 2;
        int halfheight2 = this.height - halfheight1;
        blit(matrices, getX(), getY(), 0,
                46 + c * 20, halfwidth1, halfheight1);
        blit(matrices, getX() + halfwidth1, getY(), 200 - halfwidth2,
                46 + c * 20, halfwidth2, halfheight1);

        blit(matrices, getX(), getY() + halfheight1,
                0, 46 + c * 20 + 20 - halfheight2, halfwidth1, halfheight2);
        blit(matrices, getX() + halfwidth1, getY() + halfheight1,
                200 - halfwidth2, 46 + c * 20 + 20 - halfheight2, halfwidth2, halfheight2);
        if (shouldDrawText()) drawTextOnButton(matrices, halfwidth2);

        if (this.isHoveredOrFocused()) {
         //   this.renderToolTip(matrices,mouseX,mouseY);
        }
    }

    public void drawTextOnButton(PoseStack stack, int halfwidth2) {
        int textColor = getFGColor();
        drawCenteredString(stack, Client.mc.font, getMessage(), getX() + halfwidth2, getY() + (this.height - 8) / 2, textColor);
    }
}