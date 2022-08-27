package com.landingis.api.storage.master.repository;

import com.landingis.api.storage.master.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewsRepository  extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
}
