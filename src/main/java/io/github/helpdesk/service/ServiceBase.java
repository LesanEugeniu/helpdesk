package io.github.helpdesk.service;

import java.util.List;

// D -> dto, T- id type
public interface ServiceBase<D, T> {

    List<D> getAll();

    D getById(T id);

    D create(D dto);

    D update(T id, D dto);

    void delete(T id);

}
