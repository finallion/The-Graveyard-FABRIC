package com.lion.graveyard.support;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class NbtReader {
    private NbtReader() {}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readGzipped(byte[] bytes) throws IOException {
        try (DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)))) {
            int type = in.readUnsignedByte();

            if (type != 10) {
                throw new IOException("Root tag is not a compound: " + type);
            }

            in.readUTF();
            return (Map<String, Object>) readPayload(in, 10);
        }
    }

    private static Object readPayload(DataInputStream in, int type) throws IOException {
        switch (type) {
            case 1:
                return in.readByte();
            case 2:
                return in.readShort();
            case 3:
                return in.readInt();
            case 4:
                return in.readLong();
            case 5:
                return in.readFloat();
            case 6:
                return in.readDouble();
            case 7: {
                byte[] data = new byte[in.readInt()];
                in.readFully(data);
                return data;
            }
            case 8:
                return in.readUTF();
            case 9: {
                int elementType = in.readUnsignedByte();
                int size = in.readInt();
                List<Object> list = new ArrayList<>(Math.max(size, 0));
                for (int i = 0; i < size; i++) {
                    list.add(readPayload(in, elementType));
                }
                return list;
            }
            case 10: {
                Map<String, Object> compound = new LinkedHashMap<>();
                int childType;
                while ((childType = in.readUnsignedByte()) != 0) {
                    String name = in.readUTF();
                    compound.put(name, readPayload(in, childType));
                }
                return compound;
            }
            case 11: {
                int[] data = new int[in.readInt()];
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readInt();
                }
                return data;
            }
            case 12: {
                long[] data = new long[in.readInt()];
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readLong();
                }
                return data;
            }
            default:
                throw new IOException("Unknown NBT tag type " + type);
        }
    }
}
