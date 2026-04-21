package com.puppymapserver.global.exception;

import lombok.Getter;

@Getter
public class PuppyMapException extends RuntimeException {

	private final String message;

	public PuppyMapException(String message, Exception e) {
		super(message, e);
		this.message = message;
	}

	public PuppyMapException(String message) {
		this.message = message;
	}
}
