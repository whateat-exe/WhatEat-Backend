package com.exe.whateat.application.common;

/**
 * Mapper for converting between Entity and DTO. Use this in conjunction with
 * {@link org.springframework.stereotype.Component} and DI for reusability.
 *
 * @param <E> Entity type.
 * @param <D> DTO type.
 */
public interface WhatEatMapper<E, D> {

    D convertToDto(E e);

    default E convertToEntity(D d) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
