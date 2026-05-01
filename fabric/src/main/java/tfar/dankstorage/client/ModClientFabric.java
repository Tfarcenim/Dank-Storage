package tfar.dankstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.dankstorage.events.ClientEvents;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.network.server.C2SOpenMenuPacket;
import tfar.dankstorage.utils.UseType;

public class ModClientFabric implements ClientModInitializer {

    public static void keyPressed(Minecraft client) {
        if (DankKeybinds.CONSTRUCTION.consumeClick()) {
            C2SButtonPacket.TOGGLE_USE_TYPE.send();
        }
        if (DankKeybinds.PICKUP_MODE.consumeClick()) {
            C2SButtonPacket.TOGGLE_PICKUP.send();
        }
    }

    @Override
    public void onInitializeClient() {
        CommonClient.setup();

        KeyMappingHelper.registerKeyMapping(DankKeybinds.CONSTRUCTION);
        KeyMappingHelper.registerKeyMapping(DankKeybinds.LOCK_SLOT);
        KeyMappingHelper.registerKeyMapping(DankKeybinds.PICKUP_MODE);
        ClientTickEvents.START_CLIENT_TICK.register(ModClientFabric::keyPressed);
        ClientTooltipComponentCallback.EVENT.register(CommonClient::tooltipImage);
        HudRenderCallback.EVENT.register(ClientEvents::extractSelectedItem);
        UseItemCallback.EVENT.register(this::interact);
    }

    public InteractionResult interact(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.level().isClientSide() && stack.getItem() instanceof DankItem && Minecraft.getInstance().hasAltDown() && DankItem.getUseType(stack)!= UseType.bag) {
            C2SOpenMenuPacket.send(hand);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
