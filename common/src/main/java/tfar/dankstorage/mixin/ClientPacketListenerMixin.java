package tfar.dankstorage.mixin;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tfar.dankstorage.item.CDankItem;
import tfar.dankstorage.world.ClientData;

@Mixin(BookViewScreen.BookAccess.class)
public class ClientPacketListenerMixin {

    @ModifyVariable(method = "fromItem", at = @At("HEAD"), argsOnly = true)
    private static ItemStack interceptBook(ItemStack stack) {
        if (stack.getItem() instanceof CDankItem) {
            return ClientData.selectedItem;
        }
        return stack;
    }
}
