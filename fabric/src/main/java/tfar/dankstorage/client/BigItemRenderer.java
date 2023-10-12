package tfar.dankstorage.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.mixin.ItemRendererAccessor;
import tfar.dankstorage.mixin.MinecraftClientAccessor;
import tfar.dankstorage.utils.Utils;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class BigItemRenderer extends ItemRenderer {

    public static final BigItemRenderer INSTANCE = new BigItemRenderer(Minecraft.getInstance(),Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getModelManager()
            , ((MinecraftClientAccessor) Minecraft.getInstance()).getItemColors(),((ItemRendererAccessor)Minecraft.getInstance().getItemRenderer()).getBlockEntityRenderer());

    public BigItemRenderer(Minecraft minecraft, TextureManager textureManager, ModelManager modelManager, ItemColors itemColors, BlockEntityWithoutLevelRenderer blockEntityWithoutLevelRenderer) {
        super(minecraft, textureManager, modelManager, itemColors, blockEntityWithoutLevelRenderer);
    }

    @Override
    public void renderAndDecorateItem(PoseStack matrices,LivingEntity livingEntity, ItemStack itemStack, int i, int j, int k) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(matrices,livingEntity, itemStack, i, j, k);//needed or else the game complains about missing models
    }

    @Override
    public void renderGuiItemDecorations(PoseStack matrices,Font fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (!stack.isEmpty()) {

            if (stack.getCount() != 1 || text != null) {
                String s = text == null ? Utils.formatLargeNumber(stack.getCount()) : text;
                matrices.translate(0.0F, 0.0F, 200.0F);
                MultiBufferSource.BufferSource irendertypebuffer$impl = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                matrices.pushPose();
                float scale = .5f;
                matrices.scale(scale, scale, 1.0F);
                fr.drawInBatch(s, (xPosition + 19 - 2 - (fr.width(s) * scale)) / scale,
                                        (yPosition + 6 + 3 + (1 / (scale * scale) - 1)) / scale, 16777215, true, matrices.last().pose(), irendertypebuffer$impl, Font.DisplayMode.NORMAL, 0, 15728880, false);
                //true, matrixstack.getLast().getNormal(), irendertypebuffer$impl, false, 0, 15728880);
                irendertypebuffer$impl.endBatch();
                matrices.popPose();
            }


            if (stack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableBlend();
                float f = stack.getDamageValue();
                float g = stack.getMaxDamage();
                float h = Math.max(0.0F, (g - f) / g);
                int k = Math.round(13.0F - f * 13.0F / g);
                int l = Mth.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
                GuiComponent.fill(matrices,xPosition + 2, yPosition + 13, 13, 2, 0, 255);
                GuiComponent.fill(matrices, xPosition + 2, yPosition + 13, k, 1, l, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            }

            LocalPlayer localPlayer = Minecraft.getInstance().player;
            float m = localPlayer == null ? 0.0F : localPlayer.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (m > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                GuiComponent.fill(matrices, xPosition, yPosition + Mth.floor(16.0F * (1.0F - m)), 16, Mth.ceil(16.0F * m), 0xffffff, 127);
                RenderSystem.enableDepthTest();
            }
        }
    }
}