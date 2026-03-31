package cat;

import db.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

public class StockLowLevelManager {

    @FXML private TableView<StockLowLevel> StockLimitReport;

    @FXML
    public void initialize() {
        StockLimitReport.getItems().clear();
        List<StockLowLevel> lowStocks = DatabaseManager.getLowStockItems();
        StockLimitReport.getItems().addAll(lowStocks);
    }

    @FXML
    private void writeExcel() throws Exception {
        Writer writer = null;
        List<StockLowLevel> lowStocks = DatabaseManager.getLowStockItems();
        try {
            DirectoryChooser fileChooser = new DirectoryChooser();
            fileChooser.setTitle("Select a folder where to save the file");
            File directory = fileChooser.showDialog(null);
            if (directory == null) {return;}

            String date = LocalDate.now().toString();
            File file = new File(directory, "StockReport_" + date + ".csv");

            writer = new BufferedWriter(new FileWriter(file));

            writer.write("Item ID,Description,\"Availability,packs\",\"Stock Limit, packs\",Recommended Min Order\n");

            for (StockLowLevel items : lowStocks) {

                String text = items.getItemId() + "," + items.getDescription() + "," + items.getAvailability() + "," +items.getStockLimit()
                        + "," + items.getRecommended();



                writer.write(text + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}