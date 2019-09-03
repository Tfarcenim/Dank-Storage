package com.tfar.dankstorage.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class DankPacketHandler {
  public static SimpleNetworkWrapper INSTANCE;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

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

    INSTANCE.registerMessage(5, MessagePickBlock.class,
            MessagePickBlock::encode,
            MessagePickBlock::decode,
            MessagePickBlock::handle);
  }
}
