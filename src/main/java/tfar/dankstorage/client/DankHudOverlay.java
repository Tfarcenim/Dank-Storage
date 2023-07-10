package tfar.dankstorage.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.ClientData;

import static tfar.dankstorage.client.Client.mc;

public class DankHudOverlay implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
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
        int xStart = screenWidth / 2 + DankStorage.ClientConfig.preview_x.get();
        int yStart = screenHeight + DankStorage.ClientConfig.preview_y.get();

        ItemStack toPlace = ClientData.selectedItem;

        if (!toPlace.isEmpty() && DankStorage.ClientConfig.preview.get()) {
            Integer color = toPlace.getItem().getRarity(toPlace).color.getColor();
            int c = color != null ? color : 0xFFFFFF;
            renderHotbarItem(poseStack, xStart, yStart, player, toPlace);
        }
        String mode = Utils.getUseType(bag).name();

        final int stringX = xStart + 8 - mc.font.width(mode) / 2;
        final int stringY = yStart + 16;
        mc.font.drawShadow(poseStack, mode, stringX, stringY, 0xffffff);
    }

    private static void renderHotbarItem(PoseStack poses, int x, int y, Player player, ItemStack stack) {
        mc.getItemRenderer().renderAndDecorateItem(poses,player, stack, x, y,0);
        mc.getItemRenderer().renderGuiItemDecorations(poses,mc.font, stack, x, y);
    }
}
