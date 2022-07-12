package com.gamerduck.kitpvp.api.scaffolding.schematic;

import com.gamerduck.kitpvp.api.scaffolding.schematic.impl.MCEditSchematic;
import com.gamerduck.kitpvp.api.scaffolding.schematic.impl.SpongeSchematic;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Scaffolding {

    /**
     * Automatically detects the type of schematic and parses the input stream
     *
     * @param inputStream Schematic input
     * @return parsed schematic
     * @throws IOException  if the input stream is invalid
     * @throws NBTException if the schematic is invalid
     */
    public static @Nullable Schematic fromStream(@NotNull InputStream inputStream) throws IOException, NBTException {
        NBTReader reader = new NBTReader(inputStream, CompressedProcesser.GZIP);
        Pair<String, NBT> pair = reader.readNamed();
        NBTCompound nbtTag = (NBTCompound) pair.getSecond();

        Schematic schematic = null;
        if (nbtTag.contains("Blocks")) schematic = new MCEditSchematic();
        else if (nbtTag.contains("Palette")) schematic = new SpongeSchematic();

        if (schematic != null) schematic.read(nbtTag);
        return schematic;
    }

    /**
     * Automatically detects the type of schematic and parses the file
     *
     * @param path Schematic path
     * @return parsed schematic
     * @throws IOException  if the file is invalid
     * @throws NBTException if the schematic is invalid
     */
    public static @Nullable Schematic fromPath(@NotNull Path path) throws IOException, NBTException {
        if (!Files.exists(path)) throw new FileNotFoundException("Invalid Schematic: File does not exist");
        return fromStream(Files.newInputStream(path));
    }

    /**
     * Automatically detects the type of schematic and parses the file
     *
     * @param file Schematic file
     * @return parsed schematic
     * @throws IOException  if the file is invalid
     * @throws NBTException if the schematic is invalid
     */
    public static @Nullable Schematic fromFile(@NotNull File file) throws IOException, NBTException {
        if (!file.exists()) throw new FileNotFoundException("Invalid Schematic: File does not exist");
        return fromStream(new FileInputStream(file));
    }

}
