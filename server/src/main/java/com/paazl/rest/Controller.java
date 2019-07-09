package com.paazl.rest;

import com.paazl.service.SheepStatusesDto;
import com.paazl.service.ShepherdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


//todo remove
@RestController
public class Controller {
    private final Logger log = LoggerFactory.getLogger(getClass());
    ShepherdService service;

    @Autowired
    public Controller(ShepherdService service) {
        this.service = service;
    }

    //@RequestMapping(value = "/statuses", method = RequestMethod.GET)
    public ResponseEntity<SheepStatusesDto> find() {

        SheepStatusesDto sheepStatusses = service.getSheepStatuses();
        log.info("getSheepStatusses = " + sheepStatusses);
        if (sheepStatusses!=null)
            return new ResponseEntity<>(sheepStatusses, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);


 //       return new ResponseEntity<>(service.getSheepStatuses(), HttpStatus.OK);

//        Address address = addressService.findById(addressId);
//        if (address == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        Link link = linkTo(methodOn(AddressRestController.class).find(addressId)).withSelfRel();
//        address.add(link);
//        return new ResponseEntity<>(address, HttpStatus.FOUND);
    }
}