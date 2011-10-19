package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import no.kommune.bergen.soa.svarut.util.DispatchWindow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DispatchPolicy {

    private int maxDispatchRate = 0; // maks antall forsendelser pr minutt

    private List<DispatchWindow> dispatchWindows = new ArrayList<DispatchWindow>();
    private List<DispatchPolicyShipmentParams> shipmentParams = new ArrayList<DispatchPolicyShipmentParams>();
    private long printWindowAgeIndays;

    public DispatchPolicy() {
    }

    public int getMaxDispatchRate() {
        return maxDispatchRate;
    }

    public void setMaxDispatchRate(int maxDispatchRate) {
        this.maxDispatchRate = maxDispatchRate;
    }

    public List<DispatchWindow> getDispatchWindows() {
        return dispatchWindows;
    }

    public List<DispatchPolicyShipmentParams> getShipmentParams() {
        return shipmentParams;
    }

    public void setShipmentParams(List<DispatchPolicyShipmentParams> shipmentParams) {
        this.shipmentParams = shipmentParams;
    }

    public long getLeadTimeBeforePrintInMilliseconds(Forsendelse f) {
        ShipmentPolicy sp = ShipmentPolicy.fromValue(f.getShipmentPolicy());
        DispatchPolicyShipmentParams p = getDispatchPolicyShipmentParam(sp);
        if ( p == null ) throw new IllegalArgumentException("Unhandled ShipmentPolicy:" + sp);
        return p.getLeadTimeBeforePrint()*24*60*60*1000;
    }


    public long getLeadTimeBeforeStopInMilliseconds(Forsendelse f){
        ShipmentPolicy sp = ShipmentPolicy.fromValue(f.getShipmentPolicy());
        DispatchPolicyShipmentParams p = getDispatchPolicyShipmentParam(sp);
        if ( p == null ) throw new IllegalArgumentException("Unhandled ShipmentPolicy:" + sp);
        return p.getLeadTimeBeforeStop();
    }

    public DispatchPolicyShipmentParams getDispatchPolicyShipmentParam(ShipmentPolicy sp){
        for ( DispatchPolicyShipmentParams p : shipmentParams) {
            if ( p.getShipmentPolicy().equals(sp)) return p;
        }
        return null;
    }

    public long getPrintWindowAgeIndays() {
        return printWindowAgeIndays;
    }

    public void setPrintWindowAgeIndays(long printWindowAgeIndays) {
        this.printWindowAgeIndays = printWindowAgeIndays;
    }

    public ShipmentPolicy[] getShipmentPolicies(){
        List<ShipmentPolicy> list = new ArrayList<ShipmentPolicy>();
        for ( DispatchPolicyShipmentParams p : getShipmentParams()) {
            list.add(p.getShipmentPolicy());
        }
        return list.toArray(new ShipmentPolicy[list.size()]);
    }

    public void addDispatchWindow(DispatchWindow dispatchWindow) {
        dispatchWindows.add(dispatchWindow);
    }

    public boolean isDispatchWindowOpen() {
        final Date now = new Date();
        if ( dispatchWindows.size() == 0) return true;

        for(DispatchWindow dp : dispatchWindows) {
            if ( dp.isInWindow(now)) return true;
        }
        return false;
    }
}
