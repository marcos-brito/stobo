package com.stobo.server.catalog.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface ProductRepository extends CrudRepository<Product, Long>,
                                    PagingAndSortingRepository<Product, Long> {}
