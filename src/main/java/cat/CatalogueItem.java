package cat;

import javafx.beans.property.*;

public class CatalogueItem {
    private final StringProperty itemId = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty packageType = new SimpleStringProperty();
    private final StringProperty unit = new SimpleStringProperty();
    private final IntegerProperty unitsPerPack = new SimpleIntegerProperty();
    private final DoubleProperty packageCost = new SimpleDoubleProperty();
    private final IntegerProperty availability = new SimpleIntegerProperty();
    private final IntegerProperty stockLimit = new SimpleIntegerProperty();

    public CatalogueItem(String itemId, String description, String packageType,
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
    }

    public StringProperty itemIdProperty() { return itemId; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty packageTypeProperty() { return packageType; }
    public StringProperty unitProperty() { return unit; }
    public IntegerProperty unitsPerPackProperty() { return unitsPerPack; }
    public DoubleProperty packageCostProperty() { return packageCost; }
    public IntegerProperty availabilityProperty() { return availability; }
    public IntegerProperty stockLimitProperty() { return stockLimit; }
}