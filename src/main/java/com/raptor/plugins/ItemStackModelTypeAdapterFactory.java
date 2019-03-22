package com.raptor.plugins;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class ItemStackModelTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
		if(ItemStackModel.class.isAssignableFrom(type.getRawType())) {
			return new TypeAdapter<T>() {
	
				@Override
				public void write(JsonWriter out, T value) throws IOException {
					delegate.write(out, value);
				}
	
				@Override
				public T read(JsonReader in) throws IOException {
					T result = delegate.read(in);
					if(result != null) {
						((ItemStackModel) result).mergeCustomItem();
					}
					return result;
				}
				
			};
		} else return delegate;
	}

}
