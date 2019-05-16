package com.bonds4all.repositories;

import com.bonds4all.models.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import javax.transaction.Transactional;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface RecordRepository extends JpaRepository<Record, Long> {

    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE))
    @Query(value = "select r from Record r")
    @Transactional
    Stream<Record> streamAll();

    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE))
    @Query(value = "select r from Record r where r.clientId=clientId and r.ip=ip")
    @Transactional
    Stream<Record> streamAllForClientAndIp(Long clientId, InetAddress ip);
}
