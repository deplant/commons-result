package tech.deplant.commons.result;

// We need to describe supplier which can throw exceptions
@FunctionalInterface
public interface ThrowableSupplier<T> {
	T get() throws Exception;
}
