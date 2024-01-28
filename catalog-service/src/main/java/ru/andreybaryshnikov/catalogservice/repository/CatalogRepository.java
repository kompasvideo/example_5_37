package ru.andreybaryshnikov.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andreybaryshnikov.catalogservice.model.Product;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface CatalogRepository extends JpaRepository<Product, UUID> {
    Optional<List<Product>> findByNameIsLike(String name);
}
