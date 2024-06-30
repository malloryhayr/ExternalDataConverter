package ca.spottedleaf.dataconverter.util.nbt;


import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import java.util.List;

public class MutableNBTList {
    
    private final List<BinaryTag> list;
    private BinaryTagType<?> type;

    public MutableNBTList(List<BinaryTag> list, BinaryTagType<?> type) {
        this.list = list;
        this.type = type;
    }

    public MutableNBTList() {
        this.list = new java.util.ArrayList<>();
        this.type = BinaryTagTypes.END;
    }

    public void add(BinaryTag tag) {
        if(this.updateType(tag.type())) {
            this.list.add(tag);
        }
    }

    public void add(int index, BinaryTag tag) {
        if(this.updateType(tag.type())) {
            this.list.add(index, tag);
        }
    }

    public void set(int index, BinaryTag tag) {
        if(this.updateType(tag.type())) {
            this.list.set(index, tag);
        }
    }

    public BinaryTag remove(int index) {
        return this.list.remove(index);
    }

    public BinaryTag get(int index) {
        return this.list.get(index);
    }

    public int size() {
        return this.list.size();
    }

    public BinaryTagType<?> getType() {
        return this.type;
    }

    public List<BinaryTag> getList() {
        return this.list;
    }

    private boolean updateType(BinaryTagType<?> type) {
        if(type == BinaryTagTypes.END) {
            return false;
        } else if (this.type == BinaryTagTypes.END) {
            this.type = type;
            return true;
        } else {
            return this.type == type;
        }
    }


}
