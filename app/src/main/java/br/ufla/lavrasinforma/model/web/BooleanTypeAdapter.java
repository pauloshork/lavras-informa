package br.ufla.lavrasinforma.model.web;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Conversão entre Boolean e tinyint(1).
 * Created by paulo on 29/07/16.
 */
public class BooleanTypeAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.getAsBoolean();
    }

    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src ? 1 : 0);
    }
}
