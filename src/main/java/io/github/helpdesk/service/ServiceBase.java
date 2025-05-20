package io.github.helpdesk.service;

import java.util.List;

public interface ServiceBase<T> {

    List<T> getAll();

    T getById(String id);

    T create(T dto);

    T update(String id, T dto);

    void delete(String id);

}
