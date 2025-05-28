package tech.deplant.commons.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Result<T> permits Ok, Err {

	/**
	 * Encapsulates lazy getter as a result object.
	 * Use orElseXXX methods to unwrap result and isOk(), isErr() to check status.
	 *
	 * @param throwableSupplier getter for encapsulation
	 * @param <T>               type of value
	 * @return wrapped result of execution
	 */
	static <T> Result<T> of(ThrowableSupplier<T> throwableSupplier) {
		try {
			return new Ok<T>(Objects.requireNonNull(throwableSupplier.get()));
		} catch (Exception err) {
			return new Err<T>(err);
		}
	}

	/**
	 * Encapsulates lazy getter as a result object.
	 * Use orElseXXX methods to unwrap result and isOk(), isErr() to check status.
	 *
	 * @param throwableSupplier getter for encapsulation
	 * @param <T>               type of value
	 * @return wrapped result of execution
	 */
	static <T> Result<T> of(ThrowableSupplier<T> throwableSupplier, Supplier<Exception> exceptionSupplier) {
		try {
			return new Ok<T>(Objects.requireNonNull(throwableSupplier.get()));
		} catch (Exception err) {
			var ex = exceptionSupplier.get();
			ex.initCause(err);
			return new Err<T>(ex);
		}
	}

	/**
	 * Transposing Optional into Result.
	 * Throws NullPointerException on Result.Err.orElseThrow()
	 *
	 * @param throwableOptionalSupplier optional value wrapper
	 * @param <T>                       type of value
	 * @return result of optional check
	 */
	static <T> Result<T> ofOptional(ThrowableSupplier<Optional<T>> throwableOptionalSupplier) {
		return ofOptional(throwableOptionalSupplier, () -> new NullPointerException("No optional value provided!"));
	}

	/**
	 * Transposing Optional into Result.
	 * Throws the provided exception.
	 *
	 * @param throwableOptionalSupplier optional value wrapper
	 * @param exceptionSupplier         custom exception to throw on Result.Err.orElseThrow()
	 * @param <T>                       type of value
	 * @return result of optional check
	 */
	static <T> Result<T> ofOptional(ThrowableSupplier<Optional<T>> throwableOptionalSupplier,
	                                Supplier<Exception> exceptionSupplier) {
		var result = Result.of(throwableOptionalSupplier, exceptionSupplier);
		return result.mapResult(opt -> {
			if (opt.isPresent()) {
				return new Ok<T>(opt.get());
			} else {
				return new Err<T>(exceptionSupplier.get());
			}
		});
	}

	/**
	 * Checks whether the provided result is Ok result. It means result is
	 * guaranteed to be not an exception and not null and not empty optional
	 * (if made from optional).
	 *
	 * @return true if Ok result, false if Err result
	 */
	boolean isOk();

	/**
	 * Checks whether the provided result is Err result (exception or null or
	 * empty optional (if made from optional).
	 *
	 * @return true if Err result, false if Ok result
	 */
	boolean isErr();

	/**
	 * Applies provided function to the Ok result. If result was Err,
	 * no transformations will be done.
	 *
	 * @param mapper function to transform the Ok result
	 * @return Result with transformed type
	 * @param <R> transformed type
	 */
	<R> Result<R> map(Function<T, R> mapper);

	/**
	 * Applies provided function to the Ok result. If result was Err,
	 * no transformations will be done.
	 *
	 * @param resultMapper function to transform the Ok result
	 * @return Result provided by transformation function
	 * @param <R> transformed type
	 */
	<R> Result<R> mapResult(Function<T, Result<R>> resultMapper);

	/**
	 * Applies a provided message to the Err exception. If result was Ok,
	 * no transformations will be done.
	 *
	 * @param exceptionMessage message to add to underlying exception result
	 * @return Result with transformed exception
	 */
	Result<T> mapErr(String exceptionMessage);

	/**
	 * Wraps Err original exception with a provided exception from supplier.
	 *
	 * @param exceptionSupplier supplier to get wrapping exception
	 * @return Result with transformed exception
	 */
	Result<T> mapErr(Supplier<Exception> exceptionSupplier);

	/**
	 * Wraps Err original exception with a provided exception from supplier,
	 * but checks some additional condition to wrap exception or not.
	 *
	 * @param condition condition to wrap exception
	 * @param exceptionSupplier supplier to get wrapping exception
	 * @return Result with transformed exception
	 */
	Result<T> mapErrIf(Predicate<Exception> condition, Supplier<Exception> exceptionSupplier);

	/**
	 * Transposes Result into Optional. Err results will be presented as
	 * Optional.of(Exception) and Ok results will become Optional.empty().
	 *
	 * @return the optional underlying error
	 */
	Optional<Exception> err();

	/**
	 * Returns underlying Err exception or throws if result was Ok.
	 *
	 * @return Err exception or throw if Ok
	 */
	Exception errOrThrow(String errorMessage) throws Exception;

	/**
	 * Transposes Result into Optional. Ok results will be presented as
	 * Optional.of(T) and Err results will become Optional.empty().
	 *
	 * @return the optional underlying ok result
	 */
	Optional<T> ok();

	T orElse(T defaultResult);

	T orElse(Supplier<T> defaultResultSupplier);

	/**
	 * Returns underlying Ok result or throws Err result.
	 *
	 * @return Ok result or exception from Err
	 */
	T orElseThrow() throws Exception;

	/**
	 * Returns underlying Ok result or throws Err result.
	 *
	 * @param errorMessage Message to add to thrown exception
	 * @return Ok result or exception from Err
	 */
	T orElseThrow(String errorMessage) throws Exception;

	/**
	 * Returns underlying Ok result or throws Err result.
	 *
	 * @param exceptionSupplier Exception to wrap exception from Err
	 * @return Ok result or exception from Err
	 */
	T orElseThrow(Supplier<Exception> exceptionSupplier) throws Exception;

}