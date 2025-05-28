package tech.deplant.commons.result;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record Ok<T>(T result) implements Result<T> {
	@Override
	public boolean isOk() {
		return true;
	}

	@Override
	public boolean isErr() {
		return false;
	}

	@Override
	public <R> Result<R> map(Function<T, R> mapper) {
		try {
			return Result.of(() -> mapper.apply(result()));
		} catch (Exception e) {
			return new Err<>(e);
		}
	}

	@Override
	public <R> Result<R> mapResult(Function<T, Result<R>> resultMapper) {
		try {
			return resultMapper.apply(result());
		} catch (Exception e) {
			return new Err<>(e);
		}
	}

	@Override
	public Result<T> mapErr(String exceptionMessage) {
		return this;
	}

	@Override
	public Result<T> mapErr(Supplier<Exception> exceptionSupplier) {
		return this;
	}

	@Override
	public Result<T> mapErrIf(Predicate<Exception> condition, Supplier<Exception> exceptionSupplier) {
		return this;
	}

	@Override
	public Optional<Exception> err() {
		return Optional.empty();
	}

	@Override
	public Exception errOrThrow(String errorMessage) {
		throw new RuntimeException(errorMessage);
	}

	@Override
	public Optional<T> ok() {
		return Optional.of(result());
	}

	@Override
	public T orElse(T defaultResult) {
		return result();
	}

	@Override
	public T orElse(Supplier<T> defaultResultSupplier) {
		return result();
	}

	@Override
	public T orElseThrow() {
		return result();
	}

	@Override
	public T orElseThrow(String errorMessage) {
		return result();
	}

	@Override
	public T orElseThrow(Supplier<Exception> exceptionSupplier) {
		return result();
	}
}
