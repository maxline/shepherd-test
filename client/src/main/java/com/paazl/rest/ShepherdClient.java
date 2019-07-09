package com.paazl.rest;

import com.paazl.dto.SheepStatusesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.paazl.gui.GuiInterface;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Map;

@Component
public class ShepherdClient {

    /*
        TODO Use a Rest client to obtain the server status, so this client can be used to obtain that status.
        TODO Write unit tests.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String ERROR_MESSAGE_SERVER_IS_UNAVAILABLE = "Server is unavailable";
    private static final String SUCCESS_MESSAGE_STATUS = "Balance: %d, number of sheep healthy and dead: [%d, %d]";

    private static final String BASE_URL = "http://localhost:8080/rest/shepherdmanager";
    private static final String BALANCE_ENDPOINT = "/balance";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String ORDER_ENDPOINT = "/order";


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ShepherdClient(GuiInterface guiInterface) {

        guiInterface.addOrderRequestListener(i -> {
            guiInterface.addServerFeedback("Number of sheep to order: " + i);
            guiInterface.addServerFeedback(makeOrder(i));
        });
    }

    public String getServerStatus() {
        try {
            BigInteger balance = restTemplate.getForObject(BASE_URL + BALANCE_ENDPOINT, BigInteger.class);
            SheepStatusesDto sheepsStatus = restTemplate.getForObject(BASE_URL + STATUS_ENDPOINT, SheepStatusesDto.class);

            return getStatusMessage(balance, sheepsStatus.getNumberOfHealthySheep(), sheepsStatus.getNumberOfDeadSheep());
        } catch (RestClientException e) {
            return ERROR_MESSAGE_SERVER_IS_UNAVAILABLE;
        }
    }

    private String getStatusMessage(BigInteger balance, int aliveSheepsAmount, int deadSheepsAmount) {
        return String.format(SUCCESS_MESSAGE_STATUS, balance, aliveSheepsAmount, deadSheepsAmount);
    }

    public String makeOrder(int sheepsAmount) {
        log.info("Make order for {} number of sheeps ", sheepsAmount);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(BASE_URL + ORDER_ENDPOINT, sheepsAmount, Map.class);
            return response.getBody().get("body").toString();
        } catch (RestClientException e) {
            return ERROR_MESSAGE_SERVER_IS_UNAVAILABLE;
        }
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}