package ca.spottedleaf.dataconverter.types.nbt;

import ca.spottedleaf.dataconverter.types.ObjectType;
import ca.spottedleaf.dataconverter.types.ListType;
import ca.spottedleaf.dataconverter.types.MapType;
import ca.spottedleaf.dataconverter.types.TypeUtil;
import ca.spottedleaf.dataconverter.types.Types;
import ca.spottedleaf.dataconverter.util.nbt.MutableNBTList;
import ca.spottedleaf.dataconverter.util.nbt.NBTUtil;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.NumberBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import java.util.List;

public final class NBTListType implements ListType {

    private final MutableNBTList list;

    public NBTListType() {
        this.list = new MutableNBTList();
    }

    @SuppressWarnings("unchecked")
    public NBTListType(final ListBinaryTag tag) {
        this.list = new MutableNBTList(tag.stream().toList(), tag.elementType());
    }

    @Override
    public TypeUtil getTypeUtil() {
        return Types.NBT;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != NBTListType.class) {
            return false;
        }

        return this.list.equals(((NBTListType)obj).list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public String toString() {
        return "NBTListType{" +
                "list=" + this.list +
                '}';
    }

    public ListBinaryTag getTag() {
        return ListBinaryTag.listBinaryTag(this.list.getType(), this.list.getList());
    }

    @Override
    public ListType copy() {
        return new NBTListType(this.getTag());
    }

    protected static ObjectType getType(final byte id) {
        switch (id) {
            case 0: // END
                return ObjectType.NONE;
            case 1: // BYTE
                return ObjectType.BYTE;
            case 2: // SHORT
                return ObjectType.SHORT;
            case 3: // INT
                return ObjectType.INT;
            case 4: // LONG
                return ObjectType.LONG;
            case 5: // FLOAT
                return ObjectType.FLOAT;
            case 6: // DOUBLE
                return ObjectType.DOUBLE;
            case 7: // BYTE_ARRAY
                return ObjectType.BYTE_ARRAY;
            case 8: // STRING
                return ObjectType.STRING;
            case 9: // LIST
                return ObjectType.LIST;
            case 10: // COMPOUND
                return ObjectType.MAP;
            case 11: // INT_ARRAY
                return ObjectType.INT_ARRAY;
            case 12: // LONG_ARRAY
                return ObjectType.LONG_ARRAY;
            default:
                throw new IllegalStateException("Unknown type: " + id);
        }
    }

    @Override
    public ObjectType getType() {
        return getType(this.list.getType().id());
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public void remove(final int index) {
        this.list.remove(index);
    }

    @Override
    public Number getNumber(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag number)) {
            throw new IllegalStateException();
        }
        return NBTUtil.getNumber(number);
    }

    @Override
    public byte getByte(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag number)) {
            throw new IllegalStateException();
        }
        return number.byteValue();
    }

    @Override
    public void setByte(final int index, final byte to) {
        this.list.set(index, ByteBinaryTag.byteBinaryTag(to));
    }

    @Override
    public short getShort(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag number)) {
            throw new IllegalStateException();
        }
        return number.shortValue();
    }

    @Override
    public void setShort(final int index, final short to) {
        this.list.set(index, ShortBinaryTag.shortBinaryTag(to));
    }

    @Override
    public int getInt(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag number)) {
            throw new IllegalStateException();
        }
        return number.intValue();
    }

    @Override
    public void setInt(final int index, final int to) {
        this.list.set(index, IntBinaryTag.intBinaryTag(to));
    }

    @Override
    public long getLong(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag number)) {
            throw new IllegalStateException();
        }
        return number.longValue();
    }

    @Override
    public void setLong(final int index, final long to) {
        this.list.set(index, LongBinaryTag.longBinaryTag(to));
    }

    @Override
    public float getFloat(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag number)) {
            throw new IllegalStateException();
        }
        return number.floatValue();
    }

    @Override
    public void setFloat(final int index, final float to) {
        this.list.set(index, FloatBinaryTag.floatBinaryTag(to));
    }

    @Override
    public double getDouble(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof NumberBinaryTag number)) {
            throw new IllegalStateException();
        }
        return number.doubleValue();
    }

    @Override
    public void setDouble(final int index, final double to) {
        this.list.set(index, DoubleBinaryTag.doubleBinaryTag(to));
    }

    @Override
    public byte[] getBytes(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof ByteArrayBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((ByteArrayBinaryTag)tag).value().clone();
    }

    @Override
    public void setBytes(final int index, final byte[] to) {
        this.list.set(index, ByteArrayBinaryTag.byteArrayBinaryTag(to));
    }

    @Override
    public short[] getShorts(final int index) {
        // NBT does not support shorts
        throw new UnsupportedOperationException();
    }

    @Override
    public void setShorts(final int index, final short[] to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getInts(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof IntArrayBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((IntArrayBinaryTag)tag).value().clone();
    }

    @Override
    public void setInts(final int index, final int[] to) {
        this.list.set(index, IntArrayBinaryTag.intArrayBinaryTag(to));
    }

    @Override
    public long[] getLongs(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof LongArrayBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((LongArrayBinaryTag)tag).value().clone();
    }

    @Override
    public void setLongs(final int index, final long[] to) {
        this.list.set(index, LongArrayBinaryTag.longArrayBinaryTag(to));
    }

    @Override
    public ListType getList(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof ListBinaryTag)) {
            throw new IllegalStateException();
        }
        return new NBTListType((ListBinaryTag) tag);
    }

    @Override
    public void setList(final int index, final ListType list) {
        this.list.set(index, ((NBTListType)list).getTag());
    }

    @Override
    public MapType<String> getMap(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof CompoundBinaryTag)) {
            throw new IllegalStateException();
        }
        return new NBTMapType(((CompoundBinaryTag) tag));
    }

    @Override
    public void setMap(final int index, final MapType<?> to) {
        this.list.set(index, ((NBTMapType)to).getTag());
    }

    @Override
    public String getString(final int index) {
        final BinaryTag tag = this.list.get(index); // does bound checking for us
        if (!(tag instanceof StringBinaryTag)) {
            throw new IllegalStateException();
        }
        return ((StringBinaryTag)tag).value();
    }

    @Override
    public void setString(final int index, final String to) {
        this.list.set(index, StringBinaryTag.stringBinaryTag(to));
    }

    @Override
    public void addByte(final byte b) {
        this.list.add(ByteBinaryTag.byteBinaryTag(b));
    }

    @Override
    public void addByte(final int index, final byte b) {
        this.list.add(index, ByteBinaryTag.byteBinaryTag(b));
    }

    @Override
    public void addShort(final short s) {
        this.list.add(ShortBinaryTag.shortBinaryTag(s));
    }

    @Override
    public void addShort(final int index, final short s) {
        this.list.add(index, ShortBinaryTag.shortBinaryTag(s));
    }

    @Override
    public void addInt(final int i) {
        this.list.add(IntBinaryTag.intBinaryTag(i));
    }

    @Override
    public void addInt(final int index, final int i) {
        this.list.add(index, IntBinaryTag.intBinaryTag(i));
    }

    @Override
    public void addLong(final long l) {
        this.list.add(LongBinaryTag.longBinaryTag(l));
    }

    @Override
    public void addLong(final int index, final long l) {
        this.list.add(index, LongBinaryTag.longBinaryTag(l));
    }

    @Override
    public void addFloat(final float f) {
        this.list.add(FloatBinaryTag.floatBinaryTag(f));
    }

    @Override
    public void addFloat(final int index, final float f) {
        this.list.add(index, FloatBinaryTag.floatBinaryTag(f));
    }

    @Override
    public void addDouble(final double d) {
        this.list.add(DoubleBinaryTag.doubleBinaryTag(d));
    }

    @Override
    public void addDouble(final int index, final double d) {
        this.list.add(index, DoubleBinaryTag.doubleBinaryTag(d));
    }

    @Override
    public void addByteArray(final byte[] arr) {
        this.list.add(ByteArrayBinaryTag.byteArrayBinaryTag(arr));
    }

    @Override
    public void addByteArray(final int index, final byte[] arr) {
        this.list.add(index, ByteArrayBinaryTag.byteArrayBinaryTag(arr));
    }

    @Override
    public void addShortArray(final short[] arr) {
        // NBT does not support short[]
        throw new UnsupportedOperationException();
    }

    @Override
    public void addShortArray(final int index, final short[] arr) {
        // NBT does not support short[]
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntArray(final int[] arr) {
        this.list.add(IntArrayBinaryTag.intArrayBinaryTag(arr));
    }

    @Override
    public void addIntArray(final int index, final int[] arr) {
        this.list.add(index, IntArrayBinaryTag.intArrayBinaryTag(arr));
    }

    @Override
    public void addLongArray(final long[] arr) {
        this.list.add(LongArrayBinaryTag.longArrayBinaryTag(arr));
    }

    @Override
    public void addLongArray(final int index, final long[] arr) {
        this.list.add(index, LongArrayBinaryTag.longArrayBinaryTag(arr));
    }

    @Override
    public void addList(final ListType list) {
        this.list.add(((NBTListType)list).getTag());
    }

    @Override
    public void addList(final int index, final ListType list) {
        this.list.add(index, ((NBTListType)list).getTag());
    }

    @Override
    public void addMap(final MapType<?> map) {
        this.list.add(((NBTMapType)map).getTag());
    }

    @Override
    public void addMap(final int index, final MapType<?> map) {
        this.list.add(index, ((NBTMapType)map).getTag());
    }

    @Override
    public void addString(final String string) {
        this.list.add(StringBinaryTag.stringBinaryTag(string));
    }

    @Override
    public void addString(final int index, final String string) {
        this.list.add(index, StringBinaryTag.stringBinaryTag(string));
    }
}
