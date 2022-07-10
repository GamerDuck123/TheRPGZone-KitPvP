/*
 * Scaffolding - Schematic library for Minestom
 *  Copyright (c) 2022 SLLCoding <luisjk266@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the “Software”), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.gamerduck.kitpvp.api.scaffolding.schematic.impl;

import com.gamerduck.kitpvp.api.scaffolding.schematic.Region;
import com.gamerduck.kitpvp.api.scaffolding.schematic.Schematic;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.collections.ImmutableByteArray;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;

import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

// https://github.com/EngineHub/WorldEdit/blob/303f5a76b2df70d63480f2126c9ef4b228eb3c59/worldedit-core/src/main/java/com/sk89q/worldedit/extent/clipboard/io/SpongeSchematicReader.java#L261-L297
public class SpongeSchematic implements Schematic {

    private final List<Region.Block> regionBlocks = new ArrayList<>();

    private short width;
    private short height;
    private short length;
    private Map<String, Integer> palette = new HashMap<>();
    private byte[] blocksData;

    private boolean read = false;

    private int offsetX;
    private int offsetY;
    private int offsetZ;

    @Override
    public void read(@NotNull NBTCompound nbtTag) throws NBTException {
        readSizes(nbtTag);
        readBlockPalette(nbtTag);
        readOffsets(nbtTag);
        readBlocks();
        read = true;
    }

    private void readOffsets(@NotNull NBTCompound nbtTag) throws NBTException {
        NBTCompound metaData = nbtTag.getCompound("Metadata");
        if (metaData == null) throw new NBTException("Invalid Schematic: No Metadata");

        Integer weOffsetX = metaData.getInt("WEOffsetX");
        if (weOffsetX == null) throw new NBTException("Invalid Schematic: No WEOffsetX In Metadata");
        this.offsetX = weOffsetX;

        Integer weOffsetY = metaData.getInt("WEOffsetY");
        if (weOffsetY == null) throw new NBTException("Invalid Schematic: No WEOffsetY In Metadata");
        this.offsetY = weOffsetY;

        Integer weOffsetZ = metaData.getInt("WEOffsetZ");
        if (weOffsetZ == null) throw new NBTException("Invalid Schematic: No WEOffsetZ In Metadata");
        this.offsetZ = weOffsetZ;
    }

    private void readSizes(@NotNull NBTCompound nbtTag) throws NBTException {
        Short width = nbtTag.getShort("Width");
        if (width == null) throw new NBTException("Invalid Schematic: No Width");
        this.width = width;

        Short height = nbtTag.getShort("Height");
        if (height == null) throw new NBTException("Invalid Schematic: No Height");
        this.height = height;

        Short length = nbtTag.getShort("Length");
        if (length == null) throw new NBTException("Invalid Schematic: No Length");
        this.length = length;
    }

    private void readBlockPalette(@NotNull NBTCompound nbtTag) throws NBTException {
        Integer maxPalette = nbtTag.getInt("PaletteMax");
        if (maxPalette == null) throw new NBTException("Invalid Schematic: No PaletteMax");

        NBTCompound nbtPalette = (NBTCompound) nbtTag.get("Palette");
        if (nbtPalette == null) throw new NBTException("Invalid Schematic: No Palette");

        Set<String> keys = nbtPalette.getKeys();
        if (keys.size() != maxPalette) throw new NBTException("Invalid Schematic: PaletteMax does not match Palette size");

        for (String key : keys) {
            Integer value = nbtPalette.getInt(key);
            if (value == null) throw new NBTException("Invalid Schematic: Palette contains invalid value");

            palette.put(key, value);
        }

        palette = palette.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);

        ImmutableByteArray blocksData = nbtTag.getByteArray("BlockData");
        if (blocksData == null || blocksData.getSize() == 0) throw new NBTException("Invalid Schematic: No BlockData");
        this.blocksData = blocksData.copyArray();
    }

    private void readBlocks() throws NBTException {
        int index = 0;
        int i = 0;
        int value;
        int varIntLength;
        List<String> paletteKeys = new ArrayList<>(palette.keySet());

        while (i < this.blocksData.length) {
            value = 0;
            varIntLength = 0;

            while (true) {
                value |= (this.blocksData[i] & 127) << (varIntLength++ * 7);
                if (varIntLength > 5) throw new NBTException("Invalid Schematic: BlockData has invalid length");
                if ((this.blocksData[i] & 128) != 128) {
                    i++;
                    break;
                }
                i++;
            }

            int x = (index % (width * length)) % width;
            int y = index / (width * length);
            int z = (index % (width * length)) / width;

            String block = paletteKeys.get(value);
            short stateId = getStateId(block);

            this.regionBlocks.add(new Region.Block(new Pos(x + offsetX, y + offsetY, z + offsetZ), stateId));

            index++;
        }
    }

    @Override
    public void write(@NotNull OutputStream outputStream, @NotNull Region region) {
        // TODO: Complete
    }

    @Override
    public CompletableFuture<Region> build(Instance instance, Point position) {
        if (!read) throw new IllegalStateException("Schematic not read");
        CompletableFuture<Region> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            AbsoluteBlockBatch blockBatch = new AbsoluteBlockBatch();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Region.Block regionBlock : regionBlocks) {
                Pos absoluteBlockPosition = regionBlock.position().add(position);
                short stateId = regionBlock.stateId();

                Block block = Block.fromStateId(stateId);
                if (block != null) futures.add(instance.loadOptionalChunk(absoluteBlockPosition).thenRun(() -> blockBatch.setBlock(absoluteBlockPosition, block)));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
            blockBatch.apply(instance, () -> future.complete(new Region(instance, position, position.add(width, height, length))));
        });
        return future;
    }

    private Block getBlock(@NotNull String input) {
        String namespaceId = input.split("\\[")[0];

        return Block.fromNamespaceId(namespaceId);
    }

    private short getStateId(@NotNull String input) {
        Block block = getBlock(input);
        if (block == null) return 0;
        String states = input.replaceAll(block.name(), "");

        if (states.startsWith("[")) {
            String[] stateArray = states.substring(1, states.length() - 1).split(",");
            Map<String, String> properties = new HashMap<>(block.properties());
            for (String state : stateArray) {
                String[] split = state.split("=");
                properties.replace(split[0], split[1]);
            }
            try {
                return block.withProperties(properties).stateId();
            } catch (Exception e) {
                e.printStackTrace();
                return block.stateId();
            }
        } else return block.stateId();
    }

    @Override
    public short getWidth() {
        return width;
    }

    @Override
    public short getHeight() {
        return height;
    }

    @Override
    public short getLength() {
        return length;
    }

    @Override
    public int getOffsetX() {
        return offsetX;
    }

    @Override
    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public int getOffsetZ() {
        return offsetZ;
    }

    @Override
    public void apply(@NotNull Block.Setter setter) {
        for (Region.Block block : regionBlocks) {
            Pos pos = block.position();
            Block minestomBlock = Block.fromStateId(block.stateId());
            if (minestomBlock != null)  {
                setter.setBlock(pos, minestomBlock);
            } else {
                throw new IllegalStateException("Invalid block state id: " + block.stateId());
            }
        }
    }

}
