package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.adapter.ConversionAdapter;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(RatesController.class)
class RatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionAdapter conversionAdapter;

    @Test
    void givenConversionCacheExists_whenDeleteConversionCache_thenCacheIsCleared() throws Exception {
        mockMvc.perform(delete("/currencycache"))
                .andExpect(status().isOk());

        verify(conversionAdapter, times(1)).clearCurrencyCache();
    }

}