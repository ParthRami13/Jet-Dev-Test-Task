package com.demo.fileuploaddemo.exception;

public class InvalidLoginException extends RuntimeException {
	public InvalidLoginException(String message) {
		super(message);
	}
}
