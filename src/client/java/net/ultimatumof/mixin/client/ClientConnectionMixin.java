package net.ultimatumof.mixin.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.WaypointS2CPacket;
import net.ultimatumof.LocatorModClient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
	@Inject(method = "channelRead0", at = @At("HEAD"))
	private void onReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		//integrated server glitch - why are you in singleplayer anyways?
		if (packet.getPacketType().side() != NetworkSide.CLIENTBOUND) {
			return;
		}

		LocatorModClient.DEBUGGER.handlePacket(packet);

		//yet another scuffed workaround
		if (packet instanceof WaypointS2CPacket packet2) {
			packet2.apply(LocatorModClient.HANDLER);
		}

		if (packet instanceof PlayerPositionLookS2CPacket packet2) {
			LocatorModClient.HANDLER.handleTeleport(packet2);
		}
	}

	@Inject(method = "sendImmediately", at = @At("HEAD"))
	private void sendImmediately(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
		//integrated server glitch - why are you in singleplayer anyways?
		if (packet.getPacketType().side() != NetworkSide.SERVERBOUND) {
			return;
		}

		LocatorModClient.DEBUGGER.handlePacket(packet);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void tick(CallbackInfo ci) {
		LocatorModClient.HANDLER.onTick();
	}
}