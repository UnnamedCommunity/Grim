package ac.grim.grimac.utils.data;

import ac.grim.grimac.utils.math.Vector;

import java.util.Objects;

public class VectorData {
    public VectorData lastVector;
    public Vector vector;

    private final int debuggingData;

    // For handling replacing the type of vector it is while keeping data
    public VectorData(Vector vector, VectorData lastVector, int vectorType) {
        this.vector = vector;
        this.lastVector = lastVector;

        if (lastVector != null) {
            this.debuggingData = lastVector.debuggingData | vectorType;
        } else {
            this.debuggingData = vectorType;
        }
    }

    public VectorData(Vector vector, int vectorType) {
        this.vector = vector;
        this.debuggingData = vectorType;
    }

    public VectorData returnNewModified(int type) {
        return new VectorData(vector, this, type);
    }

    public VectorData returnNewModified(Vector newVec, int type) {
        return new VectorData(newVec, this, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorData that = (VectorData) o;
        return Objects.equals(vector, that.vector);
    }

    @Override
    public int hashCode() {
        return this.vector != null ? this.vector.hashCode() : 0;
    }

    public boolean isKnockback() {
        return (debuggingData & VectorType.Knockback) != 0;
    }

    public boolean isFirstBreadKb() {
        return (debuggingData & VectorType.FirstBreadKnockback) != 0;
    }

    public boolean isExplosion() {
        return (debuggingData & VectorType.Explosion) != 0;
    }

    public boolean isFirstBreadExplosion() {
        return (debuggingData & VectorType.FirstBreadExplosion) != 0;
    }

    public boolean isTrident() {
        return (debuggingData & VectorType.Trident) != 0;
    }

    public boolean isZeroPointZeroThree() {
        return (debuggingData & VectorType.ZeroPointZeroThree) != 0;
    }

    public boolean isSwimHop() {
        return (debuggingData & VectorType.Swimhop) != 0;
    }

    public boolean isFlipSneaking() {
        return (debuggingData & VectorType.Flip_Sneaking) != 0;
    }

    public boolean isFlipItem() {
        return (debuggingData & VectorType.Flip_Use_Item) != 0;
    }

    public boolean isJump() {
        return (debuggingData & VectorType.Jump) != 0;
    }

    public boolean isAttackSlow() {
        return (debuggingData & VectorType.AttackSlow) != 0;
    }

    public int getScore() {
        int score = 0;
        if (this.debuggingData != 0) {
            if (isExplosion()) score -= 5;
            if (isKnockback()) score -= 5;
            if (isFirstBreadExplosion()) score += 1;
            if (isFirstBreadKb()) score += 1;
            if (isFlipItem()) score += 3;
            if (isZeroPointZeroThree()) score -= 1;
        }
        return score;
    }

    @Override
    public String toString() {
        return "VectorData{" +
                "pointThree=" + isZeroPointZeroThree() +
                ", vector=" + vector +
                '}';
    }

    // TODO: This is a stupid idea that slows everything down, remove it! There are easier ways to debug grim.
    // Would make false positives really easy to fix
    // But seriously, we could trace the code to find the mistake
    public static final class VectorType {
        public static final int Normal = 0;
        public static final int Climbable = 0;
        public static final int HackyClimbable = 0;
        public static final int Teleport = 0;
        public static final int SkippedTicks = 0;
        public static final int InputResult = 0;
        public static final int StuckMultiplier = 0;
        public static final int Spectator = 0;
        public static final int Dead = 0;
        public static final int SurfaceSwimming = 0;
        public static final int SwimmingSpace = 0;
        public static final int BestVelPicked = 0;
        public static final int Firework = 0;
        public static final int Lenience = 0;
        public static final int TridentJump = 0;
        public static final int SlimePistonBounce = 0;
        public static final int Entity_Pushing = 0;

        public static final int Knockback = 1 << 0;
        public static final int FirstBreadKnockback = 1 << 1;
        public static final int Explosion = 1 << 2;
        public static final int FirstBreadExplosion = 1 << 3;
        public static final int Trident = 1 << 4;
        public static final int ZeroPointZeroThree = 1 << 5;
        public static final int Swimhop = 1 << 6;
        public static final int Flip_Sneaking = 1 << 7;
        public static final int Flip_Use_Item = 1 << 8;
        public static final int Jump = 1 << 9;
        public static final int AttackSlow = 1 << 10;
    }
}
