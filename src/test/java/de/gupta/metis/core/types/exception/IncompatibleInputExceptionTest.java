package de.gupta.metis.core.types.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("IncompatibleInputException")
final class IncompatibleInputExceptionTest
{
	@Nested
	@DisplayName("when created with of()")
	final class WhenCreatedWithOf
	{
		@Test
		@DisplayName("carries the message")
		void carriesTheMessage()
		{
			var exception = IncompatibleInputException.of("incompatible inputs");

			assertThat(exception.getMessage()).as("message").isEqualTo("incompatible inputs");
		}

		@Test
		@DisplayName("is an IllegalArgumentException")
		void isAnIllegalArgumentException()
		{
			assertThat(IncompatibleInputException.of("test"))
					.as("type hierarchy")
					.isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	@DisplayName("when created with from()")
	final class WhenCreatedWithFrom
	{
		@Test
		@DisplayName("supplier produces exception with the message")
		void supplierProducesExceptionWithTheMessage()
		{
			var supplier = IncompatibleInputException.from("supplied message");

			var exception = supplier.get();

			assertThat(exception.getMessage()).as("message from supplier").isEqualTo("supplied message");
		}

		@Test
		@DisplayName("supplier produces a new instance on each call")
		void supplierProducesANewInstanceOnEachCall()
		{
			var supplier = IncompatibleInputException.from("message");

			assertThat(supplier.get()).as("distinct instances").isNotSameAs(supplier.get());
		}

		@Test
		@DisplayName("produced exception is an IncompatibleInputException")
		void producedExceptionIsAnIncompatibleInputException()
		{
			var supplier = IncompatibleInputException.from("from test");

			assertThatThrownBy(() ->
			{
				throw supplier.get();
			})
					.as("thrown exception")
					.isInstanceOf(IncompatibleInputException.class)
					.hasMessage("from test");
		}
	}
}