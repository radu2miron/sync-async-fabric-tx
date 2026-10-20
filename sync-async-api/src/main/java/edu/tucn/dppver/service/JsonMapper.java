package edu.tucn.dppver.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A utility class for serializing and deserializing asset objects to/from JSON.
 *
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */
public class JsonMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Skip properties with null or empty values during serialization
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * Serializes an object to its JSON string representation.
     *
     * @param object the object to serialize
     * @return the JSON string representation of the object
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Deserializes a JSON string into an object of the specified type.
     *
     * @param json  the JSON string
     * @param clazz the target class type
     * @param <T>   the type of the object
     * @return the deserialized object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON to object", e);
        }
    }

    /**
     * Deserializes a JSON string into an object of a generic type using a TypeReference.
     * This method is particularly useful to deserialize JSON into collections like List, Map etc.
     *
     * @param json    the JSON string
     * @param typeRef the type reference, for example new TypeReference<List<FreightTracking>>() {}
     * @param <T>     the type to return
     * @return the deserialized object of type T
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON to object using TypeReference", e);
        }
    }
}
