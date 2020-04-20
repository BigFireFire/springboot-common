package com.itactic.jdbc.exception;

import java.io.Serializable;

public class SqlBuilderException extends RuntimeException implements
		Serializable {

	private static final long serialVersionUID = 1L;

	public SqlBuilderException(String errMsg) {
		super(errMsg);
	}

	public SqlBuilderException(String errMsg, Throwable exception) {
		super(errMsg);
	}

}
