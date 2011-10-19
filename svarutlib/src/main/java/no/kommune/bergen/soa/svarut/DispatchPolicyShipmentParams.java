package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;

public class DispatchPolicyShipmentParams {
    private ShipmentPolicy shipmentPolicy;
    private long leadTimeBeforePrint = 0L;
    private final long leadTimeBeforeStop = 1000 * 60 * 60 * 24 * 5;


    public DispatchPolicyShipmentParams(ShipmentPolicy shipmentPolicy, long leadTimeBeforePrint) {
        this.shipmentPolicy = shipmentPolicy;
        this.leadTimeBeforePrint = leadTimeBeforePrint;
    }

    public ShipmentPolicy getShipmentPolicy() {
        return shipmentPolicy;
    }

    public long getLeadTimeBeforePrint() {
        return leadTimeBeforePrint;
    }

    public long getLeadTimeBeforeStop() {
        return leadTimeBeforeStop;
    }
}
