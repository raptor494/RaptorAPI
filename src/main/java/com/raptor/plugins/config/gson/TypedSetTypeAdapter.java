package com.raptor.plugins.config.gson;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.raptor.plugins.util.TypedSet;

@SuppressWarnings("rawtypes")
public class TypedSetTypeAdapter implements InstanceCreator<TypedSet>, JsonSerializer<TypedSet> {

	@SuppressWarnings("unchecked")
	@Override
	public TypedSet createInstance(Type type) {
		if(type instanceof ParameterizedType) {
			ParameterizedType param = (ParameterizedType)type;
			Type[] typeArgs = param.getActualTypeArguments();
			if(typeArgs.length == 0)
				return new TypedSet(Object.class);
			if(typeArgs.length != 1)
				throw new IllegalArgumentException("Too many type arguments to TypedList");
			return new TypedSet(getRawType(typeArgs[0]));
		} else if(type instanceof WildcardType) {
			WildcardType wild = (WildcardType)type;
			Type[] upper = wild.getUpperBounds();
			return createInstance(upper[0]);
		} else {
			Class.class.cast(type);
			return new TypedSet(Object.class);
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

	@Override
	public JsonElement serialize(TypedSet src, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray array = new JsonArray();
		for(Object obj : src) {
			array.add(context.serialize(obj));
		}
		return array;
	}
	
}
