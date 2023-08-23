package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class NBTParser {

    public static final int TRUNK_PIECE_SIZE = 3;
    public static final int LEAF_PIECE_SIZE = 4;

    public static void parseNBTFiles() throws IOException {
        File dataDirectory = FabricLoader.getInstance().getGameDir().resolve("data").toFile();
        File targetFolder = new File(dataDirectory, "graveyard/nbt");
        File outputFile = new File(dataDirectory, "graveyard/nbt/out.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        TheGraveyard.LOGGER.info(targetFolder + " exists: " + targetFolder.exists());

        if (targetFolder.exists() && targetFolder.isDirectory()) {
            File[] filesInDir = targetFolder.listFiles();
            for (File nbtFile : filesInDir) {
                TheGraveyard.LOGGER.info("Reading " + nbtFile + "...");

                if (nbtFile.getName().equals("out.txt")) {
                    continue;
                }

                List<Integer> trunkStates = new ArrayList<>();
                List<Integer> leavesStates = new ArrayList<>();

                writer.write(nbtFile.getName());
                writer.newLine();

                if (nbtFile.exists()) {
                    try {
                        NbtCompound nbt = NbtIo.readCompressed(nbtFile);

                        NbtList sizeList = nbt.getList("size", NbtType.INT);
                        int sizeX = sizeList.getInt(0);
                        int sizeZ = sizeList.getInt(2);

                        int centerX = sizeX / 2;
                        int centerZ = sizeZ / 2;

                        NbtList blocks = nbt.getList("blocks", NbtType.COMPOUND);
                        NbtList palette = nbt.getList("palette", NbtType.COMPOUND);

                        for (int i = 0; i < blocks.size(); i++) {
                            NbtCompound blockTag = blocks.getCompound(i);
                            NbtList posList = blockTag.getList("pos", NbtType.INT);
                            int posX = posList.getInt(0);
                            int posY = posList.getInt(1);
                            int posZ = posList.getInt(2);

                            int state = blockTag.getInt("state");

                            NbtCompound paletteEntry = palette.getCompound(state);
                            String blockName = paletteEntry.getString("Name");

                            if (blockName.equals("minecraft:air")) {
                                continue;
                            }

                            Identifier blockId = new Identifier(blockName);
                            Block block = Registries.BLOCK.get(blockId);

                            if (!(block instanceof LeavesBlock) && !(block instanceof PillarBlock)) {
                                continue;
                            }

                            NbtCompound propertiesNbt = paletteEntry.getCompound("Properties");


                            if (block instanceof PillarBlock) {
                                trunkStates.add(posX - centerX);
                                trunkStates.add(posY);
                                trunkStates.add(posZ - centerZ);
                            } else {
                                leavesStates.add(posX - centerX);
                                leavesStates.add(posY);
                                leavesStates.add(posZ - centerZ);
                                leavesStates.add(Integer.parseInt(propertiesNbt.getString("distance")));
                            }
                        }

                        int dataVersion = nbt.getInt("DataVersion");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                for (int item : trunkStates) {
                    writer.write(Integer.toString(item));
                    writer.write(", ");
                }

                writer.newLine();

                for (int item : leavesStates) {
                    writer.write(Integer.toString(item));
                    writer.write(", ");
                }

                writer.newLine();
                writer.newLine();
            }
        }

        writer.close();
    }
}




