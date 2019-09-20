package com.restflow.mockservices.market.Controller;

import com.restflow.mockservices.market.EMaterialStamm;
import com.restflow.mockservices.market.Objects.Material;
import com.restflow.mockservices.market.Objects.Order;
import com.restflow.mockservices.market.Objects.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicReference;

@RestController
public class CMarketController {

    @RequestMapping(value = "/getBestOffer", method = RequestMethod.POST)
    @ResponseBody
    public Order getBestOfferMock(@RequestBody OrderRequest orderRequest) {

        AtomicReference<Material> cheapestMaterial = new AtomicReference<>(new Material("test", "bla"));
        cheapestMaterial.get().price(Double.MAX_VALUE);

        EMaterialStamm.INSTANCE.getRawMaterials().forEach(material -> {
            if (material.number().equals(orderRequest.partNumber())) {
                if (material.price() < cheapestMaterial.get().price()) {
                    cheapestMaterial.set(material);
                }
            }
        });

        if (cheapestMaterial.get().number().equals("test")) {
            throw new RuntimeException("PartNumber doesn't exist.");
        }

        int lQuantity = (int) Math.round(orderRequest.budget() / cheapestMaterial.get().price());
        String lSupplierNr = cheapestMaterial.get().vendor();

        Order lOrder = new Order();
        lOrder.material(cheapestMaterial.get()).suppliernumber(lSupplierNr).quantity(lQuantity);

        return lOrder;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

}
