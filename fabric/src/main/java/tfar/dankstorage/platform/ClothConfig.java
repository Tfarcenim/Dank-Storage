package tfar.dankstorage.platform;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import tfar.dankstorage.DankStorage;

@Config(name = DankStorage.MODID)
public class ClothConfig implements ConfigData, MLConfig{

    public boolean show_preview = true;

    public int preview_x = -140;
    public int preview_y = -25;

    public double text_size = .5;

    @Override
    public int posX() {
        return preview_x;
    }

    @Override
    public int posY() {
        return preview_y;
    }

    @Override
    public boolean showPreview() {
        return show_preview;
    }

    @Override
    public double textSize() {
        return text_size;
    }
}
