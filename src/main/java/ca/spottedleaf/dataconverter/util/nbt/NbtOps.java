package ca.spottedleaf.dataconverter.util.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.jglrxavpok.hephaistos.nbt.*;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class NbtOps implements DynamicOps<NBT> {
    public static DynamicOps<NBT> INSTANCE = new NbtOps();

    @Override
    public NBT empty() {
        return NBT.getEMPTY();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, NBT tag) {
        if(tag instanceof NBTByte byteTag) {
            return outOps.createByte(byteTag.getValue());
        } else if(tag instanceof NBTShort shortTag) {
            return outOps.createShort(shortTag.getValue());
        } else if(tag instanceof NBTInt intTag) {
            return outOps.createInt(intTag.getValue());
        } else if(tag instanceof NBTLong longTag) {
            return outOps.createLong(longTag.getValue());
        } else if(tag instanceof NBTFloat floatTag) {
            return outOps.createFloat(floatTag.getValue());
        } else if(tag instanceof NBTDouble doubleTag) {
            return outOps.createDouble(doubleTag.getValue());
        } else if(tag instanceof NBTByteArray byteArrayTag) {
            return outOps.createByteList(ByteBuffer.wrap(byteArrayTag.getValue().copyArray()));
        } else if(tag instanceof NBTString stringTag) {
            return outOps.createString(stringTag.getValue());
        } else if(tag instanceof NBTList<?> listTag) {
            return this.convertList(outOps, listTag);
        } else if(tag instanceof NBTCompound compoundTag) {
            return this.convertMap(outOps, compoundTag);
        } else if(tag instanceof NBTIntArray intArrayTag) {
            return outOps.createIntList(Arrays.stream(intArrayTag.getValue().copyArray()));
        } else if(tag instanceof NBTLongArray longArrayTag) {
            return outOps.createLongList(Arrays.stream(longArrayTag.getValue().copyArray()));
        }

        throw new IllegalStateException("Unexpected value: " + tag);
    }

    @Override
    public DataResult<Number> getNumberValue(NBT input) {
        if(input instanceof NBTNumber<?> number) {
            return DataResult.success(number.getValue());
        }
        return DataResult.error(() -> "Not a number");
    }

    @Override
    public NBT createNumeric(Number i) {
        return NBT.Double(i.doubleValue());
    }

    @Override
    public NBT createBoolean(boolean value) {
        return NBT.Byte((byte)(value ? 1 : 0));
    }

    @Override
    public NBT createInt(int value) {
        return NBT.Int(value);
    }

    @Override
    public NBT createByte(byte value) {
        return NBT.Byte(value);
    }

    @Override
    public NBT createShort(short value) {
        return NBT.Short(value);
    }

    @Override
    public NBT createLong(long value) {
        return NBT.Long(value);
    }

    @Override
    public NBT createFloat(float value) {
        return NBT.Float(value);
    }

    @Override
    public NBT createDouble(double value) {
        return NBT.Double(value);
    }

    @Override
    public DataResult<String> getStringValue(NBT input) {
        if (input instanceof NBTString stringTag) {
            return DataResult.success(stringTag.getValue());
        } else {
            return DataResult.error(() -> "Not a string");
        }
    }

    @Override
    public NBT createString(String value) {
        return NBT.String(value);
    }

    @Override
    public DataResult<NBT> mergeToList(NBT list, NBT value) {
        if(!(list instanceof NBTList<?> nbtList)) {
            return DataResult.error(() -> "Not a list");
        }
        if(nbtList.getID() != value.getID()) {
            return DataResult.error(() -> "List type mismatch");
        }

        List<NBT> newData = new ArrayList<>(nbtList.getValue());
        newData.add(value);

        return DataResult.success(NBT.List(list.getID(), newData));
    }

    @Override
    public DataResult<NBT> mergeToList(NBT list, List<NBT> values) {
        if(!(list instanceof NBTList<?> nbtList)) {
            return DataResult.error(() -> "Not a list");
        }

        //TODO: check if all values are of the same type

        List<NBT> newData = new ArrayList<>(nbtList.getValue());
        newData.addAll(values);

        return DataResult.success(NBT.List(list.getID(), newData));
    }

    @Override
    public DataResult<NBT> mergeToMap(NBT map, NBT key, NBT value) {
        if(!(map instanceof NBTCompound) && !(map instanceof NBTEnd)) {
            return DataResult.error(() -> "Not a map");
        } else if(!(key instanceof NBTString)) {
            return DataResult.error(() -> "Key is not a string");
        } else {
            MutableNBTCompound newMap = new MutableNBTCompound();
            if(map instanceof NBTCompound compoundTag) {
                newMap.putAll(compoundTag.getValue());
            }

            newMap.put(((NBTString)key).getValue(), value);
            return DataResult.success(newMap.toCompound());
        }
    }

    @Override
    public DataResult<NBT> mergeToMap(NBT map, MapLike<NBT> values) {
        if(!(map instanceof NBTCompound) && !(map instanceof NBTEnd)) {
            return DataResult.error(() -> "Not a map");
        } else {
            MutableNBTCompound newMap = new MutableNBTCompound();
            if(map instanceof NBTCompound compoundTag) {
                newMap.putAll(compoundTag.getValue());
            }

            List<NBT> errors = Lists.newArrayList();
            values.entries().forEach((pair) -> {
                NBT key = pair.getFirst();
                if (!(key instanceof NBTString)) {
                    errors.add(key);
                } else {
                    newMap.put(((NBTString) key).getValue(), pair.getSecond());
                }
            });
            return !errors.isEmpty() ? DataResult.error(() -> {
                return "following keys are not strings: " + errors;
            }, newMap.toCompound()) : DataResult.success(newMap.toCompound());
        }
    }

    @Override
    public DataResult<Stream<Pair<NBT, NBT>>> getMapValues(NBT tag) {
        if (tag instanceof NBTCompound compoundTag) {
            return DataResult.success(compoundTag.getKeys().stream().map((key) -> {
                return Pair.of(this.createString(key), compoundTag.get(key));
            }));
        } else {
            return DataResult.error(() -> "Invalid tag provided: " + tag);
        }
    }

    @Override
    public DataResult<Consumer<BiConsumer<NBT, NBT>>> getMapEntries(NBT tag) {
        if (tag instanceof NBTCompound compoundTag) {
            return DataResult.success((entryConsumer) -> compoundTag.getKeys().forEach((key) -> entryConsumer.accept(this.createString(key), compoundTag.get(key))));
        } else {
            return DataResult.error(() -> "Invalid tag provided: " + tag);
        }
    }

    @Override
    public DataResult<MapLike<NBT>> getMap(NBT tag) {
        if (tag instanceof final NBTCompound compoundTag) {
            return DataResult.success(new MapLike<NBT>() {
                @Nullable
                public NBT get(NBT tag) {
                    return compoundTag.get(((NBTString)tag).getValue());
                }

                @Nullable
                public NBT get(String string) {
                    return compoundTag.get(string);
                }

                public Stream<Pair<NBT, NBT>> entries() {
                    return compoundTag.getKeys().stream().map((key) -> Pair.of(NbtOps.this.createString(key), compoundTag.get(key)));
                }

                @Override
                public String toString() {
                    return "MapLike[" + compoundTag + "]";
                }
            });
        } else {
            return DataResult.error(() -> "Invalid tag provided: " + tag);
        }
    }

    @Override
    public NBT createMap(Stream<Pair<NBT, NBT>> map) {
        MutableNBTCompound compoundTag = new MutableNBTCompound();
        map.forEach((entry) -> compoundTag.put(((NBTString) entry.getFirst()).getValue(), entry.getSecond()));
        return compoundTag.toCompound();
    }

    private static NBT unwrap(NBTCompound nbt) {
        if (nbt.getSize() == 1) {
            NBT tag = nbt.get("");
            if (tag != null) {
                return tag;
            }
        }

        return nbt;
    }

    @SuppressWarnings("unchecked")
    public DataResult<Stream<NBT>> getStream(NBT tag) {
        if (tag instanceof NBTList listTag) {
            return listTag.getSubtagType() == NBTType.TAG_Compound ? DataResult.success(listTag.getValue().stream().map((nbt) -> {
                return unwrap((NBTCompound) nbt);
            })) : DataResult.success(listTag.getValue().stream());
        } else if(tag instanceof NBTLongArray array) {
            return DataResult.success(Streams.stream(array.getValue()).map(NBT::Long));
        } else if(tag instanceof NBTIntArray array) {
            return DataResult.success(Streams.stream(array.getValue()).map(NBT::Int));
        } else if(tag instanceof NBTByteArray array) {
            return DataResult.success(Streams.stream(array.getValue()).map(NBT::Byte));
        } else {
            return DataResult.error(() -> "Invalid tag provided: " + tag);
        }
    }

    @Override
    public DataResult<ByteBuffer> getByteBuffer(NBT input) {
        if(input instanceof NBTByteArray array) {
            return DataResult.success(ByteBuffer.wrap(array.getValue().copyArray()));
        }
        return DynamicOps.super.getByteBuffer(input);
    }

    @Override
    public NBT createByteList(ByteBuffer input) {
        ByteBuffer byteBuffer2 = input.duplicate().clear();
        byte[] bs = new byte[input.capacity()];
        byteBuffer2.get(0, bs, 0, bs.length);
        return NBT.ByteArray(bs);
    }

    @Override
    public DataResult<IntStream> getIntStream(NBT input) {
        if(input instanceof NBTIntArray array) {
            return DataResult.success(Arrays.stream(array.getValue().copyArray()));
        }
        return DynamicOps.super.getIntStream(input);
    }

    @Override
    public NBT createIntList(IntStream input) {
        return NBT.IntArray(input.toArray());
    }

    @Override
    public DataResult<LongStream> getLongStream(NBT input) {
        if(input instanceof NBTLongArray array) {
            return DataResult.success(Arrays.stream(array.getValue().copyArray()));
        }
        return DynamicOps.super.getLongStream(input);
    }

    @Override
    public NBT createLongList(LongStream input) {
        return NBT.LongArray(input.toArray());
    }

    @Override
    public NBT createList(Stream<NBT> input) {
        Iterator<NBT> it = input.iterator();

        NBT firstElement = it.next();
        if(firstElement instanceof NBTCompound) {
            return createListHeterogenous(List.of(firstElement), it);
        } else if(firstElement instanceof NBTByte) {
            return createListBytes(List.of(firstElement), it);
        } else if(firstElement instanceof NBTInt) {
            return createListInts(List.of(firstElement), it);
        } else if(firstElement instanceof NBTLong) {
            return createListLongs(List.of(firstElement), it);
        } else {
            return createListHomogenous(List.of(firstElement), it);
        }
    }

    private NBT createListBytes(List<NBT> previous, Iterator<NBT> input) {
        ByteArrayList list = new ByteArrayList(previous.size());

        for (NBT nbt : previous) {
            if(!(nbt instanceof NBTByte)) {
                return createListHomogenous(previous, input);
            }

            list.add(((NBTByte) nbt).getValue());
        }

        while (input.hasNext()) {
            NBT nbt = input.next();

            if(!(nbt instanceof NBTByte)) {
                List<NBT> newPrevious = new ArrayList<>(list.size());
                for (Byte b : list) {
                    newPrevious.add(NBT.Byte(b));
                }
                return createListHomogenous(newPrevious, input);
            }

            list.add(((NBTByte) nbt).getValue());
        }

        return NBT.ByteArray(list.toByteArray());
    }

    private NBT createListInts(List<NBT> previous, Iterator<NBT> input) {
        IntArrayList list = new IntArrayList(previous.size());

        for (NBT nbt : previous) {
            if(!(nbt instanceof NBTInt)) {
                return createListHomogenous(previous, input);
            }

            list.add(((NBTInt) nbt).getValue());
        }

        while (input.hasNext()) {
            NBT nbt = input.next();

            if(!(nbt instanceof NBTInt)) {
                List<NBT> newPrevious = new ArrayList<>(list.size());
                for (int b : list) {
                    newPrevious.add(NBT.Int(b));
                }
                return createListHomogenous(newPrevious, input);
            }

            list.add(((NBTInt) nbt).getValue());
        }

        return NBT.IntArray(list.toIntArray());
    }

    private NBT createListLongs(List<NBT> previous, Iterator<NBT> input) {
        LongArrayList list = new LongArrayList(previous.size());

        for (NBT nbt : previous) {
            if(!(nbt instanceof NBTLong)) {
                return createListHomogenous(previous, input);
            }

            list.add(((NBTLong) nbt).getValue());
        }

        while (input.hasNext()) {
            NBT nbt = input.next();

            if(!(nbt instanceof NBTLong)) {
                List<NBT> newPrevious = new ArrayList<>(list.size());
                for (long b : list) {
                    newPrevious.add(NBT.Long(b));
                }
                return createListHomogenous(newPrevious, input);
            }

            list.add(((NBTLong) nbt).getValue());
        }

        return NBT.LongArray(list.toLongArray());
    }

    private NBT createListHeterogenous(List<NBT> previous, Iterator<NBT> input) {
        MutableNBTList list = new MutableNBTList();
        previous.forEach(nbt -> list.add(wrapIfNeeded(nbt)));

        while (input.hasNext()) {
            NBT nbt = input.next();

            list.add(wrapIfNeeded(nbt));
        }

        return new NBTList<>(list.getType(), list.getList());
    }

    private static boolean isWrapper(NBTCompound nbt) {
        return nbt.getSize() == 1 && nbt.contains("");
    }

    private static NBT wrapIfNeeded(NBT value) {
        if (value instanceof NBTCompound compoundTag) {
            if (!isWrapper(compoundTag)) {
                return compoundTag;
            }
        }

        return wrapElement(value);
    }

    private static NBTCompound wrapElement(NBT value) {
        return NBT.Compound(mutableNBTCompound -> mutableNBTCompound.put("", value));
    }

    private NBT createListHomogenous(List<NBT> previous, Iterator<NBT> input) {
        MutableNBTList list = new MutableNBTList();
        previous.forEach(list::add);

        while (input.hasNext()) {
            NBT nbt = input.next();

            if(list.getType() != NBTType.TAG_End && list.getType() != nbt.getID()) {
                return createListHomogenous(list.getList(), input);
            } else {
                list.add(nbt);
            }

        }

        return new NBTList<>(list.getType(), list.getList());
    }

    @Override
    public NBT remove(NBT input, String key) {
        if(input instanceof NBTCompound inp) {
            MutableNBTCompound newMap = new MutableNBTCompound();
            inp.getKeys().stream().filter((k) -> !Objects.equals(k, key)).forEach((k) -> newMap.put(k, Objects.requireNonNull(inp.get(k))));
            return newMap.toCompound();
        }
        return input;
    }

}
