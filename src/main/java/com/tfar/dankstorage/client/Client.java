package com.tfar.dankstorage.client;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.network.MessageToggleAutoPickup;
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

  @SubscribeEvent
  public static void client(FMLClientSetupEvent e){
    ScreenManager.registerFactory(DankStorage.Objects.dank_1_container, DankStorageScreen1::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_1_container, PortableDankStorageScreen1::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_2_container, DankStorageScreen2::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_2_container, PortableDankStorageScreen2::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_3_container, DankStorageScreen3::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_3_container, PortableDankStorageScreen3::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_4_container, DankStorageScreen4::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_4_container, PortableDankStorageScreen4::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_5_container, DankStorageScreen5::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_5_container, PortableDankStorageScreen5::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_6_container, DankStorageScreen6::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_6_container, PortableDankStorageScreen6::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_7_container, DankStorageScreen7::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_7_container, PortableDankStorageScreen7::new);

    AUTO_PICKUP = new KeyBinding("key.dankstorage.autopickup", GLFW.GLFW_KEY_P, "key.categories.dankstorage");
    ClientRegistry.registerKeyBinding(AUTO_PICKUP);
  }

  @Mod.EventBusSubscriber(value = Dist.CLIENT)
  public static class key {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
      if (AUTO_PICKUP.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new MessageToggleAutoPickup());
      }
    }
  }
}
