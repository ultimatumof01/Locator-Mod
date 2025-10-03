package net.ultimatumof.locator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.WaypointS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.ultimatumof.LocatorModClient;
import org.slf4j.Logger;

public class LocatorDebugger {
    public static final Logger LOGGER = LocatorModClient.LOGGER;
    public boolean enabled;

    public void handlePacket(Packet<?> p) {
        if (!enabled) {
            return;
        }
        if (p.getPacketType().side() == NetworkSide.SERVERBOUND) {
            if (p instanceof PlayerMoveC2SPacket) {
                send("DEBUG: (c-s) move player packet");
            }
        } else {
            if (p instanceof WaypointS2CPacket) {
                send("DEBUG: (s-c) waypoint packet");
            }
            if (p instanceof PlayerPositionLookS2CPacket) {
                send("DEBUG: (s-c) player teleport "+((PlayerPositionLookS2CPacket) p).change().position().getY());
            }
        }
    }

    private void send(String s) {
        MinecraftClient.getInstance().player.sendMessage(Text.literal(s).formatted(Formatting.GRAY), false);
    }
}
