package io.github.helpdesk.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// D -> dto, T- id type
public interface ServiceBase<D, T> {

    Page<D> getAll(Pageable pageable);

    D getById(T id);

    D create(D dto);

    D update(T id, D dto);

    void delete(T id);

}
