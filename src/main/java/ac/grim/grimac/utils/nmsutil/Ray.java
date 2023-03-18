package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.Pair;
import ac.grim.grimac.utils.math.Vector;

// Copied directly from Hawk
public class Ray {

    private Vector origin;
    private Vector direction;

    public Ray(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Ray(GrimPlayer player, double x, double y, double z, float xRot, float yRot) {
        this.origin = new Vector(x, y, z);
        this.direction = calculateDirection(player, xRot, yRot);
    }

    // Account for FastMath by using player's trig handler
    // Copied from hawk which probably copied it from NMS
    public static Vector calculateDirection(GrimPlayer player, float xRot, float yRot) {
        Vector vector = new Vector();
        float rotX = (float) Math.toRadians(xRot);
        float rotY = (float) Math.toRadians(yRot);
        vector.setY(-player.trigHandler.sin(rotY));
        double xz = player.trigHandler.cos(rotY);
        vector.setX(-xz * player.trigHandler.sin(rotX));
        vector.setZ(xz * player.trigHandler.cos(rotX));
        return vector;
    }

    public String toString() {
        return "origin: " + origin + " direction: " + direction;
    }

    public Vector getPointAtDistance(double distance) {
        return origin.copy().add(direction.copy().mul(distance));
    }

    //https://en.wikipedia.org/wiki/Skew_lines#Nearest_Points
    public Pair<Vector, Vector> closestPointsBetweenLines(Ray other) {
        Vector n1 = direction.copy().cross(other.direction.copy().cross(direction));
        Vector n2 = other.direction.copy().cross(direction.copy().cross(other.direction));

        Vector c1 = origin.copy().add(direction.copy().mul(other.origin.copy().sub(origin).dot(n2) / direction.dot(n2)));
        Vector c2 = other.origin.copy().add(other.direction.copy().mul(origin.copy().sub(other.origin).dot(n1) / other.direction.dot(n1)));

        return new Pair<>(c1, c2);
    }

    public Vector getOrigin() {
        return origin;
    }

    public Vector calculateDirection() {
        return direction;
    }
}
