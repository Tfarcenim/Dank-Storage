package tfar.dankstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.dankstorage.events.ClientEvents;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.network.server.C2SOpenMenuPacket;
import tfar.dankstorage.utils.KeybindAction;
import tfar.dankstorage.utils.UseType;

public class ModClientFabric implements ClientModInitializer {

    public static void keyPressed(Minecraft client) {
        if (DankKeybinds.CONSTRUCTION.consumeClick()) {
            C2SButtonPacket.send(KeybindAction.TOGGLE_USE_TYPE);
        }
        if (DankKeybinds.PICKUP_MODE.consumeClick()) {
            C2SButtonPacket.send(KeybindAction.TOGGLE_PICKUP);
        }
    }

    @Override
    public void onInitializeClient() {
        CommonClient.setup();

        KeyBindingHelper.registerKeyBinding(DankKeybinds.CONSTRUCTION);
        KeyBindingHelper.registerKeyBinding(DankKeybinds.LOCK_SLOT);
        KeyBindingHelper.registerKeyBinding(DankKeybinds.PICKUP_MODE);
        ClientTickEvents.START_CLIENT_TICK.register(ModClientFabric::keyPressed);
        TooltipComponentCallback.EVENT.register(CommonClient::tooltipImage);
        HudRenderCallback.EVENT.register(ClientEvents::renderSelectedItem);
        UseItemCallback.EVENT.register(this::interact);
    }

    public InteractionResultHolder<ItemStack> interact(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.level().isClientSide && stack.getItem() instanceof DankItem && Screen.hasAltDown() && DankItem.getUseType(stack)!= UseType.bag) {
            C2SOpenMenuPacket.send(hand);
            return InteractionResultHolder.success(stack);
        }
        return null;
    }
}
