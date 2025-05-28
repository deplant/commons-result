package tech.deplant.commons.result;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Execution(ExecutionMode.CONCURRENT)
public class ResultTest {

	static Result<Integer> NULL_RESULT = Result.of(() -> null);
	static Result<Integer> ERR_RESULT  = Result.of(() -> 8 / 0);
	static Result<Integer> OK_RESULT   = Result.of(() -> 8);

	Supplier<Integer> supplier_null = () -> {
		return null;
	};

	Supplier<Integer> supplier_throw = () -> {
		throw new IllegalArgumentException();
	};

	Function<Integer,String> func_to_null = ok -> {
		return null;
	};

	Function<Integer,Result<String>> func_to_result_str = d -> new Ok<String>(d.toString());

	Function<Integer,String> func_to_throw = ok -> {
		throw new IllegalArgumentException();
	};

	static final String TEST_EX_MSG = "Test exception thrown!";

	static class TestException extends Exception {
		public TestException(String message) {
			super(message);
		}
	}

	Supplier<Exception> supplier_text_ex = () -> new TestException(TEST_EX_MSG);

	@BeforeAll
	static void init() {
	}

	@Test
	void of() {
		assertInstanceOf(Err.class, NULL_RESULT);
		assertInstanceOf(Err.class, ERR_RESULT);
		assertInstanceOf(Ok.class, OK_RESULT);
	}

	@Test
	void ofOptional() {
		assertInstanceOf(Err.class, Result.ofOptional(() -> null));
		assertInstanceOf(Err.class, Result.ofOptional(Optional::empty));
		assertInstanceOf(Ok.class, Result.ofOptional(() -> Optional.of(8)));
	}

	@Test
	void isOk() {
		assertFalse(NULL_RESULT.isOk());
		assertFalse(ERR_RESULT.isOk());
		assertTrue(OK_RESULT.isOk());
	}

	@Test
	void isErr() {
		assertTrue(NULL_RESULT.isErr());
		assertTrue(ERR_RESULT.isErr());
		assertFalse(OK_RESULT.isErr());
	}

	@Test
	void ok() {
		assertInstanceOf(Optional.class, NULL_RESULT.ok());
		assertTrue(NULL_RESULT.ok().isEmpty());

		assertInstanceOf(Optional.class, ERR_RESULT.ok());
		assertTrue(ERR_RESULT.ok().isEmpty());

		assertInstanceOf(Optional.class, OK_RESULT.ok());
		assertTrue(OK_RESULT.ok().isPresent());
	}

	@Test
	void err() {
		assertInstanceOf(Optional.class, NULL_RESULT.err());
		assertTrue(NULL_RESULT.err().isPresent());

		assertInstanceOf(Optional.class, ERR_RESULT.err());
		assertTrue(ERR_RESULT.err().isPresent());

		assertInstanceOf(Optional.class, OK_RESULT.err());
		assertTrue(OK_RESULT.err().isEmpty());
	}

	@Test
	void orElse_good_defaults() {
		assertEquals(1, NULL_RESULT.orElse(1));
		assertEquals(2, ERR_RESULT.orElse(2));
		assertEquals(8, OK_RESULT.orElse(3));
	}

	@Test
	void orElse_null_defaults() {
		assertNull(NULL_RESULT.orElse((Integer) null));
		assertNull(ERR_RESULT.orElse((Integer) null));
		assertEquals(8, OK_RESULT.orElse((Integer) null));
	}

	@Test
	void orElse_lazy_good_defaults() {
		assertEquals(1, NULL_RESULT.orElse(() -> 1));
		assertEquals(2, ERR_RESULT.orElse(() -> 2));
		assertEquals(8, OK_RESULT.orElse(() -> 3));
	}

	@Test
	void orElse_lazy_null_defaults() {
		assertNull(NULL_RESULT.orElse(() -> null));
		assertNull(ERR_RESULT.orElse(() -> null));
		assertEquals(8, OK_RESULT.orElse(() -> null));
	}

	@Test
	void orElseThrow_zero_args() throws Exception {
		assertThrows(RuntimeException.class, () -> NULL_RESULT.orElseThrow());
		assertThrows(RuntimeException.class, () -> ERR_RESULT.orElseThrow());
		assertEquals(8, OK_RESULT.orElseThrow());
	}

	@Test
	void orElseThrow_1_args() throws Exception {
		var message = "Test message";

		assertThrows(RuntimeException.class, () -> NULL_RESULT.orElseThrow(message), message);
		assertThrows(RuntimeException.class, () -> ERR_RESULT.orElseThrow(message), message);
		assertEquals(8, OK_RESULT.orElseThrow(message));
	}

	@Test
	void orElseThrow_1_args_lazy_good_defaults() throws Exception {

		assertThrows(TestException.class, () -> NULL_RESULT.orElseThrow(supplier_text_ex), TEST_EX_MSG);
		assertThrows(TestException.class, () -> ERR_RESULT.orElseThrow(supplier_text_ex), TEST_EX_MSG);
		assertEquals(8, OK_RESULT.orElseThrow(supplier_text_ex));
	}

	@Test
	void orElseThrow_1_args_lazy_null_defaults() throws Exception {
		assertThrows(NullPointerException.class,
		             () -> NULL_RESULT.orElseThrow((Supplier<Exception>) null),
		             (String) null
		            );
		assertThrows(NullPointerException.class,
		             () -> ERR_RESULT.orElseThrow((Supplier<Exception>) null),
		             (String) null
		            );
		assertEquals(8, OK_RESULT.orElseThrow((Supplier<Exception>) null));
		assertThrows(NullPointerException.class, () -> NULL_RESULT.orElseThrow(() -> null), (String) null);
		assertThrows(NullPointerException.class, () -> ERR_RESULT.orElseThrow(() -> null), (String) null);
		assertEquals(8, OK_RESULT.orElseThrow(() -> null));
	}

	@Test
	void map_ok_result_with_error_map_function() throws Exception {
		assertInstanceOf(Err.class, OK_RESULT.map(func_to_throw));
		assertThrows(IllegalArgumentException.class, () -> OK_RESULT.map(func_to_throw).orElseThrow());
	}

	@Test
	void map_ok_result_with_null_map_function() throws Exception {
		assertInstanceOf(Err.class, OK_RESULT.map(func_to_null));
		assertThrows(NullPointerException.class, () -> OK_RESULT.map(func_to_null).orElseThrow());
	}

	@Test
	void map() throws Exception {
		assertThrows(NullPointerException.class, () -> NULL_RESULT.map(Object::toString).orElseThrow());
		assertThrows(ArithmeticException.class, () -> ERR_RESULT.map(Object::toString).orElseThrow());
		assertInstanceOf(String.class, OK_RESULT.map(Object::toString).orElseThrow());
	}

	@Test
	void mapResult() {
		assertThrows(NullPointerException.class, () -> NULL_RESULT.mapResult(func_to_result_str).orElseThrow());
		assertThrows(ArithmeticException.class, () -> ERR_RESULT.mapResult(func_to_result_str).orElseThrow());
		assertEquals("8", OK_RESULT.mapResult(func_to_result_str).orElse(() -> "0"));
		assertEquals("0", OK_RESULT.mapResult(i -> {
			if (1 == 2) {
				return Result.of(i::toString);
			} else {
				throw new RuntimeException();
			}
			}).orElse(() -> "0"));
	}

	@Test
	void mapErr() throws Exception {
		assertEquals(TEST_EX_MSG, NULL_RESULT.mapErr(TEST_EX_MSG).errOrThrow("No error!").getMessage());
		assertEquals(TEST_EX_MSG, ERR_RESULT.mapErr(TEST_EX_MSG).errOrThrow("No error!").getMessage());
		assertEquals(8, OK_RESULT.mapErr(TEST_EX_MSG).orElse(() -> 0));
	}

	@Test
	void mapErr_lazy() {
		assertThrows(TestException.class, () -> NULL_RESULT.mapErr(supplier_text_ex).orElseThrow());
		assertThrows(TestException.class, () -> ERR_RESULT.mapErr(supplier_text_ex).orElseThrow());
		assertEquals(8, OK_RESULT.mapErr(supplier_text_ex).orElse(() -> 0));
	}

	@Test
	void mapErrIf() {
		assertThrows(TestException.class, () -> NULL_RESULT.mapErrIf(ex -> true,supplier_text_ex).orElseThrow());
		assertThrows(NullPointerException.class, () -> NULL_RESULT.mapErrIf(ex -> false,supplier_text_ex).orElseThrow());
		assertThrows(TestException.class, () -> ERR_RESULT.mapErrIf(ex -> true,supplier_text_ex).orElseThrow());
		assertThrows(ArithmeticException.class, () -> ERR_RESULT.mapErrIf(ex -> false,supplier_text_ex).orElseThrow());
		assertEquals(8, OK_RESULT.mapErrIf(ex -> true,supplier_text_ex).orElse(() -> 0));
	}

	@Test
	void errOrThrow() throws Exception {
		assertInstanceOf(NullPointerException.class, NULL_RESULT.errOrThrow("No error!"));
		assertInstanceOf(ArithmeticException.class, ERR_RESULT.errOrThrow("No error!"));
		assertThrows(RuntimeException.class, () -> OK_RESULT.errOrThrow("No error!"));
	}

}