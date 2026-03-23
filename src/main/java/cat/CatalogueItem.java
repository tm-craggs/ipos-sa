package cat;

import javafx.beans.property.*;

public class CatalogueItem {
    private final IntegerProperty itemId = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty packageType = new SimpleStringProperty();
    private final StringProperty unit = new SimpleStringProperty();
    private final IntegerProperty unitsPerPack = new SimpleIntegerProperty();
    private final DoubleProperty packageCost = new SimpleDoubleProperty();
    private final IntegerProperty availability = new SimpleIntegerProperty();
    private final IntegerProperty stockLimit = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();

    public CatalogueItem(int itemId, String description, String packageType,
                         String unit, int unitsPerPack, double packageCost,
                         int availability, int stockLimit) {
        this.itemId.set(itemId);
        this.description.set(description);
        this.packageType.set(packageType);
        this.unit.set(unit);
        this.unitsPerPack.set(unitsPerPack);
        this.packageCost.set(packageCost);
        this.availability.set(availability);
        this.stockLimit.set(stockLimit);
        this.status.set(availability < stockLimit ? "Low stock" : "OK");
    }

    public IntegerProperty itemIdProperty() { return itemId; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty packageTypeProperty() { return packageType; }
    public StringProperty unitProperty() { return unit; }
    public IntegerProperty unitsPerPackProperty() { return unitsPerPack; }
    public DoubleProperty packageCostProperty() { return packageCost; }
    public IntegerProperty availabilityProperty() { return availability; }
    public IntegerProperty stockLimitProperty() { return stockLimit; }
    public StringProperty statusProperty() { return status; }

    public int getItemId() { return itemId.get(); }
    public String getDescription() { return description.get(); }
    public String getPackageType() { return packageType.get(); }
    public String getUnit() { return unit.get(); }
    public int getUnitsPerPack() { return unitsPerPack.get(); }
    public double getPackageCost() { return packageCost.get(); }
    public int getAvailability() { return availability.get(); }
    public int getStockLimit() { return stockLimit.get(); }
    public String getStatus() { return status.get(); }
}