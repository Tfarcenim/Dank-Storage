package tfar.dankstorage;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfar.dankstorage.mixin.MinecraftServerAccess;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.world.CDankSavedData;
import tfar.dankstorage.world.MaxId;

import java.io.File;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class DankStorage {

    public static final String MODID = "dankstorage";
    public static final String MOD_NAME = "Dank-Storage";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static MaxId maxId;

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {

    //    Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
    //    Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
      //  if (Services.PLATFORM.isModLoaded("examplemod")) {

       //     LOG.info("Hello to examplemod");
      //  }
    }

    public static MaxId getMaxId(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(MaxId::loadStatic,MaxId::new,
                        DankStorage.MODID+":max_id");
    }

    public static void onServerShutDown(MinecraftServer server) {
        maxId = null;
        CommonUtils.uncacheRecipes();
    }

    public static void onServerStart(MinecraftServer server) {
        LevelStorageSource.LevelStorageAccess storageSource = ((MinecraftServerAccess)server).getStorageSource();
        File file = storageSource.getDimensionPath(server.getLevel(Level.OVERWORLD).dimension())
                .resolve("data/"+ DankStorage.MODID).toFile();
        file.mkdirs();

        DankStorage.maxId = DankStorage.getMaxId(server);
    }

    public static CDankSavedData getData(int id, MinecraftServer server) {
        if (id <= CommonUtils.INVALID) throw new RuntimeException("Invalid frequency: "+id);
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        return overworld.getDataStorage()
                .computeIfAbsent(compoundTag -> CDankSavedData.loadStatic(compoundTag,overworld), () -> new CDankSavedData(overworld),
                        DankStorage.MODID+"/"+id);
    }
}