package cat;

import db.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import java.util.List;

public class StockLowLevelManager {

    @FXML private TableView<StockLowLevel> StockLimitReport;

    @FXML
    public void initialize() {
        StockLimitReport.getItems().clear();
        List<StockLowLevel> lowStocks = DatabaseManager.getLowStockItems();
        StockLimitReport.getItems().addAll(lowStocks);
    }
}