package me.dthb.drq.util.gson;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TableSerializer implements JsonSerializer<Table>, JsonDeserializer<Table<?, ?, ?>> {

    @Override
    public JsonElement serialize(Table table, Type type, JsonSerializationContext context) {
        return context.serialize(table.rowMap());
    }

    @Override
    public Table deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
        Type parameterizedType = hashMapOf(
                typeArguments[0],
                hashMapOf(typeArguments[1], typeArguments[2]).getType()).getType();
        Map<?, Map<?, ?>> map = context.deserialize(json, parameterizedType);

        Table<Object, Object, Object> table = HashBasedTable.create();
        for (Object rowKey : map.keySet()) {
            Map<?, ?> rowMap = map.get(rowKey);
            for (Object columnKey : rowMap.keySet()) {
                Object value = rowMap.get(columnKey);
                table.put(rowKey, columnKey, value);
            }
        }
        return table;
    }

    // see https://github.com/acebaggins/guava-gson-serializers/blob/master/src/main/java/com/baggonius/gson/immutable/Types.java
    static <K, V> TypeToken<HashMap<K, V>> hashMapOf(Type key, Type value) {
        TypeParameter<K> newKeyTypeParameter = new TypeParameter<K>() {
        };
        TypeParameter<V> newValueTypeParameter = new TypeParameter<V>() {
        };
        return new TypeToken<HashMap<K, V>>() {
        }
                .where(newKeyTypeParameter, typeTokenOf(key))
                .where(newValueTypeParameter, typeTokenOf(value));
    }

    private static <E> TypeToken<E> typeTokenOf(Type type) {
        return (TypeToken<E>) TypeToken.of(type);
    }

}
