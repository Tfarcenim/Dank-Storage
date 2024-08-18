package tfar.dankstorage.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.DankStorageNeoForge;
import tfar.dankstorage.blockentity.CommonDockBlockEntity;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.DankPacketHandlerNeoForge;
import tfar.dankstorage.network.client.S2CModPacket;
import tfar.dankstorage.network.server.C2SModPacket;
import tfar.dankstorage.platform.services.IPlatformHelper;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankInventoryForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    final MLConfig config = new TomlConfigs();

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    public static PayloadRegistrar registrar;
    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec) {
        registrar.playToClient(type, streamCodec, (p, t) -> p.handleClient());
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf, MSG> streamCodec) {
        registrar.playToServer(type, streamCodec, (p, t) -> p.handleServer((ServerPlayer) t.player()));
    }


    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        DankPacketHandlerNeoForge.sendToClient(msg, player);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        DankPacketHandlerNeoForge.sendToServer(msg);
    }

    @Override
    public DankInterface createInventory(DankStats stats, int frequency) {
        return new DankInventoryForge(stats, frequency);
    }

    @Override
    public Slot createSlot(DankInterface dankInventory, int index, int xPosition, int yPosition) {
        return new DankSlot((DankInventoryForge) dankInventory, index, xPosition, yPosition);
    }

    @Override
    public <F> void registerAll(Map<String, ? extends F> map, Registry<F> registry, Class<? extends F> filter) {
        List<Pair<ResourceLocation, Supplier<?>>> list = DankStorageNeoForge.registerLater.computeIfAbsent(registry, k -> new ArrayList<>());
        for (Map.Entry<String, ? extends F> entry : map.entrySet()) {
            list.add(Pair.of(DankStorage.id(entry.getKey()), entry::getValue));
        }
    }

    @Override
    public <F> void unfreeze(Registry<F> registry) {
        ((MappedRegistry<F>)registry).unfreeze();
    }

    @Override
    public ItemStack getCloneStack(Level level, BlockPos pos, BlockState state, HitResult hitResult, Player player) {
        return state.getCloneItemStack(hitResult, level, pos, player);
    }

    @Override
    public CommonDockBlockEntity<?> blockEntity(BlockPos pos, BlockState state) {
        return new DockBlockEntity(pos,state);
    }

    @Override
    public MLConfig getConfig() {
        return config;
    }
}