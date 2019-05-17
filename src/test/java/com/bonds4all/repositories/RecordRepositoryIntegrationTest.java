package com.bonds4all.repositories;

import com.bonds4all.models.ActionType;
import com.bonds4all.models.Bond;
import com.bonds4all.models.Client;
import com.bonds4all.models.Record;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Period;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RecordRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BondRepository bondRepository;

    private Client client0;
    private Client client1;

    private Bond bond0;
    private Bond bond1;
    private Bond bond2;
    private Bond bond3;
    private Bond bond4;
    private Bond bond5;
    
    private Record record0;
    private Record record1;
    private Record record2;
    private Record record3;
    private Record record4;
    private Record record5;
    
    @Before
    public void setUp() throws Exception {

        client0 = entityManager.persist(new Client("Pavel", "Nedvěd", "fotbalista"));
        client1 = entityManager.persist(new Client("František", "Nedvěd", "countryman"));

        bond0 = entityManager.persist(new Bond(client0, BigDecimal.valueOf(5), Period.ofYears(5), BigDecimal.valueOf(501)));
        bond1 = entityManager.persist(new Bond(client1, BigDecimal.valueOf(5), Period.ofYears(5), BigDecimal.valueOf(502)));
        bond2 = entityManager.persist(new Bond(client1, BigDecimal.valueOf(10), Period.ofYears(5), BigDecimal.valueOf(503)));
        bond3 = entityManager.persist(new Bond(client1, BigDecimal.valueOf(5), Period.ofYears(10), BigDecimal.valueOf(504)));
        bond4 = entityManager.persist(new Bond(client1, BigDecimal.valueOf(10), Period.ofYears(10), BigDecimal.valueOf(505)));
        bond5 = entityManager.persist(new Bond(client1, BigDecimal.valueOf(5), Period.ofYears(15), BigDecimal.valueOf(506)));

        record0 = entityManager.persist(new Record("1.1.1.1", client0, ActionType.BUY_BOND, bond0));
        record1 = entityManager.persist(new Record("1.1.1.1", client1, ActionType.BUY_BOND, bond1));
        record2 = entityManager.persist(new Record("127.0.0.1", client1, ActionType.BUY_BOND, bond2));
        record3 = entityManager.persist(new Record("127.0.0.1", client1, ActionType.BUY_BOND, bond3));
        record4 = entityManager.persist(new Record("127.0.0.1", client1, ActionType.BUY_BOND, bond4));
        record5 = entityManager.persist(new Record("127.0.0.1", client1, ActionType.BUY_BOND, bond5));

        entityManager.flush();

        /*Mockito.when(recordRepository.findByClient_ClientId(client0.getClientId()))
                .thenReturn(Arrays.asList(record0));

        Mockito.when(recordRepository.findByClientAndIp(client1, "1.1.1.1"))
                .thenReturn(Arrays.asList(record1));*/
    }

    @Test
    public void findByClientAndIp() {
        // when
        List<Record> found = recordRepository.findByClient_ClientIdAndIp(client1.getClientId(), "1.1.1.1");

        // then
        assertSame(bond1, found.get(0).getBond());
    }

    @Test
    public void findByClient_ClientId() {
        // when
        List<Record> found = recordRepository.findByClient_ClientId(client0.getClientId());

        // then
        assertSame(record0, found.get(0));
    }
}