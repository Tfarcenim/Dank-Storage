package tfar.dankstorage.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import tfar.dankstorage.DankStorage;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, DankStorage.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("block.dankstorage.dock", "Dock");
              add("item.dankstorage.dank_1", "Dank 1");
              add("item.dankstorage.dank_2", "Dank 2");
              add("item.dankstorage.dank_3", "Dank 3");
              add("item.dankstorage.dank_4", "Dank 4");
              add("item.dankstorage.dank_5", "Dank 5");
              add("item.dankstorage.dank_6", "Dank 6");
              add("item.dankstorage.dank_7", "Dank 7");
              add("item.dankstorage.1_to_2", "1 to 2");
              add("item.dankstorage.2_to_3", "2 to 3");
              add("item.dankstorage.3_to_4", "3 to 4");
              add("item.dankstorage.4_to_5", "4 to 5");
              add("item.dankstorage.5_to_6", "5 to 6");
              add("item.dankstorage.6_to_7", "6 to 7");
              add("item.dankstorage.red_print", "Red Print");
              add("container.dankstorage.dank_1", "Dank 1");
              add("container.dankstorage.dank_2", "Dank 2");
              add("container.dankstorage.dank_3", "Dank 3");
              add("container.dankstorage.dank_4", "Dank 4");
              add("container.dankstorage.dank_5", "Dank 5");
              add("container.dankstorage.dank_6", "Dank 6");
              add("container.dankstorage.dank_7", "Dank 7");
              add("dankstorage.mode.none", "None");
              add("dankstorage.mode.pickup_all", "Pickup All");
              add("dankstorage.mode.filtered_pickup", "Filtered Pickup");
              add("dankstorage.mode.void_pickup", "Void Pickup");

              add("dankstorage.sorting_type.descending", "Sort, Descending");
              add("dankstorage.sorting_type.descending.desc", "Descending, Sorts from greatest to least number of items");

              add("dankstorage.sorting_type.ascending", "Sort, Ascending");
              add("dankstorage.sorting_type.ascending.desc", "Ascending, Sorts from least to greatest number of items");

              add("dankstorage.sorting_type.registry_name", "Sort, Registry Name");
              add("dankstorage.sorting_type.registry_name.desc", "Registry Name, Sorts by registry name in alphabetical order *excluding* mod ids");

              add("dankstorage.sorting_type.modid" , "Sort, Mod id");
              add("dankstorage.sorting_type.modid.desc", "Mod id, Sorts by mod id then by registry name");

              add("dankstorage.auto_sort", "Auto Sort,");

              add("key.dankstorage.pickup", "Toggle Mode");
              add("key.dankstorage.construction", "Construction Mode");
              add("key.dankstorage.lock_slot", "Lock Slot");
              add("key.dankstorage.pickup_mode", "Pickup Mode");
              add("key.categories.dankstorage", "Dank Storage");
              add("text.dankstorage.shift", "Press %s for Info");
              add("text.dankstorage.empty", "Empty");
              add("text.dankstorage.lock", "Press %s to Lock");
              add("text.dankstorage.change_pickup_mode", "Press %s to change pickup mode");
              add("text.dankstorage.current_pickup_mode", "Current Pickup Mode, %s");
              add("text.dankstorage.changeusetype", "Press %s to change usetype");
              add("text.dankstorage.currentusetype", "Current Usetype, %s");
              add("dankstorage.usetype.bag", "Bag");
              add("dankstorage.usetype.construction", "Construction");
              add("text.dankstorage.blacklisted_storage", "This item has been blacklisted from storage!");
              add("text.dankstorage.blacklisted_usage", "This item cannot be used from storage!");
              add("text.dankstorage.stacklimit", "Stack Limit, %s");
              add("text.dankstorage.exact", "Exact Count, %s");
              add("text.dankstorage.formatcontaineditems", "%s %s");
              add("text.dankstorage.upgrade_successful", "Upgrade Successful");
              add("text.dankstorage.lock_button" , "Press to lock frequency");
              add("text.dankstorage.unlock_button" , "Press to unlock frequency");
              add("text.dankstorage.save_frequency_button" , "Save the new frequency \n%s \n%s \n%s \n%s \n%s");
              add("text.dankstorage.save_frequency_button.invalid" , "Red, %s");
              add("text.dankstorage.save_frequency_button.invalidtxt" , "This frequency is invalid");
              add("text.dankstorage.save_frequency_button.too_high" , "Orange, %s");
              add("text.dankstorage.save_frequency_button.too_hightxt" , "This frequency doesn't exist yet");
              add("text.dankstorage.save_frequency_button.different_tier" , "Yellow, %s");
              add("text.dankstorage.save_frequency_button.different_tiertxt" , "This frequency has a different tier");
              add("text.dankstorage.save_frequency_button.good" , "Green, %s");
              add("text.dankstorage.save_frequency_button.goodtxt" , "This frequency can be changed to");
              add("text.dankstorage.save_frequency_button.locked_frequency" , "Blue, %s");
              add("text.dankstorage.save_frequency_button.locked_frequencytxt" , "This frequency is locked");
              add("text.dankstorage.red_print.tooltip0" , "Copies frequency to other danks");
              add("text.dankstorage.red_print.tooltip1" , "Only works on same tier");
              add("dankstorage.command.reset_frequency.not_a_dank", "Not a Dank");
              add("dankstorage.command.set_tier.invalid_id" , "The frequency does not exist");
              add("text.dankstorage.compress_button" , "Compresses reversible 3x3 and 2x2 recipes");

              add("text.dankstorage.pickup_button" , "Pickup Modes \n%s \n%s \n%s \n%s");
              add("text.dankstorage.pickup_button.none" , "Gray, %s");
              add("text.dankstorage.pickup_button.nonetxt" , "Doesn't pick up anything");
              add("text.dankstorage.pickup_button.all" , "Green, %s");
              add("text.dankstorage.pickup_button.alltxt" , "Picks up everything the player touches");
              add("text.dankstorage.pickup_button.filtered" , "Yellow, %s");
              add("text.dankstorage.pickup_button.filteredtxt" , "Only picks up items that are already present in the dank");
              add("text.dankstorage.pickup_button.void" , "Red, %s");
              add("text.dankstorage.pickup_button.voidtxt" , "Will void all slot overflow, has same pickup restrictions as filtered");

              add("text.dankstorage.sort_button" , "Sorts from greatest to least");
              add("itemGroup.dankstorage" , "Dank Storage");

              add("text.dankstorage.tier_mismatch", "Current Dank cannot open backing inventory, upgrade or switch to supported frequency");
              add("text.dankstorage.open_config" , "Open Config");
    }
}
