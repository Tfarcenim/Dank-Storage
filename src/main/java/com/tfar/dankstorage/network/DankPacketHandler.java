package com.tfar.dankstorage.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class DankPacketHandler {
  public static SimpleNetworkWrapper INSTANCE;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

    INSTANCE.registerMessage(CMessageChangeSlot.Handler.class, CMessageChangeSlot.class, 0, Side.SERVER);
    INSTANCE.registerMessage(CMessagePickBlock.Handler.class, CMessagePickBlock.class, 1, Side.SERVER);
    INSTANCE.registerMessage(CMessageToggle.Handler.class, CMessageToggle.class, 2, Side.SERVER);
  }
}
