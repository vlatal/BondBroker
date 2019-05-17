package com.bonds4all;

import static org.assertj.core.api.Assertions.assertThat;
import com.bonds4all.controllers.ClientController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmokeTest {

    @Autowired
    private ClientController clientController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(clientController).isNotNull();
    }
}