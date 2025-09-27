package net.ultimatumof.util;

import net.minecraft.util.math.Vec3d;

public class TriangulationInfo {
    public double x1, z1, x2, z2, theta1, theta2;

    public TriangulationInfo(Vec3d start, double angle) {
        this.x1 = start.x;
        this.z1 = start.z;
        this.theta1 = angle;
        this.x2 = Double.NaN;
        this.z2 = Double.NaN;
        this.theta2 = Double.NaN;
    }

    public void update(Vec3d next, double angle) {
        this.x2 = next.x;
        this.z2 = next.z;
        this.theta2 = angle;
    }

    //chatgpt
    public double[] findIntersection() {
        // Convert angles to radians
        double rad1 = Math.toRadians(theta1);
        double rad2 = Math.toRadians(theta2);

        // Direction vectors for each ray
        double dx1 = -Math.sin(rad1);
        double dz1 = Math.cos(rad1);
        double dx2 = -Math.sin(rad2);
        double dz2 = Math.cos(rad2);

        // Solve for intersection:
        // (x1, z1) + t1*(dx1, dz1) = (x2, z2) + t2*(dx2, dz2)
        double det = dx1 * (-dz2) - dz1 * (-dx2);

        if (Math.abs(det) < 1e-8) {
            // Rays are parallel (no intersection or infinite)
            return null;
        }

        double dx = x2 - x1;
        double dz = z2 - z1;

        double t1 = (dx * (-dz2) - dz * (-dx2)) / det;
        double t2 = (dx * dz1 - dz * dx1) / (dx2 * dz1 - dz2 * dx1);

        double ix = x1 + t1 * dx1;
        double iz = z1 + t1 * dz1;

        return new double[]{ix, iz};
    }
}
