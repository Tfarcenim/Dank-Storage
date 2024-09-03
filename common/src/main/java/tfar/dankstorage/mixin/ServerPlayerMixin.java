package tfar.dankstorage.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.menu.DankMenu;
import tfar.dankstorage.menu.CustomSync;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "initMenu",at = @At("RETURN"))
    private void customSync(AbstractContainerMenu abstractContainerMenu, CallbackInfo ci) {
        if (abstractContainerMenu instanceof DankMenu dankMenu) {
            dankMenu.setSynchronizer(new CustomSync((ServerPlayer)(Object) this));
        }
    }
}
