package com.paazl.rest;

import com.paazl.dto.SheepStatusesDto;
import com.paazl.exception.OrderAmountException;
import com.paazl.gui.GuiInterface;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;
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
            //guiInterface.addServerFeedback(makeOrder(i));
            guiInterface.addServerFeedback(testJersey(i));
        });
    }

    public String testJersey(int i) {


//        Client client = ClientBuilder.newClient();
//
//
//        client
//                .target(BASE_URL + STATUS_ENDPOINT)
//                .request(MediaType.APPLICATION_JSON)
//                .get(SheepStatusesDto.class);
//
//        System.out.println(client);




//        try {
//
//            Client client = Client.create();
//
//            WebResource webResource = client
//                    .resource(BASE_URL + STATUS_ENDPOINT);
//
//            ClientResponse response = webResource.accept("application/json")
//                    .get(ClientResponse.class);
//
//            if (response.getStatus() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + response.getStatus());
//            }
//
//            SheepStatusesDto output = response.getEntity(SheepStatusesDto.class);
//
//            System.out.println("Output from Server .... \n");
//            System.out.println(output);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }


        ClientConfig clientConfig = new DefaultClientConfig();

        // Create Client based on Config
        Client client = Client.create(clientConfig);

        WebResource webResource = client.resource(BASE_URL + STATUS_ENDPOINT);

        WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_JSON)
                .header("content-type", MediaType.APPLICATION_JSON);

        ClientResponse response = builder.get(ClientResponse.class);

        // Status 200 is successful.
        if (response.getStatus() != 200) {
            System.out.println("Failed with HTTP Error code: " + response.getStatus());
            String error= response.getEntity(String.class);
            System.out.println("Error: "+error);
            return null;
        }

        System.out.println("Output from Server .... \n");

        SheepStatusesDto employee = response.getEntity(SheepStatusesDto.class);
        System.out.println(employee);
        return null;
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