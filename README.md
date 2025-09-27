# Locator-Mod

This mod uses waypoint packets to triangulate the precise coordinates of any player on a server.

# Technical

Waypoint packet types:

Azimuth - stores angle from North to target (long range)

ChunkBased - stores chunk of target (medium range?)

Position - stores exact position of target (close range)

I only consider Azimuth packets because if you were to get any others, you are close enough to just find them normally.
To locate the player, the mod will start tracking incoming waypoint packets and check to see if they give information about the player. If they do, it will store the player location and azimuth angle, alongside 1 other packet. It will then use the information to obtain the coordinates of target using the intersection of 2 rays:

(x1, z1) + t1*(dx1, dz1) = (x2, z2) + t2*(dx2, dz2)

x = x1 + t1 * dx1

z = z1 + t1 * dz1

(x1, x2, z1, z2 represent the coordinates of the positions)
(dx1, dx2, dz1, dx2 represent direction vectors for the positions) 

# Notice

This only works if target is stationary, I'll maybe fix this in a later version.
