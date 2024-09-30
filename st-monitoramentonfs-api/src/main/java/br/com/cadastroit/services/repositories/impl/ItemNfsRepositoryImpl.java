package br.com.cadastroit.services.repositories.impl;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.ItemNfs;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface ItemNfsRepositoryImpl extends PagingAndSortingRepository<ItemNfs, Long> {
}
