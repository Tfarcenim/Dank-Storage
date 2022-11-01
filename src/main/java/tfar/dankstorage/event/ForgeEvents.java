package tfar.dankstorage.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.ClientData;

public class ForgeEvents {

    static Minecraft mc = Minecraft.getInstance();

    public static void renderStack(RenderGameOverlayEvent e) {
        PoseStack matrixStack = e.getMatrixStack();
        Player player = mc.player;
        if (player == null)
            return;
        if (!(player.containerMenu instanceof InventoryMenu)) return;
        ItemStack bag = player.getMainHandItem();
        if (!(bag.getItem() instanceof DankItem)) {
            bag = player.getOffhandItem();
            if (!(bag.getItem() instanceof DankItem))
                return;
        }
        int xStart = mc.getWindow().getGuiScaledWidth() / 2;
        int yStart = mc.getWindow().getGuiScaledHeight();

        ItemStack toPlace = ClientData.selectedItem;

        if (!toPlace.isEmpty()) {


            Integer color = toPlace.getItem().getRarity(toPlace).color.getColor();

            int c = color != null ? color : 0xFFFFFF;


            final int itemX = xStart - 150;
            final int itemY = yStart - 25;
            renderHotbarItem(matrixStack,itemX, itemY, 0, player, toPlace);
        }
        final int stringX = xStart - 155;
        final int stringY = yStart - 10;
        String mode = Utils.getUseType(bag).name();
        mc.font.drawShadow(matrixStack, mode, stringX, stringY, 0xffffff);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);

        }

    private static void renderHotbarItem(PoseStack poses,int x, int y, float partialTicks, Player player, ItemStack stack) {
        float f = (float) stack.getPopTime() - partialTicks;
        if (f > 0.0F) {
            poses.pushPose();
            float f1 = 1.0F + f / 5.0F;
            poses.translate((float) (x + 8), (float) (y + 12), 0.0F);
            poses.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            poses.translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
        }

        mc.getItemRenderer().renderAndDecorateItem(player, stack, x, y,0);
        if (f > 0.0F) {
            poses.popPose();
        }
        mc.getItemRenderer().renderGuiItemDecorations(mc.font, stack, x, y);
    }

    public static void onPickBlock(InputEvent.ClickInputEvent e) {
        if (e.isPickBlock()) {

            if (Utils.isHoldingDank(mc.player) && mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
                //C2SMessagePickBlock.send(mc.picked);
                e.setCanceled(true);
            }
        }
    }

    public static void onScroll(InputEvent.MouseScrollEvent e) {
        if (ClientMixinEvents.onScroll(e.getScrollDelta()))e.setCanceled(true);
    }
}
