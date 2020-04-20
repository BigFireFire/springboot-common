package com.itactic.core.exception;

import org.springframework.core.NestedRuntimeException;

import java.io.Serializable;

public class BootCustomException extends NestedRuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;

	public BootCustomException(String errMsg) {
		super(errMsg);
	}

	public BootCustomException(String errMsg, Throwable exception) {
		super(errMsg);
	}

}
