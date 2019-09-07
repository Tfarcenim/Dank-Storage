package com.tfar.dankstorage.network;

import com.tfar.dankstorage.DankStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DankPacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DankStorage.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(1, CMessageToggle.class,
            CMessageToggle::encode,
            CMessageToggle::decode,
            CMessageToggle::handle);

    INSTANCE.registerMessage(2, CMessageConstructionMode.class,
            CMessageConstructionMode::encode,
            CMessageConstructionMode::decode,
            CMessageConstructionMode::handle);

    INSTANCE.registerMessage(3, CMessageChangeSlot.class,
            CMessageChangeSlot::encode,
            CMessageChangeSlot::new,
            CMessageChangeSlot::handle);

    INSTANCE.registerMessage(4, CMessagePickBlock.class,
            CMessagePickBlock::encode,
            CMessagePickBlock::decode,
            CMessagePickBlock::handle);
  }
}
