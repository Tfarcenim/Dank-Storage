package tfar.dankstorage.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.world.ClientData;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleOpenBook",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"),cancellable = true)
    private void interceptBook(ClientboundOpenBookPacket pPacket, CallbackInfo ci) {
        ItemStack itemstack = this.minecraft.player.getItemInHand(pPacket.getHand());
        if (itemstack.getItem() instanceof DankItem) {
            this.minecraft.setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(ClientData.selectedItem)));
            ci.cancel();
        }
    }
}
