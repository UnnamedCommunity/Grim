package ac.grim.grimac.utils.blockstate;

import ac.grim.grimac.utils.latency.CompensatedWorld;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_7.Chunk_v1_7;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8.Chunk_v1_8;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;

public final class ChunkUnsafe {
    private static final MethodHandle DATA_PALETTE;
    private static final WrappedBlockState[] STATES;

    static {
        try {
            Method getMappingsIndex = WrappedBlockState.class.getDeclaredMethod("getMappingsIndex", ClientVersion.class);
            getMappingsIndex.setAccessible(true);
            Byte versionId = (Byte) getMappingsIndex.invoke(null, CompensatedWorld.blockVersion);

            Field byId = WrappedBlockState.class.getDeclaredField("BY_ID");
            byId.setAccessible(true);
            Map<Byte, Map<Integer, WrappedBlockState>> BY_ID = (Map<Byte, Map<Integer, WrappedBlockState>>) byId.get(null);
            Map<Integer, WrappedBlockState> ID = BY_ID.get(versionId);

            int id = 0;
            for (int stateId : ID.keySet().stream().mapToInt(Integer::intValue).sorted().toArray()) {
                if (stateId != id++) {
                    throw new IllegalStateException("invalid id " + stateId + " at " + (id - 1));
                }
            }

            STATES = ID.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).map(Map.Entry::getValue).toArray(WrappedBlockState[]::new);

            Field dataPalette = Chunk_v1_9.class.getDeclaredField("dataPalette");
            dataPalette.setAccessible(true);
            DATA_PALETTE = MethodHandles.lookup().unreflectGetter(dataPalette);
        } catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }

    public static int getStateId(BaseChunk chunk, int x, int y, int z) {
        if (chunk instanceof Chunk_v1_18) {
            return ((Chunk_v1_18) chunk).getChunkData().get(x, y, z);
        } else if (chunk instanceof Chunk_v1_9) {
            try {
                return ((DataPalette) DATA_PALETTE.invokeExact((Chunk_v1_9) chunk)).get(x, y, z);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else if (chunk instanceof Chunk_v1_8) {
            return ((Chunk_v1_8) chunk).getBlocks().get(x, y, z);
        } else if (chunk instanceof Chunk_v1_7) {
            Chunk_v1_7 v1_7 = ((Chunk_v1_7) chunk);
            return v1_7.getBlocks().get(x, y, z) | v1_7.getExtendedBlocks().get(x, y, z) << 12;
        } else {
            throw new IllegalStateException("invalid chunk type: " + chunk.getClass().getName());
        }
    }

    public static WrappedBlockState getState(BaseChunk chunk, int x, int y, int z) {
        return getByGlobalId(getStateId(chunk, x, y, z));
    }

    public static WrappedBlockState getByGlobalId(int combinedID) {
        return combinedID == 0 ? STATES[0] : getByGlobalIdUnsafe(combinedID).clone();
    }

    public static WrappedBlockState getStateUnsafe(BaseChunk chunk, int x, int y, int z) {
        return getByGlobalIdUnsafe(getStateId(chunk, x, y, z));
    }

    public static WrappedBlockState getByGlobalIdUnsafe(int combinedID) {
        return combinedID >= 0 && combinedID < STATES.length ? STATES[combinedID] : STATES[0];
    }
}
