package com.randioo.mahjong_public_server.test;

import java.lang.reflect.Method;

import com.randioo.randioo_server_base.template.Function;

public abstract class MethodFunction implements Function {
	public MethodFunction(Method method) {
		this.method = method;
	}

	protected Method method;

}
