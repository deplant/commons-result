package tech.deplant.commons.result;

import java.util.function.Supplier;

/**
 * A special version of supplier that can throw exceptions
 */
@FunctionalInterface
public interface ThrowableSupplier<T> {

	/**
	 * Gets a supplied value
	 * @return supplied value
	 * @throws Exception rethrown exception
	 */
	T get() throws Exception;
}
