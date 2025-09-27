package net.ultimatumof.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.TrackedWaypointHandler;
import net.ultimatumof.LocatorMod;
import net.ultimatumof.LocatorModClient;

public class LocatorWaypointHandler implements TrackedWaypointHandler {
    private String tracking;
    private TriangulationInfo info = new TriangulationInfo(new Vec3d(Double.NaN, Double.NaN, Double.NaN), Double.NaN);;

    @Override
    public void onTrack(TrackedWaypoint waypoint) {
        if (waypoint.getClass().getSimpleName().contains("Azimuth")) {
            handleAzimuth(waypoint);
        }
    }

    @Override
    public void onUpdate(TrackedWaypoint waypoint) {
        if (waypoint.getClass().getSimpleName().contains("Azimuth")) {
            handleAzimuth(waypoint);
        }
    }

    @Override
    public void onUntrack(TrackedWaypoint waypoint) {

    }

    private void handleAzimuth(TrackedWaypoint waypoint) {
        final String[] name = new String[1];
        waypoint.getSource().ifLeft((uuid) -> {
            try {
                name[0] = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(uuid).getProfile().getName();
            } catch (Exception e) {
                name[0] = tracking == null ? "" : tracking;
            }
        }).ifRight((a) -> {
            name[0] = a;
        });
        if (name[0] == null || !name[0].equals(tracking)) {
            return;
        }
        double angle = waypoint.getRelativeYaw(MinecraftClient.getInstance().world, new TrackedWaypoint.YawProvider() {
            @Override
            public float getCameraYaw() {
                return 0;
            }

            @Override
            public Vec3d getCameraPos() {
                return null;
            }
        });
        angle = (angle + 360) % 360;
        Vec3d pos = MinecraftClient.getInstance().player.getPos();
        send(Text.literal("Received angle packet for ").formatted(Formatting.GRAY)
                .append(Text.literal(tracking).formatted(Formatting.GOLD))
                .append(Text.literal(" at " + Math.round(pos.x * 1000.0) / 1000.0 + " " + Math.round(pos.y * 1000.0) / 1000.0 + " " + Math.round(pos.z * 1000.0) / 1000.0 + " with angle " + Math.round(angle * 1000.0) / 1000.0).formatted(Formatting.GRAY)));
        if (Double.isNaN(info.x1)) {
            this.info = new TriangulationInfo(pos, angle);
        } else {
            this.info.update(pos, angle);
            double[] data = this.info.findIntersection();
            send(Text.literal("Found intersection at x=").formatted(Formatting.WHITE)
                    .append(Text.literal(Math.round(data[0] * 1000.0) / 1000.0 + "").formatted(Formatting.GOLD).formatted(Formatting.BOLD))
                    .append(Text.literal(" y=").formatted(Formatting.WHITE))
                    .append(Text.literal(Math.round(data[1] * 1000.0) / 1000.0 + "").formatted(Formatting.GOLD).formatted(Formatting.BOLD)
                    ));
            changeTracking(null);
        }
    }

    public void changeTracking(String tracking) {
        this.tracking = tracking;
        this.info = new TriangulationInfo(new Vec3d(Double.NaN, Double.NaN, Double.NaN), Double.NaN);
    }

    private void send(Text s) {
        MinecraftClient.getInstance().player.sendMessage(s, false);
    }
}
