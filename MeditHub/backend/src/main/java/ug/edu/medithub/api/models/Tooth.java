package ug.edu.medithub.api.models;

public class Tooth {

    private Number number;
    private boolean extraction;
    private boolean filling;
    private boolean cleaning;
    private boolean rootCanal;
    private boolean crown;
    private boolean implant;


    public Tooth(Number number, boolean extraction, boolean filling, boolean cleaning, boolean rootCanal, boolean crown, boolean implant) {
        this.number = number;
        this.extraction = extraction;
        this.filling = filling;
        this.cleaning = cleaning;
        this.rootCanal = rootCanal;
        this.crown = crown;
        this.implant = implant;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public boolean isExtraction() {
        return extraction;
    }

    public void setExtraction(boolean extraction) {
        this.extraction = extraction;
    }

    public boolean isFilling() {
        return filling;
    }

    public void setFilling(boolean filling) {
        this.filling = filling;
    }

    public boolean isCleaning() {
        return cleaning;
    }


    public void setCleaning(boolean cleaning) {
        this.cleaning = cleaning;
    }

    public boolean isRootCanal() {
        return rootCanal;
    }

    public void setRootCanal(boolean rootCanal) {
        this.rootCanal = rootCanal;
    }

    public boolean isCrown() {
        return crown;
    }

    public void setCrown(boolean crown) {
        this.crown = crown;
    }

    public boolean isImplant() {
        return implant;
    }

    public void setImplant(boolean implant) {
        this.implant = implant;
    }

    @Override
    public String toString() {
        return "Tooth{" +
                "number=" + number +
                ", extraction=" + extraction +
                ", filling=" + filling +
                ", cleaning=" + cleaning +
                ", rootCanal=" + rootCanal +
                ", crown=" + crown +
                ", implant=" + implant +
                '}';
    }

}
