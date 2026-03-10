package com.guiutils.keybind;

import com.guiutils.gui.GUIUtilsOverlay;
import com.guiutils.gui.SavedGUIManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeybindManager {

    /** Press V to re-open the saved GUI wrapped in the overlay */
    private static KeyBinding keyOpenSaved;

    /** Press O to open the overlay on the CURRENT GUI */
    private static KeyBinding keyWrapCurrent;

    public static void registerKeybinds() {
        keyOpenSaved = new KeyBinding("Open Saved GUI", Keyboard.KEY_V, "GUI Utils");
        keyWrapCurrent = new KeyBinding("Wrap Current GUI", Keyboard.KEY_O, "GUI Utils");

        ClientRegistry.registerKeyBinding(keyOpenSaved);
        ClientRegistry.registerKeyBinding(keyWrapCurrent);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        // V → restore saved GUI
        if (keyOpenSaved.isPressed()) {
            if (SavedGUIManager.hasSavedGUI() && mc.currentScreen == null) {
                SavedGUIManager.restoreSavedGUI();
            }
        }

        // O → wrap whatever GUI is currently open in the overlay
        if (keyWrapCurrent.isPressed()) {
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GUIUtilsOverlay)) {
                GUIUtilsOverlay overlay = new GUIUtilsOverlay(mc.currentScreen);
                mc.displayGuiScreen(overlay);
            }
        }
    }
}
