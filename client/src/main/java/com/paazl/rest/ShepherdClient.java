package com.paazl.rest;

import com.paazl.exception.OrderAmountException;
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

import static com.paazl.common.Constants.*;

@Component
public class ShepherdClient {

    /*
                TODO Use a Rest client to obtain the server status, so this client can be used to obtain that status.
                TODO Write unit tests.
             */
    private final Logger log = LoggerFactory.getLogger(getClass());

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
            validateSheepsAmount(sheepsAmount);
        } catch (OrderAmountException e) {
            return e.getMessage();
        }

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(BASE_URL + ORDER_ENDPOINT, sheepsAmount, Map.class);
            return response.getBody().get("body").toString();
        } catch (RestClientException e) {
            return ERROR_MESSAGE_SERVER_IS_UNAVAILABLE;
        }
    }

    private void validateSheepsAmount(int sheepsAmount) throws OrderAmountException {
        if (sheepsAmount <= 0) {
            throw new OrderAmountException();
        }
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}