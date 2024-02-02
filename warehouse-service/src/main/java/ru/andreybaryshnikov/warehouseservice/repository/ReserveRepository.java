package ru.andreybaryshnikov.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreybaryshnikov.warehouseservice.model.ReserveProduct;

import java.util.List;

public interface ReserveRepository extends JpaRepository<ReserveProduct, String> {

    List<ReserveProduct> findAllByRequestId(String xRequesId);
}
