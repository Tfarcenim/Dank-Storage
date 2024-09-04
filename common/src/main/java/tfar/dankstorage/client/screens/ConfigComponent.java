package tfar.dankstorage.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import tfar.dankstorage.menu.DankMenu;

public class ConfigComponent implements Renderable, GuiEventListener{

    private boolean visible;
    private Minecraft minecraft;
    private int width;
    private int height;
    private DankMenu menu;


    public void init(int pWidth, int pHeight, Minecraft pMinecraft, DankMenu pMenu) {
        this.minecraft = pMinecraft;
        this.width = pWidth;
        this.height = pHeight;
        this.menu = pMenu;
        if (this.visible) {
            this.initVisuals();
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    protected void setVisible(boolean pVisible) {
        if (pVisible) {
            this.initVisuals();
        }

        this.visible = pVisible;
    //    this.book.setOpen(this.menu.getRecipeBookType(), pVisible);
        if (!pVisible) {
    //        this.recipeBookPage.setInvisible();
        }

       // this.sendUpdateSettings();
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }


    public void initVisuals() {

    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isVisible()) {
            pGuiGraphics.pose().pushPose();
          //  pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

        //    this.searchBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

          //  for (RecipeBookTabButton recipebooktabbutton : this.tabButtons) {
       //         recipebooktabbutton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
         //   }
//
//this.filterButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
         //   this.recipeBookPage.render(pGuiGraphics, i, j, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.pose().popPose();
        }
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        if (this.isVisible()) {
            int i = (this.width - 147) / 2 - 111;
            int j = (this.height - 166) / 2;
            guiGraphics.blit(ChangeFrequencyScreen.DEMO_BACKGROUND_LOCATION, i, j, 0, 0, 100, 166);
        }
    }

    @Override
    public void setFocused(boolean b) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
