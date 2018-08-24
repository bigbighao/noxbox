package live.noxbox.model;

import java.util.Map;

public class Filters {

    private boolean supply;
    private boolean demand;
    private String maxPrice;
    private Map<String,Boolean> types;

    public Filters() { }

    public Filters(Boolean supply, boolean demand, String price, Map<String,Boolean> types) {
        this.supply = supply;
        this.demand = demand;
        this.maxPrice = price;
        this.types = types;
    }

    public Boolean getSupply() {
        return supply;
    }

    public Filters setSupply(boolean supply) {
        this.supply = supply;
        return this;
    }

    public Boolean getDemand() {
        return demand;
    }

    public Filters setDemand(boolean demand) {
        this.demand = demand;
        return this;
    }

    public String getPrice() {
        return maxPrice;
    }

    public Filters setPrice(String price) {
        this.maxPrice = price;
        return this;
    }

    public Map<String,Boolean> getTypes() {
        return types;
    }

    public Filters setTypes(Map<String,Boolean> types) {
        this.types = types;
        return this;
    }
}