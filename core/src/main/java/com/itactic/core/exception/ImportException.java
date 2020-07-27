package com.itactic.core.exception;



import java.io.Serializable;

/**
 * @auther LinYiHao
 * @date 2020/7/24 15:22
 */
public class ImportException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public ImportException(String errMsg) {
        super(errMsg);
    }

}
