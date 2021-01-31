package com.itactic.core.service;

import com.itactic.core.exception.BootCustomException;

/**
 * @author 1Zx.
 * @date 2021/1/22 13:15
 */
public interface IAdvicesService {

    void addBefore (Class<?> cls, Object o) throws BootCustomException;

    void addAfter(Class<?> cls, Object o) throws BootCustomException;

    void getBefore(Class<?> cls, Object o) throws BootCustomException;

    void getAfter(Class<?> cls, Object o) throws BootCustomException;

    void deleteBefore(Class<?> cls, Object o) throws BootCustomException;

    void deleteAfter(Class<?> cls, Object o) throws BootCustomException;

    void updateBefore(Class<?> cls, Object o) throws BootCustomException;

    void updateAfter(Class<?> cls, Object o) throws BootCustomException;
}
