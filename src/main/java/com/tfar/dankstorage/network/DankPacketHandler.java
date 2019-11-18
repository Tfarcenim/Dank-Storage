package com.tfar.dankstorage.network;

import com.tfar.dankstorage.DankStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DankPacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DankStorage.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, CMessageTogglePickup.class,
            (message, buffer) -> {},
            buffer -> new CMessageTogglePickup(),
            CMessageTogglePickup::handle);

    INSTANCE.registerMessage(id++, C2SMessageLockSlot.class,
            C2SMessageLockSlot::encode,
            C2SMessageLockSlot::new,
            C2SMessageLockSlot::handle);

    INSTANCE.registerMessage(id++, CMessageToggleUseType.class,
            (message, buffer) -> {},
            buffer -> new CMessageToggleUseType(),
            CMessageToggleUseType::handle);

    INSTANCE.registerMessage(id++, C2SMessageScrollSlot.class,
            C2SMessageScrollSlot::encode,
            C2SMessageScrollSlot::new,
            C2SMessageScrollSlot::handle);

    INSTANCE.registerMessage(id++, CMessagePickBlock.class,
            (cMessagePickBlock, buffer) -> {},
            buffer -> new CMessagePickBlock(),
            CMessagePickBlock::handle);

    INSTANCE.registerMessage(id++, C2SMessageTagMode.class,
            (message, buffer) -> {},
            buffer -> new C2SMessageTagMode(),
            C2SMessageTagMode::handle);

    INSTANCE.registerMessage(id++, CMessageSort.class,
            (message, buffer) -> {},
            buffer -> new CMessageSort(),
            CMessageSort::handle);

    INSTANCE.registerMessage(id++, S2CSyncExtendedSlotContents.class,
            S2CSyncExtendedSlotContents::encode,
            S2CSyncExtendedSlotContents::new,
            S2CSyncExtendedSlotContents::handle);

    INSTANCE.registerMessage(id++, S2CSyncNBTSize.class,
            S2CSyncNBTSize::encode,
            S2CSyncNBTSize::new,
            S2CSyncNBTSize::handle);
  }
}
