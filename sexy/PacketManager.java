package com.guiutils.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages packet interception:
 *  - delayPackets: queues outgoing packets instead of sending them
 *  - flushQueue:   sends all queued packets at once
 *  - closeWithoutPacket: drops all queued packets (client-side close only)
 */
public class PacketManager {

    private boolean delayPackets = false;
    private final List<Packet<?>> packetQueue = new ArrayList<>();

    // ------------------------------------------------------------------ //
    //  Public API
    // ------------------------------------------------------------------ //

    public boolean isDelayingPackets() {
        return delayPackets;
    }

    public void setDelayPackets(boolean delay) {
        this.delayPackets = delay;
        if (!delay) {
            flushQueue();
        }
    }

    public void toggleDelay() {
        setDelayPackets(!delayPackets);
    }

    /** Sends every queued packet and empties the queue. */
    public void flushQueue() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.getNetHandler() == null) {
            packetQueue.clear();
            return;
        }
        for (Packet<?> pkt : new ArrayList<>(packetQueue)) {
            mc.getNetHandler().getNetworkManager().sendPacket(pkt);
        }
        packetQueue.clear();
    }

    /** Drops all queued packets without sending (used by Close Without Packet). */
    public void dropQueue() {
        packetQueue.clear();
    }

    public int getQueueSize() {
        return packetQueue.size();
    }

    /**
     * Called by the Mixin / packet hook before a packet is sent.
     * Returns true if the packet was intercepted (caller should NOT send it).
     */
    public boolean interceptPacket(Packet<?> packet) {
        if (delayPackets) {
            packetQueue.add(packet);
            return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ //
    //  Forge events – reset state on disconnect
    // ------------------------------------------------------------------ //

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        packetQueue.clear();
        delayPackets = false;
    }
}
