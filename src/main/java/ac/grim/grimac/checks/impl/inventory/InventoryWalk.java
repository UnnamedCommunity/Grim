package ac.grim.grimac.checks.impl.inventory;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.VectorData;
import ac.grim.grimac.utils.data.VectorData.MoveVectorData;
import ac.grim.grimac.utils.data.VehicleData;
import ac.grim.grimac.utils.data.packetentity.PacketEntitySelf;
import ac.grim.grimac.utils.latency.CompensatedInventory;
import ac.grim.grimac.utils.nmsutil.Collisions;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import java.util.StringJoiner;

@CheckData(name = "InventoryWalk", setback = 1, decay = 0.25)
public class InventoryWalk extends Check implements PostPredictionCheck {
  private MoveVectorData lastMove, move;
  private int horseJumpVerbose;

  public InventoryWalk(GrimPlayer player) {
    super(player);
  }

  @Override
  public void onPacketReceive(PacketReceiveEvent event) {
    if (event.getPacketType() == Client.CLICK_WINDOW) {
      if (this.lastMove == null || this.move == null) {
        return;
      }

      if(this.lastMove.x != 0 && this.lastMove.z != 0 &&
         this.move.x != 0 && this.move.z != 0) {
        if(shouldModifyPackets()) {
          event.setCancelled(true);
          this.player.onPacketCancel();
        }
      }
    }
  }

  @Override
  public void onPredictionComplete(PredictionComplete predictionComplete) {
    if (!predictionComplete.isChecked() ||
        predictionComplete.getData().isTeleport() ||
        this.player.getSetbackTeleportUtil().blockOffsets) {
      return;
    }

    if(isInventoryOpen()) {
      PacketEntitySelf playerEntity = this.player.compensatedEntities.getSelf();
      if (playerEntity.inVehicle()) {
        VehicleData vehicleData = this.player.vehicleData;
        if((vehicleData.nextVehicleForward != 0 || vehicleData.nextVehicleHorizontal != 0) && flagWithSetback()) {
          alert("vehicleMove=" + vehicleData.nextVehicleForward + ", " + vehicleData.nextVehicleHorizontal);
        }

        if(vehicleData.nextHorseJump > 0) {
          // Will flag once, if player opens chest with pressed space bar
          if(this.horseJumpVerbose++ >= 1 && flagWithSetback()) {
            alert("horseJump=" + vehicleData.nextHorseJump);
          }
        }

        return;
      }

      this.lastMove = this.move;
      this.move = findMovement(this.player.predictedVelocity);

      boolean isMoving = this.move != null && (this.move.x != 0 || this.move.z != 0);

      // TODO: swimming
      // TODO: find a better way to deal with pistons?
      boolean isJumping = this.player.uncertaintyHandler.slimePistonBounces.isEmpty() &&
                          this.player.predictedVelocity.isJump();

      if (!isMoving && !isJumping) {
        return;
      }

      if(isDesynced()) {
        return;
      }

      if(flagWithSetback()) {
        closeInventory(this.player.getInventory()); // Force close inventory to prevent infinity flags

        StringJoiner joiner = new StringJoiner(" ");
        if(isMoving) {
          joiner.add("move=(" + this.move.x + ", " + this.move.z + ")");
        }

        if(isJumping) {
          joiner.add("jump");
        }

        alert(joiner.toString());
      }
    } else {
      this.horseJumpVerbose = 0;
    }
  }

  // TODO: test 1.7.10
  private boolean isDesynced() {
    CompensatedInventory inventory = this.player.getInventory();

    if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
      if (inventory.inventoryType instanceof Integer) {
        int inventoryType = (int) inventory.inventoryType;

        // Closing beacon inventory via cross will cause desync
        if (inventoryType == 8) {
          closeInventory(inventory);
          return true;
        }
      }
    }

    if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_1)) {
      // TODO: this allows player to move ~0.15 blocks without flagging this check, can this be improved without huge mess?
      SimpleCollisionBox possibleHitbox = this.player.boundingBox.copy().expand(0.2f);

      if (Collisions.hasMaterial(this.player, possibleHitbox, (state, position) -> state.getType() == StateTypes.NETHER_PORTAL)) {
        closeInventory(inventory);
        return true; // Opening something inside ender portal will cause desync
      }
    }

    return false;
  }

  private void closeInventory(CompensatedInventory inventory) {
    // TODO: dont run it many times
    int windowId = inventory.openWindowID;
    this.player.user.sendPacket(new WrapperPlayServerCloseWindow(windowId));

    this.player.sendTransaction();
    this.player.latencyUtils.addRealTimeTask(this.player.lastTransactionSent.get(), () -> {
      PacketEvents.getAPI().getProtocolManager().receivePacket(this.player.user.getChannel(), new WrapperPlayClientCloseWindow(windowId));
    });
  }

  public boolean isInventoryOpen() {
    return this.player.getInventory().isOpen;
  }

  private MoveVectorData findMovement(VectorData vectorData) {
    if(!vectorData.isPlayerInput()) {
      return null;
    }

    if (vectorData instanceof MoveVectorData) {
      return (MoveVectorData) vectorData;
    }

    while (vectorData != null) {
      vectorData = vectorData.lastVector;
      if (vectorData instanceof MoveVectorData) {
        return (MoveVectorData) vectorData;
      }
    }

    return null;
  }
}
