package com.guiutils.gui;

import com.guiutils.core.GUIUtilsMod;
import com.guiutils.network.PacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Overlay panel drawn on top of any GUI.
 * Shows: Close Without Packet | Save GUI | Delay Packets toggle | command bar
 */
public class GUIUtilsOverlay extends GuiScreen {

    // The original GUI we are overlaying
    private final GuiScreen wrappedGui;

    private GuiTextField commandBar;

    // Button positions
    private static final int BTN_W  = 160;
    private static final int BTN_H  = 20;
    private static final int BTN_X  = 4;
    private static final int BTN_Y_START = 4;
    private static final int BTN_GAP = 22;

    // Button IDs
    private static final int ID_CLOSE_NO_PKT = 1;
    private static final int ID_SAVE_GUI     = 2;
    private static final int ID_DELAY        = 3;

    public GUIUtilsOverlay(GuiScreen wrapped) {
        this.wrappedGui = wrapped;
    }

    @Override
    public void initGui() {
        // Forward to wrapped GUI
        if (wrappedGui != null) {
            wrappedGui.setWorldAndResolution(mc, width, height);
        }

        // Buttons
        buttonList.clear();

        int y = BTN_Y_START;
        buttonList.add(new net.minecraft.client.gui.GuiButton(
                ID_CLOSE_NO_PKT, BTN_X, y, BTN_W, BTN_H, "Close without packet"));

        y += BTN_GAP;
        buttonList.add(new net.minecraft.client.gui.GuiButton(
                ID_SAVE_GUI, BTN_X, y, BTN_W, BTN_H, "Save GUI"));

        y += BTN_GAP;
        PacketManager pm = GUIUtilsMod.getPacketManager();
        buttonList.add(new net.minecraft.client.gui.GuiButton(
                ID_DELAY, BTN_X, y, BTN_W, BTN_H,
                "Delay packets: " + pm.isDelayingPackets()));

        // Command bar at bottom-left
        commandBar = new GuiTextField(0, fontRendererObj, BTN_X, height - 24, BTN_W, 18);
        commandBar.setMaxStringLength(256);
        commandBar.setFocused(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw wrapped GUI first
        if (wrappedGui != null) {
            wrappedGui.drawScreen(mouseX, mouseY, partialTicks);
        }

        // Draw semi-transparent sidebar background
        drawRect(0, 0, BTN_X + BTN_W + 4, height, 0x99000000);

        // Refresh delay button label
        PacketManager pm = GUIUtilsMod.getPacketManager();
        for (Object btn : buttonList) {
            if (btn instanceof net.minecraft.client.gui.GuiButton) {
                net.minecraft.client.gui.GuiButton b = (net.minecraft.client.gui.GuiButton) btn;
                if (b.id == ID_DELAY) {
                    b.displayString = "Delay packets: " + pm.isDelayingPackets();
                    if (pm.isDelayingPackets()) {
                        b.displayString += " (" + pm.getQueueSize() + ")";
                    }
                }
            }
        }

        // Draw buttons on top
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Command bar
        commandBar.drawTextBox();

        // Saved GUI label
        if (SavedGUIManager.hasSavedGUI()) {
            drawString(fontRendererObj,
                    "Saved: " + SavedGUIManager.getSavedTitle(),
                    BTN_X, BTN_Y_START + BTN_GAP * 3 + 6, 0xAAAAAA);
        }
    }

    @Override
    protected void actionPerformed(net.minecraft.client.gui.GuiButton button) throws IOException {
        PacketManager pm = GUIUtilsMod.getPacketManager();

        switch (button.id) {
            case ID_CLOSE_NO_PKT:
                // Drop queued packets, close client-side only
                pm.dropQueue();
                mc.displayGuiScreen(null);
                break;

            case ID_SAVE_GUI:
                SavedGUIManager.saveCurrentGUI();
                break;

            case ID_DELAY:
                pm.toggleDelay();
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (commandBar.isFocused()) {
            if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
                String cmd = commandBar.getText().trim();
                if (!cmd.isEmpty()) {
                    if (!cmd.startsWith("/")) cmd = "/" + cmd;
                    mc.thePlayer.sendChatMessage(cmd);
                    commandBar.setText("");
                }
                return;
            }
            commandBar.textboxKeyTyped(typedChar, keyCode);
            return;
        }

        // ESC while not typing → close without sending
        if (keyCode == Keyboard.KEY_ESCAPE) {
            GUIUtilsMod.getPacketManager().dropQueue();
            mc.displayGuiScreen(null);
            return;
        }

        // Forward to wrapped GUI
        if (wrappedGui != null) {
            wrappedGui.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        commandBar.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Forward clicks outside the sidebar to wrapped GUI
        if (mouseX > BTN_X + BTN_W + 4 && wrappedGui != null) {
            wrappedGui.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (wrappedGui != null) {
            wrappedGui.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public GuiScreen getWrappedGui() {
        return wrappedGui;
    }
}
