package com.raptor.plugins.config.gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class EnumMapTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
		if(type.getRawType() == EnumMap.class) {
			Type t = type.getType();
			if(t instanceof ParameterizedType) {
				ParameterizedType p = (ParameterizedType) t;
				Type[] typeArgs = p.getActualTypeArguments();
				if(typeArgs.length != 2 || !(typeArgs[0] instanceof Class))
					return delegate;
				if(!((Class<?>)typeArgs[0]).isEnum())
					return delegate;
				@SuppressWarnings("unchecked")
				final Class<Enum<?>> enumType = (Class<Enum<?>>)typeArgs[0];
				final Type valueType = typeArgs[1];
				
				return new TypeAdapter<T>() {

					@Override
					public void write(JsonWriter out, T value) throws IOException {
						delegate.write(out, value);
					}

					@SuppressWarnings("unchecked")
					@Override
					public T read(JsonReader in) throws IOException {
						if(in.peek() == JsonToken.NULL)
							return null;
						@SuppressWarnings({ "rawtypes" })
						Map map = new EnumMap(enumType);
						
						in.beginObject();
						do {
							String name = in.nextName();
							try {
								@SuppressWarnings({ "rawtypes" })
								Enum value = Enum.valueOf((Class) enumType, name);
								map.put(value, gson.<Object>fromJson(in, valueType));
							} catch(IllegalArgumentException e) {
								in.skipValue();
							}
						} while(in.peek() != JsonToken.END_OBJECT && in.peek() != JsonToken.END_DOCUMENT);
						in.endObject();
						return (T) map;
					}
					
				};
			}
		}
		// else
		return delegate;
	}

}
