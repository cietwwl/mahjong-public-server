package com.randioo.randioo_server_base.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectMethod {
	private Method method;
	private Object targetObj;

	public Object invoke(Object... params) {
		try {
			return method.invoke(targetObj, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ObjectMethod newObjectMethod(Object obj, String methodName, Class<?>... paramArrayOfClass) {
		return newObjectMethod(obj.getClass(), obj, methodName, paramArrayOfClass);
	}

	public static ObjectMethod newObjectMethod(Class<?> Class, Object obj, String methodName,
			Class<?>... paramArrayOfClass) {
		try {
			ObjectMethod objectMethod = new ObjectMethod();

			Method method = Class.getMethod(methodName, paramArrayOfClass);

			objectMethod.method = method;
			objectMethod.targetObj = obj;
			return objectMethod;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
