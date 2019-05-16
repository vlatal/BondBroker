package com.bonds4all.repositories;

import com.bonds4all.models.Bond;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BondRepository extends JpaRepository<Bond, Long> {

}
