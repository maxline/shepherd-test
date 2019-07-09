package com.paazl.rest;

import com.paazl.service.SheepStatusesDto;
import com.paazl.service.ShepherdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;


@Path("/shepherdmanager")
@Service
public class ShepherdResource {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ShepherdService service;

    @Autowired
    public ShepherdResource(ShepherdService service) {
        this.service = service;
    }

    @GET
    @Path("/balance")
    public BigInteger getBalance() {
        log.debug("Called getBalance");
        BigInteger balance = service.getBalance();
        return balance;
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public SheepStatusesDto getStatus() {
        log.debug("called getSheepStatusses");
        SheepStatusesDto sheepStatuses = service.getSheepStatuses();
        return sheepStatuses;
    }

    @POST
    @Path("/order")
    public ResponseEntity<String> makeOrder(int sheepsAmount) {
        log.debug("makeOrder for {} sheeps ", sheepsAmount);
        return new ResponseEntity<>(service.orderNewSheep(sheepsAmount), HttpStatus.OK);
    }
}