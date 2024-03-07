package uk.joshiejack.penguinlib.scripting.wrapper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@SuppressWarnings("unused")
public class DataJS extends AbstractJS<CompoundTag> {
    public DataJS(CompoundTag tag) {
        super(tag);
    }


    public boolean load(String name, boolean bool) {
        CompoundTag object = penguinScriptingObject;
        return object.contains(name, Tag.TAG_BYTE) ? object.getBoolean(name) : bool;
    }

    public double load(String name, double dbl) {
        CompoundTag object = penguinScriptingObject;
        return object.contains(name, Tag.TAG_INT) ? object.getInt(name) : object.contains(name, Tag.TAG_DOUBLE) ? object.getDouble(name) : dbl;
    }

    public int load(String name, int num) {
        CompoundTag object = penguinScriptingObject;
        return object.contains(name, Tag.TAG_INT) ? object.getInt(name) : num;
    }

    public String load(String name, String str) {
        CompoundTag object = penguinScriptingObject;
        return object.contains(name, Tag.TAG_STRING) ? object.getString(name) : str;
    }

    public boolean has(String name) {
        return penguinScriptingObject.contains(name);
    }

    public void save(String name, boolean bool) {
        save(name, (Object) bool);
    }

    public void save(String name, int number) {
        save(name, (Object) number);
    }

    public void save(String name, double dubble) {
        save(name, (Object) dubble);
    }

    public void save(String name, float ffff) {
        save(name, (double) ffff);
    }

    public void save(String name, Object var) {
        CompoundTag object = penguinScriptingObject;
        String stringValue = var.toString();
        if (Boolean.parseBoolean(stringValue)) {
            object.putBoolean(name, true);
        } else {
            try {
                double doubleValue = Double.parseDouble(stringValue);
                if (doubleValue % 1 == 0) { // Check if there is no fractional part
                    int intValue = (int) doubleValue;
                    object.putInt(name, intValue);
                } else {
                    object.putDouble(name, doubleValue);
                }
            } catch (NumberFormatException e) {
                object.putString(name, stringValue);
            }
        }
    }

    public enum Type {
        BOOLEAN, DOUBLE, INTEGER, STRING
    }
}
