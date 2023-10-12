package tfar.dankstorage.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tfar.dankstorage.DankStorageForge;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.ClientData;

import static tfar.dankstorage.client.Client.mc;

public class DankHudOverlay implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
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
        int xStart = screenWidth / 2 + DankStorageForge.ClientConfig.preview_x.get();
        int yStart = screenHeight + DankStorageForge.ClientConfig.preview_y.get();

        ItemStack toPlace = ClientData.selectedItem;

        if (!toPlace.isEmpty() && DankStorageForge.ClientConfig.preview.get()) {
            Integer color = toPlace.getItem().getRarity(toPlace).color.getColor();
            int c = color != null ? color : 0xFFFFFF;
            renderHotbarItem(guiGraphics, xStart, yStart, player, toPlace);
        }
        String mode = Utils.getUseType(bag).name();

        final int stringX = xStart + 8 - mc.font.width(mode) / 2;
        final int stringY = yStart + 16;
        guiGraphics.drawString(mc.font,mode, stringX, stringY, 0xffffff);
    }

    private static void renderHotbarItem(GuiGraphics poses, int x, int y, Player player, ItemStack stack) {
        poses.renderFakeItem(stack, x, y);
        poses.renderItemDecorations(mc.font, stack, x, y);
    }
}
