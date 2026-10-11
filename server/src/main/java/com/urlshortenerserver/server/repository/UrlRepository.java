package com.urlshortenerserver.server.repository;

import com.urlshortenerserver.server.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url,Long> , JpaSpecificationExecutor<Url> {


    List<Url> findAllByDeletedFalse();

    Optional<Url> findAllByCode(String code);

    Optional<Url> findByCode(String code);

    boolean existsByCode(String code);

    Optional<Url> findAllByCodeAndDeletedFalse(String code);

    void deleteByCode(String code);
}
