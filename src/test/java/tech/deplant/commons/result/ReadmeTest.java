package tech.deplant.commons.result;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Optional;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Execution(ExecutionMode.CONCURRENT)
public class ReadmeTest {

	@Test
	public void example_01() {
		var result = Result.of(() -> 8 / 0);

		switch (result) {
			case Ok(Integer i) when i > 2 -> System.out.println("Good!");
			case Ok ok -> System.out.println("Ok: " + ok.result());
			case Err err -> System.out.println("Err: " + err.error().getMessage());
		}
	}

	public static Result<Integer> functionCanFail(Integer i) {
		return Result.of(() -> 2 / i);
	}

	@Test
	public void example_02() {
		Result<Integer> goodResult = Result.of(() -> 7);
		Result<Integer> multipliedResult = goodResult.map(i -> i * 2); // transformations
		Result<String> stringResult = goodResult.map(Object::toString); // type transformations
		Result<Integer> combinedResult = goodResult.mapResult(s -> functionCanFail(s)); // transformations with other results
		Integer digit = combinedResult.orElse(2); // get or default when fail
		Optional<Integer> optionalDigit = combinedResult.ok(); // get as optional (empty when fail)
	}
}
