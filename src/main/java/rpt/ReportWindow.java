package rpt;

public class ReportWindow {

    private final ReportManager reportManager = new ReportManager();

    public void openAllInvoicesReport() {
        String report = reportManager.generateAllInvoicesReport("2026-01-01", "2026-01-31");
        System.out.println(report);
    }

    public void openMerchantReport() {
        String report = reportManager.generateMerchantInvoicesReport("M001", "2026-01-01", "2026-01-31");
        System.out.println(report);
    }
}