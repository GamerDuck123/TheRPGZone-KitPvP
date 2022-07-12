package com.gamerduck.kitpvp.api.scaffolding.schematic;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.DynamicChunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

// TODO: Entities?
@SuppressWarnings("UnstableApiUsage")
public class SchematicChunkLoader implements IChunkLoader {

    private final @NotNull Function<@NotNull Chunk, @NotNull CompletableFuture<Void>> saveHandler;
    private final Long2ObjectMap<ChunkBatch> batches = new Long2ObjectOpenHashMap<ChunkBatch>();

    private SchematicChunkLoader(
            @NotNull Function<@NotNull Chunk, @NotNull CompletableFuture<Void>> saveHandler,
            @NotNull Collection<Schematic> schematics,
            int offsetX,
            int offsetY,
            int offsetZ
    ) {
        this.saveHandler = saveHandler;

        // The block setter used for Schematic#apply
        Block.Setter setter = (x, y, z, block) -> {
            int chunkX = ChunkUtils.getChunkCoordinate(x);
            int chunkZ = ChunkUtils.getChunkCoordinate(z);
            long index = ChunkUtils.getChunkIndex(chunkX, chunkZ);

            // Get the batch, create it if it doesn't exist
            ChunkBatch batch = batches.computeIfAbsent(index, key -> new ChunkBatch());

            // Add the block to the batch
            batch.setBlock(x + offsetX, y + offsetY, z + offsetZ, block);
        };

        // Apply the schematics
        for (Schematic schematic : schematics) {
            schematic.apply(setter);
        }
    }

    /**
     * Creates a builder for a {@link SchematicChunkLoader}.
     *
     * @return The builder.
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
        long index = ChunkUtils.getChunkIndex(chunkX, chunkZ);
        ChunkBatch batch = batches.get(index);

        if (batch == null) {
            return CompletableFuture.completedFuture(null);
        }

        DynamicChunk chunk = new DynamicChunk(instance, chunkX, chunkZ);
        CompletableFuture<Chunk> future = new CompletableFuture<>();
        batch.apply(instance, chunk, future::complete);

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> saveChunk(@NotNull Chunk chunk) {
        return saveHandler.apply(chunk);
    }

    public static class Builder {

        private final List<Schematic> schematics = new ArrayList<>();
        private @NotNull Function<@NotNull Chunk, @NotNull CompletableFuture<Void>> handler = chunk ->
                CompletableFuture.completedFuture(null);
        private int xOffset;
        private int yOffset;
        private int zOffset;

        private Builder() {
        }

        /**
         * Adds a schematic to this chunk loader.
         * <br><br>
         * Note that schematics are loaded in the order they are added.
         * <br>
         * This means that the last added schematic is the only schematic that is guaranteed to have all its data.
         *
         * @param schematic The schematic to add.
         * @return This builder.
         */
        // TODO: Add a way to position schematics within the instance.
        public @NotNull Builder addSchematic(@NotNull Schematic schematic) {
            schematics.add(schematic);
            return this;
        }

        /**
         * Specifies the offset that applies to all schematics added to this chunk loader.
         *
         * @param x The x offset.
         * @param y The y offset.
         * @param z The z offset.
         * @return This builder.
         */
        public @NotNull Builder offset(int x, int y, int z) {
            this.xOffset = x;
            this.yOffset = y;
            this.zOffset = z;
            return this;
        }

        /**
         * Specifies the handler to use to save the chunks.
         *
         * @param handler The handler.
         * @return This builder.
         */
        public @NotNull Builder saveChunkHandler(@NotNull Function<@NotNull Chunk, @NotNull CompletableFuture<Void>> handler) {
            this.handler = handler;
            return this;
        }

        public @NotNull SchematicChunkLoader build() {
            return new SchematicChunkLoader(handler, List.copyOf(schematics), xOffset, yOffset, zOffset);
        }

    }

}
