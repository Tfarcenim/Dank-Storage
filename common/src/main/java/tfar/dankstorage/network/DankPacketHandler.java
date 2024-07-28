package tfar.dankstorage.network;

import net.minecraft.resources.ResourceLocation;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.network.client.*;
import tfar.dankstorage.network.server.*;
import tfar.dankstorage.platform.Services;

import java.util.Locale;

public class DankPacketHandler {

    public static void registerPackets() {

        Services.PLATFORM.registerServerPacket(C2SScrollSlotPacket.class, C2SScrollSlotPacket::new);
        Services.PLATFORM.registerServerPacket(C2SLockSlotPacket.class, C2SLockSlotPacket::new);
        Services.PLATFORM.registerServerPacket(C2SButtonPacket.class, C2SButtonPacket::new);
        Services.PLATFORM.registerServerPacket(C2SSetFrequencyPacket.class, C2SSetFrequencyPacket::new);
        Services.PLATFORM.registerServerPacket(C2SRequestContentsPacket.class, C2SRequestContentsPacket::new);

        ///////server to client

        Services.PLATFORM.registerClientPacket(S2CSendExtendedSlotChangePacket.class, S2CSendExtendedSlotChangePacket::new);
        Services.PLATFORM.registerClientPacket(S2CSendGhostSlotPacket.class, S2CSendGhostSlotPacket::new);
        Services.PLATFORM.registerClientPacket(S2CSyncSelectedDankItemPacket.class, S2CSyncSelectedDankItemPacket::new);
        Services.PLATFORM.registerClientPacket(S2CInitialSyncContainerPacket.class, S2CInitialSyncContainerPacket::new);
        Services.PLATFORM.registerClientPacket(S2CContentsForDisplayPacket.class, S2CContentsForDisplayPacket::new);
    }

    public static ResourceLocation packet(Class<?> clazz) {
        return DankStorage.id(clazz.getName().toLowerCase(Locale.ROOT));
    }

}
