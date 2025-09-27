package net.ultimatumof.mixin.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.bar.LocatorBar;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.WaypointS2CPacket;
import net.ultimatumof.LocatorModClient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

	@Inject(method = "sendInternal", at = @At("HEAD"))
	private void onSend(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
		if (packet.getPacketType().side() != NetworkSide.SERVERBOUND) {
			return;
		}
	}

	@Inject(method = "channelRead0", at = @At("HEAD"))
	private void onReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		if (packet.getPacketType().side() != NetworkSide.CLIENTBOUND) {
			return;
		}
		if (packet instanceof WaypointS2CPacket packet2) {
			packet2.apply(LocatorModClient.HANDLER);
		}
	}
}