package com.paazl.rest;

import com.paazl.dto.SheepStatusesDto;
import com.paazl.gui.GuiInterface;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.paazl.common.Constants.ERROR_MESSAGE_ORDER_AMOUNT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShepherdClientTest {

    private static final String BASE_URL = "http://localhost:8080/rest/shepherdmanager";
    private static final String BALANCE_ENDPOINT = "/balance";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String ORDER_ENDPOINT = "/order";
    private static final String SUCCESS_MESSAGE_STATUS = "Balance: %d, number of sheep healthy and dead: [%d, %d]";

    private static final String SUCCESS_MESSAGE_SHEEPS_WERE_ORDERED = "In total %s sheep were ordered and added to your flock!";
    private static final String ERROR_MESSAGE_SERVER_IS_UNAVAILABLE = "Server is unavailable";

    @Mock
    private GuiInterface guiInterface;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ShepherdClient testInstance;

    @Before
    public void setup() {
        testInstance.setRestTemplate(restTemplate);
    }

    @Test
    public void getServerStatus_ShouldReturnSuccessStatusMessage() {

        BigInteger balance = BigInteger.valueOf(1504);
        SheepStatusesDto sheepStatuses = new SheepStatusesDto(7, 0);

        when(restTemplate.getForObject(BASE_URL + BALANCE_ENDPOINT, BigInteger.class)).thenReturn(balance);
        when(restTemplate.getForObject(BASE_URL + STATUS_ENDPOINT, SheepStatusesDto.class)).thenReturn(sheepStatuses);

        assertThat(testInstance.getServerStatus(),
                is(String.format(SUCCESS_MESSAGE_STATUS,
                        balance,
                        sheepStatuses.getNumberOfHealthySheep(),
                        sheepStatuses.getNumberOfDeadSheep())));

    }

    @Test
    public void makeOrder_ShouldReturnSuccessMessageSheepsWereOrdered() {
        int nofSheepDesired = 7;

        Map<String, String> map = new HashMap<>();
        map.put("body", String.format(SUCCESS_MESSAGE_SHEEPS_WERE_ORDERED, nofSheepDesired));
        ResponseEntity response = new ResponseEntity(map, HttpStatus.OK);

        when(restTemplate.postForEntity(BASE_URL + ORDER_ENDPOINT, nofSheepDesired, Map.class)).thenReturn(response);

        assertThat(testInstance.makeOrder(nofSheepDesired),
                is(String.format(SUCCESS_MESSAGE_SHEEPS_WERE_ORDERED, nofSheepDesired)));
    }

    @Test
    public void getServerStatus_ShouldReturnErrorMessageServerIsUnavailable() {

        when(restTemplate.getForObject(BASE_URL + BALANCE_ENDPOINT, BigInteger.class)).thenThrow(new RestClientException(""));
        when(restTemplate.getForObject(BASE_URL + STATUS_ENDPOINT, SheepStatusesDto.class)).thenThrow(new RestClientException(""));

        assertThat(testInstance.getServerStatus(), is(ERROR_MESSAGE_SERVER_IS_UNAVAILABLE));
    }

    @Test
    public void makeOrder_ShouldReturnErrorMessageServerIsUnavailable() {
        int nofSheepDesired = 1;

        when(restTemplate.postForEntity(BASE_URL + ORDER_ENDPOINT, nofSheepDesired, Map.class)).thenThrow(new RestClientException(""));

        assertThat(testInstance.makeOrder(nofSheepDesired), is(ERROR_MESSAGE_SERVER_IS_UNAVAILABLE));
    }

    @Test
    public void makeOrder_ShouldReturnErrorMessageOrderAmountException() {
        int nofSheepDesired = -1;

        when(restTemplate.postForEntity(BASE_URL + ORDER_ENDPOINT, nofSheepDesired, Map.class)).thenThrow(new RestClientException(""));

        assertThat(testInstance.makeOrder(nofSheepDesired), is(ERROR_MESSAGE_ORDER_AMOUNT));
    }
}