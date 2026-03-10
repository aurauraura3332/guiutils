package com.guiutils.core;

import com.guiutils.keybind.KeybindManager;
import com.guiutils.network.PacketManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = GUIUtilsMod.MODID,
    name = GUIUtilsMod.NAME,
    version = GUIUtilsMod.VERSION,
    acceptedMinecraftVersions = "[1.8.9]"
)
public class GUIUtilsMod {

    public static final String MODID   = "guiutils";
    public static final String NAME    = "GUI Utils";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.Instance(MODID)
    public static GUIUtilsMod INSTANCE;

    private static PacketManager packetManager;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        packetManager = new PacketManager();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(packetManager);
        MinecraftForge.EVENT_BUS.register(new KeybindManager());
        KeybindManager.registerKeybinds();
        LOGGER.info("GUI Utils initialized.");
    }

    public static PacketManager getPacketManager() {
        return packetManager;
    }
}
