package org.oagi.score.repo.api.bie.model;

public class WrappedAsbie {

    private Abie fromAbie;

    private Asbie asbie;

    private WrappedAsbiep toAsbiep;

    public Abie getFromAbie() {
        return fromAbie;
    }

    public void setFromAbie(Abie fromAbie) {
        this.fromAbie = fromAbie;
    }

    public Asbie getAsbie() {
        return asbie;
    }

    public void setAsbie(Asbie asbie) {
        this.asbie = asbie;
    }

    public WrappedAsbiep getToAsbiep() {
        return toAsbiep;
    }

    public void setToAsbiep(WrappedAsbiep toAsbiep) {
        this.toAsbiep = toAsbiep;
    }
}
