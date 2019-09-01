package com.tfar.dankstorage.network;

import com.tfar.dankstorage.DankStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DankPacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DankStorage.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(1, MessageToggleAutoPickup.class,
            MessageToggleAutoPickup::encode,
            MessageToggleAutoPickup::decode,
            MessageToggleAutoPickup::handle);

    INSTANCE.registerMessage(2, MessageToggleAutoVoid.class,
            MessageToggleAutoVoid::encode,
            MessageToggleAutoVoid::decode,
            MessageToggleAutoVoid::handle);

    INSTANCE.registerMessage(3, MessageToggleConstruction.class,
            MessageToggleConstruction::encode,
            MessageToggleConstruction::decode,
            MessageToggleConstruction::handle);

    INSTANCE.registerMessage(4, MessageChangeSlot.class,
            MessageChangeSlot::encode,
            MessageChangeSlot::new,
            MessageChangeSlot::handle);
  }
}
