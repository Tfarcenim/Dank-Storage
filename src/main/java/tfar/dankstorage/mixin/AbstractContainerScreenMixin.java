package tfar.dankstorage.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.client.BigItemRenderer;
import tfar.dankstorage.inventory.DankSlot;

//todo mixin ItemRenderer?
@Mixin(AbstractContainerScreen.class)
abstract class AbstractContainerScreenMixin extends Screen {
    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getBlitOffset()I", ordinal = 0))
    private void swapRendererBack(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        if (slot instanceof DankSlot) {
            this.itemRenderer = BigItemRenderer.INSTANCE;
            itemRenderer.blitOffset = 100;
        }
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V"))
    private void swapRendererBack3(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        this.itemRenderer = minecraft.getItemRenderer();
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderAndDecorateItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"))
    private void swapRendererBack1(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        if (slot instanceof DankSlot) {
            this.itemRenderer = BigItemRenderer.INSTANCE;
            BigItemRenderer.INSTANCE.blitOffset = 100;
        }
    }

    @Inject(method = "renderSlot", at = @At(value = "RETURN"))
    private void swapRendererBack2(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        this.itemRenderer = minecraft.getItemRenderer();
    }
}
