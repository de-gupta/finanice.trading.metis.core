package de.gupta.metis.core.types.exception;

import java.util.function.Supplier;

public final class MissingInputException extends RuntimeException
{
	public static Supplier<MissingInputException> from(String message)
	{
		return () -> new MissingInputException(message);
	}

	public static MissingInputException of(String message)
	{
		return new MissingInputException(message);
	}

	private MissingInputException(String message)
	{
		super(message);
	}
}