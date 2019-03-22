package com.raptor.plugins.config.gson;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;
import java.util.Set;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raptor.plugins.util.TypedMap;

@SuppressWarnings("rawtypes")
public class TypedMapTypeAdapter implements InstanceCreator<TypedMap>, JsonSerializer<TypedMap> {

	@SuppressWarnings("unchecked")
	@Override
	public TypedMap createInstance(Type type) {
		if(type instanceof ParameterizedType) {
			ParameterizedType param = (ParameterizedType)type;
			Type[] typeArgs = param.getActualTypeArguments();
			if(typeArgs.length == 0)
				return new TypedMap(Object.class, Object.class);
			if(typeArgs.length < 2)
				throw new IllegalArgumentException("Too few type arguments to TypedList");
			if(typeArgs.length > 2)
				throw new IllegalArgumentException("Too many type arguments to TypedList");
			return new TypedMap(getRawType(typeArgs[0]), getRawType(typeArgs[1]));
		} else if(type instanceof WildcardType) {
			WildcardType wild = (WildcardType)type;
			Type[] upper = wild.getUpperBounds();
			return createInstance(upper[0]);
		} else {
			Class.class.cast(type);
			return new TypedMap(Object.class, Object.class);
		}
	}

	private static Class<?> getRawType(Type type) {
		if(type instanceof Class)
			return (Class<?>)type;
		else if(type instanceof GenericArrayType)
			return Array.newInstance(getRawType(((GenericArrayType)type).getGenericComponentType()), 0).getClass();
		else if(type instanceof ParameterizedType)
			return getRawType(((ParameterizedType)type).getRawType());
		else if(type instanceof TypeVariable)
			return getRawType(((TypeVariable<?>)type).getBounds()[0]);
		else if(type instanceof WildcardType)
			return getRawType(((WildcardType)type).getUpperBounds()[0]);
		else throw new IllegalArgumentException("Cannot get raw type from " + type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JsonElement serialize(TypedMap src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		for(Map.Entry entry : (Set<Map.Entry>)src.entrySet()) {
			obj.add(context.serialize(entry.getKey()).getAsString(), context.serialize(entry.getValue()));
		}
		return obj;
	}
}
