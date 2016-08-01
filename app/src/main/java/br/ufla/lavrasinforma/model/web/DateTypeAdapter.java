package br.ufla.lavrasinforma.model.web;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

import br.ufla.lavrasinforma.UtilConvert;

/**
 * Classe que serializa objetos do tipo Date.
 * Created by paulo on 31/07/16.
 */
public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return UtilConvert.fromString(json.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(UtilConvert.fromDate(src));
    }
}
