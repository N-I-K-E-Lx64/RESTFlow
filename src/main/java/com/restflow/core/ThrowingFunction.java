package com.restflow.core;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

  R apply(T t) throws E;

	/*private static <T, R, E extends Exception> Function<T, R> throwingFunctionWrapper(ThrowingFunction<T, R, E> function) {
		return i -> {
			try {
				return function.apply(i);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}*/
}
