package com.demo.fileuploaddemo.exception;

public class RecordAlreadyExistException extends RuntimeException {
	public RecordAlreadyExistException(String message) {
		super(message);
	}
}
