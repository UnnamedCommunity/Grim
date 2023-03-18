package ac.grim.grimac.predictionengine.predictions;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.VectorData;
import ac.grim.grimac.utils.math.Vector;

import java.util.HashSet;
import java.util.Set;

public class PredictionEngineLava extends PredictionEngine {
    @Override
    public void addJumpsToPossibilities(GrimPlayer player, Set<VectorData> existingVelocities) {
        for (VectorData vector : existingVelocities.toArray(new VectorData[0])) {
            existingVelocities.add(new VectorData(vector.vector.copy().addY(0.04), vector, VectorData.VectorType.Jump));

            if (player.slightlyTouchingLava && player.lastOnGround && !player.onGround) {
                Vector withJump = vector.vector.copy();
                super.doJump(player, withJump);
                existingVelocities.add(new VectorData(withJump, vector, VectorData.VectorType.Jump));
            }
        }
    }
}
