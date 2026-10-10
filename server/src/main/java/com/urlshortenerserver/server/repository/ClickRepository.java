package com.urlshortenerserver.server.repository;

import com.urlshortenerserver.server.model.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClickRepository extends JpaRepository<Click,Long>, JpaSpecificationExecutor<Click> {
}
