package tfar.dankstorage.platform;

import com.google.common.collect.Lists;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class TomlConfigs implements MLConfig {
    @Override
    public int posX() {
        return ClientConfig.preview_x.get();
    }

    @Override
    public int posY() {
        return ClientConfig.preview_y.get();
    }

    @Override
    public boolean showPreview() {
        return ClientConfig.preview.get();
    }

    @Override
    public double textSize() {
        return ClientConfig.textSize.get();
    }

    public static class ClientConfig {
        public static ModConfigSpec.BooleanValue preview;
        public static ModConfigSpec.IntValue preview_x;
        public static ModConfigSpec.IntValue preview_y;
        public static ModConfigSpec.DoubleValue textSize;

        public ClientConfig(ModConfigSpec.Builder builder) {
            builder.push("client");
            preview = builder
                    .comment("Whether to display the preview of the item in the dank, disable if you have optifine")
                    .define("preview", true);
            preview_x = builder
                    .comment("X position of preview")
                    .defineInRange("preview_x", -140, -10000, 10000);
            preview_y = builder
                    .comment("Y position of preview")
                    .defineInRange("preview_y", -25, -10000, 10000);
            textSize = builder.comment("Size of item text")
                    .defineInRange("text_size",.5,0,1);
            builder.pop();
        }
    }

    public static class ServerConfig {
    public static ModConfigSpec.IntValue stacklimit1;
    public static ModConfigSpec.IntValue stacklimit2;
    public static ModConfigSpec.IntValue stacklimit3;
    public static ModConfigSpec.IntValue stacklimit4;
    public static ModConfigSpec.IntValue stacklimit5;
    public static ModConfigSpec.IntValue stacklimit6;
    public static ModConfigSpec.IntValue stacklimit7;
      public static ModConfigSpec.ConfigValue<List<String>> convertible_tags;

    public static final List<String> defaults = Lists.newArrayList(
            "c:ingots/iron",
            "c:ingots/gold",
            "c:ores/coal",
            "c:ores/diamond",
            "c:ores/emerald",
            "c:ores/gold",
            "c:ores/iron",
            "c:ores/lapis",
            "c:ores/redstone",

            "c:gems/amethyst",
            "c:gems/peridot",
            "c:gems/ruby",

            "c:ingots/copper",
            "c:ingots/lead",
            "c:ingots/nickel",
            "c:ingots/silver",
            "c:ingots/tin",

            "c:ores/copper",
            "c:ores/lead",
            "c:ores/ruby",
            "c:ores/silver",
            "c:ores/tin");

    public ServerConfig(ModConfigSpec.Builder builder) {
      builder.push("server");
      stacklimit1 = builder.
              comment("Stack limit of first dank storage")
              .defineInRange("stacklimit1", 256, 1, Integer.MAX_VALUE);
      stacklimit2 = builder.
              comment("Stack limit of second dank storage")
              .defineInRange("stacklimit2", 1024, 1, Integer.MAX_VALUE);
      stacklimit3 = builder.
              comment("Stack limit of third dank storage")
              .defineInRange("stacklimit3", 4096, 1, Integer.MAX_VALUE);
      stacklimit4 = builder.
              comment("Stack limit of fourth dank storage")
              .defineInRange("stacklimit4", 16384, 1, Integer.MAX_VALUE);
      stacklimit5 = builder.
              comment("Stack limit of fifth dank storage")
              .defineInRange("stacklimit5", 65536, 1, Integer.MAX_VALUE);
      stacklimit6 = builder.
              comment("Stack limit of sixth dank storage")
              .defineInRange("stacklimit6", 262144, 1, Integer.MAX_VALUE);
      stacklimit7 = builder.
              comment("Stack limit of seventh dank storage")
              .defineInRange("stacklimit7", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

      convertible_tags = builder.
              comment("Tags that are eligible for conversion, input as a list of resourcelocation, eg 'c:ingots/iron'")
              .define("convertible tags", defaults);
      builder.pop();
    }
  }
}
