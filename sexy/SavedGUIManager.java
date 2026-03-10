package com.guiutils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * Saves a reference to the currently open GUI so it can be
 * re-opened later (after Close Without Packet) by pressing V.
 */
public class SavedGUIManager {

    private static GuiScreen savedScreen = null;
    private static String    savedTitle  = null;

    private SavedGUIManager() {}

    /** Saves the currently open GUI (if any). */
    public static void saveCurrentGUI() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) {
            savedScreen = mc.currentScreen;
            savedTitle  = mc.currentScreen.getClass().getSimpleName();
        }
    }

    /** Restores the saved GUI. */
    public static void restoreSavedGUI() {
        if (savedScreen != null) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(savedScreen);
        }
    }

    public static boolean hasSavedGUI() {
        return savedScreen != null;
    }

    public static String getSavedTitle() {
        return savedTitle != null ? savedTitle : "None";
    }

    public static void clearSaved() {
        savedScreen = null;
        savedTitle  = null;
    }
}
