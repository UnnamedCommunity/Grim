package ac.grim.grimac.utils.math;

import com.github.retrooper.packetevents.protocol.world.BlockFace;

public final class Vector {
    private double x, y, z;

    public Vector() { }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    public Vector setY(double y) {
        this.y = y;
        return this;
    }

    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    public Vector set(Vector vector) {
        return set(vector.x, vector.y, vector.z);
    }

    public Vector set(BlockFace blockFace) {
        return set(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
    }

    public Vector set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector add(Vector vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public Vector add(BlockFace blockFace) {
        return add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
    }

    public Vector add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector addXZ(double x, double z) {
        this.x += x;
        this.z += z;
        return this;
    }

    public Vector addX(double x) {
        this.x += x;
        return this;
    }

    public Vector addY(double y) {
        this.y += y;
        return this;
    }

    public Vector addZ(double z) {
        this.z += z;
        return this;
    }

    public Vector sub(Vector vector) {
        return sub(vector.x, vector.y, vector.z);
    }

    public Vector sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector sub(double v) {
        this.x -= v;
        this.y -= v;
        this.z -= v;
        return this;
    }

    public Vector mul(Vector vector) {
        return mul(vector.x, vector.y, vector.z);
    }

    public Vector mul(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vector mulXZ(double x, double z) {
        this.x *= x;
        this.z *= z;
        return this;
    }

    public Vector mul(double v) {
        this.x *= v;
        this.y *= v;
        this.z *= v;
        return this;
    }

    public Vector copy() {
        return new Vector(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;
        Vector that = (Vector) o;
        return Double.compare(that.x, this.x) == 0 &&
               Double.compare(that.y, this.y) == 0 &&
               Double.compare(that.z, this.z) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(this.x);
        int result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Vector{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    public double distanceSquared(Vector vector) {
        return distanceSquared(vector.x, vector.y, vector.z);
    }

    public double distanceSquared(double x, double y, double z) {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2);
    }

    public double distance(Vector vector) {
        return distance(vector.x, vector.y, vector.z);
    }

    public double distance(double x, double y, double z) {
        return Math.sqrt(distanceSquared(x, y, z));
    }

    public double lengthSquared() {
        return Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vector normalize() {
        double length = length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    public double dot(Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector cross(Vector o) {
        double nX = y * o.z - o.y * z;
        double nY = z * o.x - o.z * x;
        double nZ = x * o.y - o.x * y;

        this.x = nX;
        this.y = nY;
        this.z = nZ;

        return this;
    }
}
