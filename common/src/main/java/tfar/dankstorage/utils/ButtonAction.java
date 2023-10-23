package tfar.dankstorage.utils;

public enum ButtonAction {
    LOCK_FREQUENCY(true), PICK_BLOCK(false), SORT(true),
    TOGGLE_TAG(true), TOGGLE_PICKUP(false), TOGGLE_USE_TYPE(false), COMPRESS(true);
    public final boolean requiresContainer;

    ButtonAction(boolean requiresContainer) {
        this.requiresContainer = requiresContainer;
    }
}
