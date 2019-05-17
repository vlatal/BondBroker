package com.bonds4all.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BondController.class)
public class BondControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private BondController controller;

        @Test
        public void newBond() throws Exception {
            /*when(controller.newBond()).thenReturn("Hello Mock");
            this.mockMvc.perform(get("/greeting")).andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string(containsString("Hello Mock")));*/
        }

    @Test
    public void isRestrictedTime() {
        ZonedDateTime d = ZonedDateTime.of(2019, 5, 16, 22, 0, 0, 0, ZoneId.systemDefault());

        assertTrue(BondController.isRestrictedTime(d.withHour(4), 19, 7));
        assertFalse(BondController.isRestrictedTime (d.withHour(10), 19, 7));
        assertFalse(BondController.isRestrictedTime(d.withHour(16), 19, 7));
        assertTrue(BondController.isRestrictedTime(d.withHour(22), 19, 7));

        assertFalse(BondController.isRestrictedTime(d.withHour(4), 7, 19));
        assertTrue(BondController.isRestrictedTime(d.withHour(10), 7, 19));
        assertTrue(BondController.isRestrictedTime(d.withHour(16), 7, 19));
        assertFalse(BondController.isRestrictedTime(d.withHour(22), 7, 19));

    }
}