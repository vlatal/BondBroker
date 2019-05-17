package com.bonds4all.repositories;

import com.bonds4all.models.Client;
import com.bonds4all.models.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import javax.transaction.Transactional;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface RecordRepository extends JpaRepository<Record, Long> {

    //@Query(value = "select r from Record r where r.client = :clientId and r.ip = :ip")
    List<Record> findByClient_ClientIdAndIp(@Param("clientId") long clientId, @Param("ip") String ip);

    //@Query(value = "select r from Record r where r.client = :clientId")
    List<Record> findByClient_ClientId(@Param("clientId") long clientId);
}
