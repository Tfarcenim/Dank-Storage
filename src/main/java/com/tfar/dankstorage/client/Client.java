package com.tfar.dankstorage.client;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.network.MessageToggleAutoPickup;
import com.tfar.dankstorage.network.MessageToggleAutoVoid;
import com.tfar.dankstorage.screen.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class Client {

  public static KeyBinding AUTO_PICKUP;
  public static KeyBinding AUTO_VOID;

  @SubscribeEvent
  public static void client(FMLClientSetupEvent e){
    ScreenManager.registerFactory(DankStorage.Objects.dank_1_container, DankScreens.DankStorageScreen1::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_1_container, DankScreens.PortableDankStorageScreen1::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_2_container, DankScreens.DankStorageScreen2::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_2_container, DankScreens.PortableDankStorageScreen2::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_3_container, DankScreens.DankStorageScreen3::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_3_container, DankScreens.PortableDankStorageScreen3::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_4_container, DankScreens.DankStorageScreen4::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_4_container, DankScreens.PortableDankStorageScreen4::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_5_container, DankScreens.DankStorageScreen5::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_5_container, DankScreens.PortableDankStorageScreen5::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_6_container, DankScreens.DankStorageScreen6::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_6_container, DankScreens.PortableDankStorageScreen6::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_7_container, DankScreens.DankStorageScreen7::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_7_container, DankScreens.PortableDankStorageScreen7::new);

    AUTO_PICKUP = new KeyBinding("key.dankstorage.pickup", GLFW.GLFW_KEY_P, "key.categories.dankstorage");
    AUTO_VOID = new KeyBinding("key.dankstorage.void", GLFW.GLFW_KEY_O, "key.categories.dankstorage");
    ClientRegistry.registerKeyBinding(AUTO_PICKUP);
  }

  @Mod.EventBusSubscriber(value = Dist.CLIENT)
  public static class key {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
      if (AUTO_PICKUP.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new MessageToggleAutoPickup());
      }
      if (AUTO_VOID.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new MessageToggleAutoVoid());
      }
    }
  }
}
