package org.wensheng.juicyraspberrypie.command;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class InstructionTests {
	@Nested
	class allArguments {
		@SuppressWarnings("PMD.UnusedPrivateMethod")
		private static @NotNull Stream<Arguments> consumption() {
			return Stream.of(
					Arguments.of("", (Object) new String[0]),
					Arguments.of("one", (Object) new String[]{"one"}),
					Arguments.of("one,two,and three", (Object) new String[]{"one", "two", "and three"}),
					Arguments.of("", (Object) new String[]{null}),
					Arguments.of(",", (Object) new String[]{null, null}),
					Arguments.of("one,,three", (Object) new String[]{"one", null, "three"}),
					Arguments.of(",two,", (Object) new String[]{null, "two", null})
			);
		}

		@ParameterizedTest
		@MethodSource
		void consumption(@NotNull final String expected, @NotNull final String... args) {
			assertThat(new Instruction(args, null).allArguments(), is(equalTo(expected)));
		}

		@Test
		void consumes_remaining_arguments() {
			final Instruction instruction = new Instruction(new String[]{"one", "two", "three"}, null);
			instruction.next();
			assertThat(instruction.allArguments(), is("two,three"));
		}

		@Test
		void fully_consumes_arguments() {
			final Instruction instruction = new Instruction(new String[]{"one", "two", "three"}, null);
			instruction.allArguments();
			assertThat(instruction.hasNext(), is(false));
		}
	}
}
