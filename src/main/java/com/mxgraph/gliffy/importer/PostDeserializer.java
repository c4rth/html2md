package com.mxgraph.gliffy.importer;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.type.*;
import com.fasterxml.jackson.databind.util.Converter;

import java.io.IOException;
import java.util.Objects;


/**
 * Enables post deserialization for classes that implement {@link PostDeserializable}
 */
/*
public class PostDeserializer implements TypeAdapterFactory {
    public interface PostDeserializable {
        void postDeserialize();
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            public T read(JsonReader in) throws IOException {
                T obj = delegate.read(in);
                if (obj instanceof PostDeserializable) {
                    ((PostDeserializable)obj).postDeserialize();
                }
                return obj;
            }
        };
    }
}
 */
public class PostDeserializer extends BeanDeserializerModifier {

    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(beanDesc.getType(), deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(type, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyReferenceDeserializer(DeserializationConfig config, ReferenceType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(type, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyArrayDeserializer(DeserializationConfig config, ArrayType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(type, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyCollectionDeserializer(DeserializationConfig config, CollectionType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(type, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyCollectionLikeDeserializer(DeserializationConfig config, CollectionLikeType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(type, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyMapDeserializer(DeserializationConfig config, MapType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(type, deserializer);
    }

    @Override
    public JsonDeserializer<?> modifyMapLikeDeserializer(DeserializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return createDelegate(type, deserializer);
    }

    @Override
    public KeyDeserializer modifyKeyDeserializer(DeserializationConfig config, JavaType type, KeyDeserializer deserializer) {
        return new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
                final var deserializedKey = deserializer.deserializeKey(key, ctxt);
                if (deserializedKey instanceof PostDeserializable postDeserializable) {
                    postDeserializable.postDeserialize();
                }
                return deserializedKey;
            }
        };
    }

    private JsonDeserializer<?> createDelegate(JavaType type, JsonDeserializer<?> target) {
        return new StdDelegatingDeserializer<>(new PostDeserializeConverter(type), type, target);
    }

    public interface PostDeserializable {
        void postDeserialize();
    }

    private static class PostDeserializeConverter implements Converter<Object, Object> {
        private final JavaType type;

        public PostDeserializeConverter(JavaType type) {
            Objects.requireNonNull(this.type = type);
        }

        @Override
        public Object convert(Object value) {
            if (value instanceof PostDeserializable postDeserializable) {
                postDeserializable.postDeserialize();
            }
            return value;
        }

        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return type;
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return type;
        }
    }

}