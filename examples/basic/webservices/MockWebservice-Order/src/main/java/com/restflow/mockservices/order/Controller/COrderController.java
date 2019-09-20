package com.restflow.mockservices.order.Controller;

import com.restflow.mockservices.order.Objects.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class COrderController {

    private AtomicInteger orderNumber = new AtomicInteger();

    @RequestMapping(value = "/placeOrder", method = RequestMethod.POST)
    public ResponseEntity<?> placeOrderMock(@RequestBody Order order) {

        String lResponse;

        if (order.material().number().equals("TRFR2004")) {
            lResponse = MessageFormat.format("SUCCESS: Order number [{0}]", orderNumber.getAndIncrement());
            return ResponseEntity.status(200).contentType(MediaType.TEXT_PLAIN).body(lResponse);
        }

        lResponse = MessageFormat.format("ERROR: You can only order part TRFR2004 here. You ordered [{0}]", order.material().number());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(lResponse);
    }
}
