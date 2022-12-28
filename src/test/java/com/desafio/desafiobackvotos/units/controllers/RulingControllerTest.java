package com.desafio.desafiobackvotos.units.controllers;

import com.desafio.desafiobackvotos.resources.controllers.RulingController;
import com.desafio.desafiobackvotos.services.RulingService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(RulingController.class)
public class RulingControllerTest {

    @MockBean
    private RulingService rulingService;

    @Autowired
    WebTestClient webTestClient;
}
