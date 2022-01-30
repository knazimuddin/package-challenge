package com.pkc.exceptions;

import lombok.Data;

@Data
public class ConstraintViolationException extends Exception{
	
	private String faultyLine;
	private int lineNumber;
	
	public ConstraintViolationException(Throwable exception) {
        super(exception);
    }
	
	public ConstraintViolationException(String message, String faultyLine, int lineNumber) {
        super(message);
        this.faultyLine = faultyLine;
        this.lineNumber = lineNumber;
    }
	
}
