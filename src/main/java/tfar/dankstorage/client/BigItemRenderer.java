package tfar.dankstorage.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.mixin.ItemRendererAccessor;
import tfar.dankstorage.mixin.MinecraftClientAccessor;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class BigItemRenderer extends ItemRenderer {

    public static final BigItemRenderer INSTANCE = new BigItemRenderer(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getModelManager()
            , ((MinecraftClientAccessor) Minecraft.getInstance()).getItemColors(),((ItemRendererAccessor)Minecraft.getInstance().getItemRenderer()).getBlockEntityRenderer());
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.#");

    protected BigItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn, BlockEntityWithoutLevelRenderer blockEntityWithoutLevelRenderer) {
        super(textureManagerIn, modelManagerIn, itemColorsIn,blockEntityWithoutLevelRenderer);
    }

    @Override
    public void renderGuiItemDecorations(Font fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty()) {
            PoseStack matrixstack = new PoseStack();

            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? getStringFromInt(stack.getCount()) : text;
                matrixstack.translate(0.0D, 0.0D, this.blitOffset + 200.0F);
                MultiBufferSource.BufferSource irendertypebuffer$impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                matrixstack.pushPose();
                float scale = .5f;
                matrixstack.scale(scale, scale, 1.0F);
                fr.drawInBatch(s, (xPosition + 19 - 2 - (fr.width(s) * scale)) / scale,
                        (yPosition + 6 + 3 + (1 / (scale * scale) - 1)) / scale, 16777215, true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
                //true, matrixstack.getLast().getNormal(), irendertypebuffer$impl, false, 0, 15728880);
                irendertypebuffer$impl.endBatch();
                matrixstack.popPose();
            }


            if (stack.isDamaged()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
              //  RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.getBuilder();
                float f = stack.getDamageValue();
                float g = stack.getMaxDamage();
                float h = Math.max(0.0F, (g - f) / g);
                int k = Math.round(13.0F - f * 13.0F / g);
                int l = Mth.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
                ((ItemRendererAccessor) this).$fillRect(bufferBuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                ((ItemRendererAccessor) this).$fillRect(bufferBuilder, xPosition + 2, yPosition + 13, k, 1, l >> 16 & 255, l >> 8 & 255, l & 255, 255);
                RenderSystem.enableBlend();
              //  RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            LocalPlayer localPlayer = Minecraft.getInstance().player;
            float m = localPlayer == null ? 0.0F : localPlayer.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (m > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator2 = Tesselator.getInstance();
                BufferBuilder bufferBuilder2 = tesselator2.getBuilder();
                ((ItemRendererAccessor) this).$fillRect(bufferBuilder2, xPosition, yPosition + Mth.floor(16.0F * (1.0F - m)), 16, Mth.ceil(16.0F * m), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    public String getStringFromInt(int number) {

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }
}