package net.ultimatumof;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.ultimatumof.locator.LocatorDebugger;
import net.ultimatumof.locator.LocatorWaypointHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorModClient implements ClientModInitializer {
	public static final String MOD_ID = "locator-mod";
	public static final LocatorWaypointHandler HANDLER = new LocatorWaypointHandler();
	public static final LocatorDebugger DEBUGGER = new LocatorDebugger();
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitializeClient() {
		LOGGER.info("Init Locator Mod Client.");

		//Register locator command
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("find")
				.then(ClientCommandManager.argument("player", StringArgumentType.word())
				.executes(context -> {
					String player = StringArgumentType.getString(context, "player");

					if (!MinecraftClient.getInstance().getNetworkHandler().getWaypointHandler().hasWaypoint()) {
						context.getSource().sendFeedback(Text.literal("Waypoints are not enabled.").formatted(Formatting.RED));
						return -1;
					}

					if (MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(player) == null) {
						context.getSource().sendFeedback(Text.literal("Couldn't find player " + player + ". Are you sure you typed it correctly? (it is case-sensitive!)").formatted(Formatting.RED));
						return -1;
					}

					context.getSource().sendFeedback(Text.literal("Starting tracking ").append(Text.literal(player).formatted(Formatting.GOLD)).append("."));
					LocatorModClient.HANDLER.changeTracking(player);

					return 1;
				}))));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("locatorDebug")
				.executes(context -> {
					DEBUGGER.enabled = !DEBUGGER.enabled;
					context.getSource().sendFeedback(Text.literal("debugging=" + DEBUGGER.enabled));

					return 1;
				})));
	}
}