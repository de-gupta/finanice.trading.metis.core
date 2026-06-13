package de.gupta.metis.core.types.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MissingInputException")
final class MissingInputExceptionTest
{
	@Nested
	@DisplayName("when created with of()")
	final class WhenCreatedWithOf
	{
		@Test
		@DisplayName("carries the message")
		void carriesTheMessage()
		{
			var exception = MissingInputException.of("missing value");

			assertThat(exception.getMessage()).as("message").isEqualTo("missing value");
		}

		@Test
		@DisplayName("is a RuntimeException")
		void isARuntimeException()
		{
			assertThat(MissingInputException.of("test"))
					.as("type hierarchy")
					.isInstanceOf(RuntimeException.class);
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
			var supplier = MissingInputException.from("supplied message");

			var exception = supplier.get();

			assertThat(exception.getMessage()).as("message from supplier").isEqualTo("supplied message");
		}

		@Test
		@DisplayName("supplier produces a new instance on each call")
		void supplierProducesANewInstanceOnEachCall()
		{
			var supplier = MissingInputException.from("message");

			assertThat(supplier.get()).as("distinct instances").isNotSameAs(supplier.get());
		}

		@Test
		@DisplayName("produced exception is a MissingInputException")
		void producedExceptionIsAMissingInputException()
		{
			var supplier = MissingInputException.from("from test");

			assertThatThrownBy(() ->
			{
				throw supplier.get();
			})
					.as("thrown exception")
					.isInstanceOf(MissingInputException.class)
					.hasMessage("from test");
		}
	}
}