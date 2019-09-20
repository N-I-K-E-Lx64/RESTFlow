package com.restflow.mockservices.market;

import com.restflow.mockservices.market.Objects.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EMaterialStamm {

    INSTANCE;

    private Set<String[]> rawmat = (Set) Stream.of(new String[]{"BOLT1004", "Inbusschraube 5x20mm", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"BRKT1004", "Bremsanlage", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"CHAN1004", "Kette", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"DGAM1004", "Kettenschaltung Bauteile", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"HXNT1004", "Sechskantmutter 5 mm", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"LWSH1004", "Sicherungsscheibe 5 mm", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"OFFR1004", "Mountainbikerahmen Herren", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"OFFR2004", "Mountainbikerahmen Frauen", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"ORHB1004", "Mountainbikelenker", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"ORSK1004", "new String[] Mountainbikesitz Bauteile", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"ORTB1004", "Mountainbikerohr", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"ORTR1004", "Mountainbikereifen", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"ORWH1004", "Mountainbike-Aluminiumrad", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"PCKG1004", "Verpackung", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"PEDL1004", "Pedal Bauteile", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRFR1004", "Touringrahmen-Schwarz", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRFR2004", "Touringrahmen-Silber", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRFR3004", "Touringrahmen-Rot", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRHB1004", "Touringlenker", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRSK1004", "Touringsitz Bauteile", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRTB1004", "Touringrohr", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRTR1004", "Touringreifen", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRWH1004", "Touring-Aluminiumrad", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"TRWH2004", "Kohlefaserrad", "HD00", "E00", "DE00", "10.0", "EUR"},
            new String[]{"WDOC1004", "Garantiedokument", "HD00", "E00", "DE00", "10.0", "EUR"}).collect(Collectors.toSet());

    public List<Material> getRawMaterials() {

        List<Material> lRawMat = new ArrayList<>();
        this.rawmat.forEach((m) -> {
            Material material = new Material(m[0], m[1]);
            material.plant(m[2]).purchasegroup(m[3]).purchaseorg(m[4]).price(Double.parseDouble(m[5])).currency(m[6]).type("ROH");
            String lMaterialNumber = material.number();
            String lGbiNumber = lMaterialNumber.substring(lMaterialNumber.length() - 3);

            if (!material.plant().equals("HD00") && !material.plant().equals("HH00")) {
                if (material.plant().equals("DL00") || material.plant().equals("SD00")) {
                    material.vendor("0000103" + lGbiNumber);
                }
            } else {
                material.vendor("0000122" + lGbiNumber);
            }

            lRawMat.add(material);
        });

        return lRawMat;
    }


}
