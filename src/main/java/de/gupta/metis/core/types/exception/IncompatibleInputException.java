package de.gupta.metis.core.types.exception;

import java.util.function.Supplier;

public final class IncompatibleInputException extends IllegalArgumentException
{
	public static Supplier<IncompatibleInputException> from(String message)
	{
		return () -> new IncompatibleInputException(message);
	}

	public static IncompatibleInputException of(String message)
	{
		return new IncompatibleInputException(message);
	}

	private IncompatibleInputException(String message)
	{
		super(message);
	}
}