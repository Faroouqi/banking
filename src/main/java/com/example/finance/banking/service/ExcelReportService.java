package com.example.finance.banking.service;

import com.example.finance.banking.entity.Loan;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.repository.LoanRepository;
import com.example.finance.banking.repository.TransactionRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelReportService {

    // =========================================================
    // COLOUR PALETTE
    // =========================================================

    /** Deep navy – title bars & totals rows */
    private static final String C_NAVY        = "1B2A4A";
    /** Teal – column header backgrounds */
    private static final String C_TEAL        = "0D7377";
    /** Soft amber – KPI "Net Balance" card */
    private static final String C_AMBER       = "F4A261";
    /** Light amber bg – "Pending Loans" KPI card */
    private static final String C_AMBER_LIGHT = "FFF3CD";
    /** Soft green – income / positive values */
    private static final String C_GREEN       = "06D6A0";
    /** Light green bg – income KPI card */
    private static final String C_GREEN_LIGHT = "D4EDDA";
    /** Soft red – expense / debit values */
    private static final String C_RED         = "FF6B6B";
    /** Light red bg – expense KPI card */
    private static final String C_RED_LIGHT   = "F8D7DA";
    /** Pale blue – alternating row background */
    private static final String C_ROW_ALT     = "EAF4FB";
    /** White – standard row background */
    private static final String C_ROW_WHITE   = "FFFFFF";
    /** Light grey border colour */
    private static final String C_BORDER      = "CBD5E0";
    /** Near-black for body text */
    private static final String C_TEXT_DARK   = "1A202C";
    /** White text */
    private static final String C_WHITE       = "FFFFFF";
    /** Loan status: PAID – green bg */
    private static final String C_STATUS_PAID_BG  = "D4EDDA";
    private static final String C_STATUS_PAID_FG  = "1A7F4B";
    /** Loan status: PARTIAL – amber bg */
    private static final String C_STATUS_PART_BG  = "FFF3CD";
    private static final String C_STATUS_PART_FG  = "856404";
    /** Loan status: PENDING – red bg */
    private static final String C_STATUS_PEND_BG  = "F8D7DA";
    private static final String C_STATUS_PEND_FG  = "721C24";

    private static final String FONT_NAME = "Calibri";
    private static final String INR_FORMAT = "₹#,##0.00";

    // =========================================================
    // FIELDS
    // =========================================================

    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;

    private Integer pendingLoanAmount = 0;

    public ExcelReportService(TransactionRepository transactionRepository,
                              LoanRepository loanRepository) {
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
    }

    // =========================================================
    // ENTRY POINT
    // =========================================================

    public byte[] generateDummyFinanceReport(Integer id) throws IOException {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Create sheets in logical order; Dashboard is inserted at position 0 later
            XSSFSheet transactionSheet = workbook.createSheet("Transactions");
            XSSFSheet loanSheet        = workbook.createSheet("Loans");
            XSSFSheet summarySheet     = workbook.createSheet("Dashboard");

            // Populate data sheets first (summary needs pendingLoanAmount)
            createTransactionSheet(workbook, transactionSheet, id);
            createLoanSheet(workbook, loanSheet, id);
            createSummarySheet(workbook, summarySheet, id);

            // Move Dashboard to first tab
            workbook.setSheetOrder("Dashboard", 0);
            workbook.setActiveSheet(0);

            // Freeze panes below banner + sub-header + column-header rows
            transactionSheet.createFreezePane(0, 3);
            loanSheet.createFreezePane(0, 3);
            summarySheet.createFreezePane(0, 7);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // =========================================================
    // TRANSACTION SHEET
    // =========================================================

    private void createTransactionSheet(XSSFWorkbook workbook,
                                        XSSFSheet sheet,
                                        Integer id) {

        // ── Banner row ──────────────────────────────────────
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        Row banner = sheet.createRow(0);
        banner.setHeightInPoints(30);
        Cell bannerCell = banner.createCell(0);
        bannerCell.setCellValue("  Transaction Ledger  |  " + LocalDate.now().getYear());
        bannerCell.setCellStyle(createBannerStyle(workbook));

        // ── Sub-header row ───────────────────────────────────
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
        Row subRow = sheet.createRow(1);
        subRow.setHeightInPoints(16);
        Cell subCell = subRow.createCell(0);
        subCell.setCellValue("  Showing all transactions for the current year");
        subCell.setCellStyle(createSubHeaderStyle(workbook));

        // ── Column headers ───────────────────────────────────
        CellStyle headerStyle = createColumnHeaderStyle(workbook);
        String[] headers = {"Date", "Type", "Category", "Amount", "Note"};
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(22);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ── Data rows ────────────────────────────────────────
        CellStyle moneyStyle     = createMoneyStyle(workbook, false);
        CellStyle dataStyle      = createDataStyle(workbook, false);
        CellStyle alternateStyle = createDataStyle(workbook, true);

        List<Transaction> transactions =
                transactionRepository.findByYear(LocalDate.now().getYear(), id);

        for (int i = 0; i < transactions.size(); i++) {
            Transaction tx  = transactions.get(i);
            Row row         = sheet.createRow(i + 3);
            row.setHeightInPoints(18);
            boolean isAlt   = (i % 2 == 0);
            boolean isCredit = tx.getType().toString().equalsIgnoreCase("CREDIT");

            CellStyle rowStyle = isAlt ? alternateStyle : dataStyle;

            createStyledCell(row, 0, tx.getDate().toString(), rowStyle);
            createTypeBadgeCell(row, 1, tx.getType().toString(), workbook, isAlt, isCredit);
            createStyledCell(row, 2, tx.getCategory(), rowStyle);

            Cell amtCell = row.createCell(3);
            amtCell.setCellValue(tx.getAmount().doubleValue());
            amtCell.setCellStyle(createAmountStyle(workbook, isCredit, isAlt));

            createStyledCell(row, 4, tx.getNote(), rowStyle);
        }

        // ── Totals row ───────────────────────────────────────
        int totalRow = transactions.size() + 3;
        Row totals = sheet.createRow(totalRow);
        totals.setHeightInPoints(22);
        CellStyle totalStyle = createTotalsRowStyle(workbook);
        CellStyle totalMoneyStyle = createTotalsMoneyStyle(workbook);
        for (int c = 0; c < 5; c++) {
            Cell tc = totals.createCell(c);
            tc.setCellStyle(c == 3 ? totalMoneyStyle : totalStyle);
        }
        totals.getCell(2).setCellValue("TOTAL");
        totals.getCell(2).getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        totals.getCell(3).setCellFormula(
                "SUM(D4:D" + totalRow + ")"
        );

        // ── Auto-filter & column widths ───────────────────────
        sheet.setAutoFilter(new CellRangeAddress(2, 2, 0, 4));
        sheet.setColumnWidth(0, 4400);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 5500);
        sheet.setColumnWidth(4, 10000);
    }

    // =========================================================
    // LOAN SHEET
    // =========================================================

    private void createLoanSheet(XSSFWorkbook workbook,
                                 XSSFSheet sheet,
                                 Integer id) {

        // ── Banner row ──────────────────────────────────────
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        Row banner = sheet.createRow(0);
        banner.setHeightInPoints(30);
        Cell bannerCell = banner.createCell(0);
        bannerCell.setCellValue("  Loan Tracker  |  Active & Partial Loans");
        bannerCell.setCellStyle(createBannerStyle(workbook));

        // ── Sub-header ───────────────────────────────────────
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
        Row subRow = sheet.createRow(1);
        subRow.setHeightInPoints(16);
        Cell subCell = subRow.createCell(0);
        subCell.setCellValue("  Status: PAID = fully settled | PARTIAL = ongoing | PENDING = not started");
        subCell.setCellStyle(createSubHeaderStyle(workbook));

        // ── Column headers ───────────────────────────────────
        CellStyle headerStyle = createColumnHeaderStyle(workbook);
        String[] headers = {"Person Name", "Date Given", "Amount", "Paid Amount", "Remaining", "Status"};
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(22);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ── Data rows ────────────────────────────────────────
        List<Loan> loans = loanRepository.findByUserId(id);
        loans = loans.stream()
                .filter(loan -> loan.getStatus() != Loan.LoanStatus.PAID)
                .toList();

        BigDecimal totalPending = loans.stream()
                .map(Loan::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pendingLoanAmount = totalPending.intValue();

        for (int i = 0; i < loans.size(); i++) {
            Loan loan   = loans.get(i);
            Row row     = sheet.createRow(i + 3);
            row.setHeightInPoints(20);
            boolean isAlt = (i % 2 == 0);

            CellStyle rowStyle = isAlt
                    ? createDataStyle(workbook, true)
                    : createDataStyle(workbook, false);
            CellStyle moneyStyle = createMoneyStyle(workbook, isAlt);

            createStyledCell(row, 0, loan.getPersonName(), rowStyle);
            createStyledCell(row, 1, loan.getDate().toString(), rowStyle);
            createMoneyCell(row, 2, loan.getAmount().doubleValue(), moneyStyle);
            createMoneyCell(row, 3, loan.getPaidAmount().doubleValue(), moneyStyle);
            createMoneyCell(row, 4,
                    loan.getAmount().doubleValue() - loan.getPaidAmount().doubleValue(),
                    moneyStyle);
            createLoanStatusCell(row, 5, loan.getStatus().toString(), workbook);
        }

        // ── Totals row ───────────────────────────────────────
        int totalRow = loans.size() + 3;
        Row totals = sheet.createRow(totalRow);
        totals.setHeightInPoints(22);
        CellStyle totalStyle      = createTotalsRowStyle(workbook);
        CellStyle totalMoneyStyle = createTotalsMoneyStyle(workbook);

        for (int c = 0; c < 6; c++) {
            Cell tc = totals.createCell(c);
            tc.setCellStyle(c >= 2 && c <= 4 ? totalMoneyStyle : totalStyle);
        }
        totals.getCell(1).setCellValue("TOTALS");
        for (int c = 2; c <= 4; c++) {
            char col = (char) ('A' + c);
            totals.getCell(c).setCellFormula(
                    "SUM(" + col + "4:" + col + totalRow + ")"
            );
        }

        // ── Auto-filter & column widths ───────────────────────
        sheet.setAutoFilter(new CellRangeAddress(2, 2, 0, 5));
        sheet.setColumnWidth(0, 7500);
        sheet.setColumnWidth(1, 4800);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 4500);
    }

    // =========================================================
    // DASHBOARD / SUMMARY SHEET
    // =========================================================

    private void createSummarySheet(XSSFWorkbook workbook,
                                    XSSFSheet sheet,
                                    Integer id) {

        // ── Big title banner ──────────────────────────────────
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(40);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("  FINANCIAL ANALYTICS REPORT  |  " + LocalDate.now().getYear());
        titleCell.setCellStyle(createMainTitleStyle(workbook));

        // spacer row
        sheet.createRow(1).setHeightInPoints(10);

        // ── KPI Cards (rows 2-5, 3 cols each) ────────────────
        createKpiCard(workbook, sheet, 2, 0,  "Total Income",   null, C_GREEN_LIGHT, C_TEXT_DARK, true);
        createKpiCard(workbook, sheet, 2, 3,  "Total Expense",  null, C_RED_LIGHT,   C_TEXT_DARK, false);
        createKpiCard(workbook, sheet, 2, 6,  "Net Balance",    null, C_AMBER_LIGHT, C_TEXT_DARK, false);
        createKpiCard(workbook, sheet, 2, 9,  "Pending Loans",  null, "E8EAF6",      C_TEXT_DARK, false);

        // KPI values (row 4 = index 4 in sheet, card label is row 2 merged, value row 3-5 merged)
        List<Object[]> summary =
                transactionRepository.getMonthlySummaryTillLastMonth(id, LocalDate.now());

        BigDecimal totalIncome  = summary.stream().map(r -> (BigDecimal) r[1]).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = summary.stream().map(r -> (BigDecimal) r[2]).reduce(BigDecimal.ZERO, BigDecimal::add);
        int balance = Math.max(totalIncome.intValue() - totalExpense.intValue(), 0);

        setKpiValue(workbook, sheet, 4, 0, totalIncome.doubleValue(),  C_GREEN_LIGHT);
        setKpiValue(workbook, sheet, 4, 3, totalExpense.doubleValue(), C_RED_LIGHT);
        setKpiValue(workbook, sheet, 4, 6, (double) balance,          C_AMBER_LIGHT);
        setKpiValue(workbook, sheet, 4, 9, (double) pendingLoanAmount,"E8EAF6");

        // spacer row
        sheet.createRow(6).setHeightInPoints(14);

        // ── Monthly breakdown table ───────────────────────────
        CellStyle secHeader = createSectionHeaderStyle(workbook);
        CellStyle colHeader = createColumnHeaderStyle(workbook);
        CellStyle moneyStyle = createMoneyStyle(workbook, false);
        CellStyle altStyle   = createDataStyle(workbook, true);
        CellStyle dataStyle  = createDataStyle(workbook, false);

        // Section label
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 2));
        Row secRow = sheet.createRow(7);
        secRow.setHeightInPoints(22);
        Cell secCell = secRow.createCell(0);
        secCell.setCellValue("Monthly Breakdown");
        secCell.setCellStyle(secHeader);

        // Column headers
        Row chRow = sheet.createRow(8);
        chRow.setHeightInPoints(20);
        for (int c = 0; c < 3; c++) {
            Cell ch = chRow.createCell(c);
            ch.setCellValue(new String[]{"Month", "Income", "Expense"}[c]);
            ch.setCellStyle(colHeader);
        }

        // Map for month abbreviations
        Map<Integer, String> monthMap = new HashMap<>();
        monthMap.put(1,"Jan"); monthMap.put(2,"Feb"); monthMap.put(3,"Mar");
        monthMap.put(4,"Apr"); monthMap.put(5,"May"); monthMap.put(6,"Jun");
        monthMap.put(7,"Jul"); monthMap.put(8,"Aug"); monthMap.put(9,"Sep");
        monthMap.put(10,"Oct"); monthMap.put(11,"Nov"); monthMap.put(12,"Dec");

        int dataStartRow = 9;
        int rowNum = dataStartRow;

        for (Object[] r : summary) {
            int    mon  = ((Number) r[0]).intValue();
            double inc  = ((Number) r[1]).doubleValue();
            double exp  = ((Number) r[2]).doubleValue();

            Row row = sheet.createRow(rowNum);
            row.setHeightInPoints(18);

            boolean isAlt = (rowNum % 2 == 0);
            CellStyle rowStyle = isAlt ? altStyle : dataStyle;
            CellStyle mStyle   = createMoneyStyle(workbook, isAlt);

            Cell mCell = row.createCell(0);
            mCell.setCellValue(monthMap.getOrDefault(mon, String.valueOf(mon)));
            mCell.setCellStyle(rowStyle);

            createMoneyCell(row, 1, inc, mStyle);
            createMoneyCell(row, 2, exp, mStyle);

            rowNum++;
        }

        // Totals row for monthly table
        Row totRow = sheet.createRow(rowNum);
        totRow.setHeightInPoints(22);
        CellStyle tStyle  = createTotalsRowStyle(workbook);
        CellStyle tmStyle = createTotalsMoneyStyle(workbook);
        totRow.createCell(0).setCellStyle(tStyle);
        totRow.createCell(1).setCellStyle(tmStyle);
        totRow.createCell(2).setCellStyle(tmStyle);
        totRow.getCell(0).setCellValue("TOTAL");
        totRow.getCell(1).setCellFormula("SUM(B" + (dataStartRow + 1) + ":B" + rowNum + ")");
        totRow.getCell(2).setCellFormula("SUM(C" + (dataStartRow + 1) + ":C" + rowNum + ")");

        // ── Charts ───────────────────────────────────────────
        int chartDataEnd = rowNum - 1; // last data row (0-indexed for CellRangeAddress)
        createBarChart(sheet, dataStartRow, chartDataEnd);
        populatePieDataAndCreateChart(workbook, sheet, rowNum + 3);

        // ── Column widths ─────────────────────────────────────
        sheet.setColumnWidth(0, 7000);
        for (int c = 1; c <= 11; c++) sheet.setColumnWidth(c, 5000);
    }

    // =========================================================
    // KPI CARD HELPERS
    // =========================================================

    /**
     * Creates a 3-column wide KPI card label row (rows startRow..startRow+1 merged).
     * The value is set separately via {@link #setKpiValue}.
     */
    private void createKpiCard(XSSFWorkbook workbook, XSSFSheet sheet,
                               int startRow, int startCol,
                               String label, String value,
                               String bgHex, String fgHex,
                               boolean isFirst) {

        // Ensure rows exist
        for (int r = startRow; r <= startRow + 3; r++) {
            if (sheet.getRow(r) == null) {
                Row row = sheet.createRow(r);
                row.setHeightInPoints(r == startRow ? 18 : 26);
            }
        }

        // Label row – merged across 3 cols
        sheet.addMergedRegion(
                new CellRangeAddress(startRow, startRow, startCol, startCol + 2));
        Cell labelCell = sheet.getRow(startRow).createCell(startCol);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(createKpiLabelStyle(workbook, bgHex, fgHex));

        // Value rows – merge rows startRow+1 to startRow+3 across 3 cols
        for (int r = startRow + 1; r <= startRow + 3; r++) {
            if (sheet.getRow(r) == null) {
                Row row = sheet.createRow(r);
                row.setHeightInPoints(22);
            }
            sheet.addMergedRegion(
                    new CellRangeAddress(r, r, startCol, startCol + 2));
            Cell vc = sheet.getRow(r).createCell(startCol);
            vc.setCellStyle(createKpiValueStyle(workbook, bgHex, fgHex));
        }
    }

    private void setKpiValue(XSSFWorkbook workbook, XSSFSheet sheet,
                             int row, int col, double value, String bgHex) {
        Cell cell = sheet.getRow(row).getCell(col);
        if (cell == null) cell = sheet.getRow(row).createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(createKpiValueFilledStyle(workbook, bgHex));
    }

    // =========================================================
    // BAR CHART
    // =========================================================

    private void createBarChart(XSSFSheet sheet, int dataStart, int dataEnd) {

        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        // Anchor: col 4 row 7 → col 11 row 21  (0-indexed)
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 4, 7, 12, 22);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Monthly Income vs Expense");
        chart.setTitleOverlay(false);
        chart.getOrAddLegend().setPosition(LegendPosition.BOTTOM);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Month");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Amount (₹)");
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // +1 because CellRangeAddress is 0-based
        XDDFDataSource<String> months =
                XDDFDataSourcesFactory.fromStringCellRange(sheet,
                        new CellRangeAddress(dataStart, dataEnd, 0, 0));

        XDDFNumericalDataSource<Double> income =
                XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(dataStart, dataEnd, 1, 1));

        XDDFNumericalDataSource<Double> expense =
                XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(dataStart, dataEnd, 2, 2));

        XDDFBarChartData data =
                (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        data.setBarDirection(BarDirection.COL);

        XDDFBarChartData.Series incomeSeries =
                (XDDFBarChartData.Series) data.addSeries(months, income);
        incomeSeries.setTitle("Income", null);
        incomeSeries.setShowLeaderLines(false);

        XDDFBarChartData.Series expenseSeries =
                (XDDFBarChartData.Series) data.addSeries(months, expense);
        expenseSeries.setTitle("Expense", null);

        chart.plot(data);
    }

    // =========================================================
    // PIE CHART
    // =========================================================

    private void populatePieDataAndCreateChart(XSSFWorkbook workbook,
                                               XSSFSheet sheet,
                                               int startRow) {

        CellStyle headerStyle = createColumnHeaderStyle(workbook);
        CellStyle dataStyle   = createDataStyle(workbook, false);
        CellStyle altStyle    = createDataStyle(workbook, true);
        CellStyle moneyStyle  = createMoneyStyle(workbook, false);

        // Section header
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 2));
        Row secRow = sheet.createRow(startRow);
        secRow.setHeightInPoints(22);
        Cell secCell = secRow.createCell(0);
        secCell.setCellValue("Expense Category Split");
        secCell.setCellStyle(createSectionHeaderStyle(workbook));

        // Column headers
        Row chRow = sheet.createRow(startRow + 1);
        chRow.setHeightInPoints(20);
        chRow.createCell(0).setCellValue("Category");
        chRow.createCell(1).setCellValue("Amount");
        chRow.getCell(0).setCellStyle(headerStyle);
        chRow.getCell(1).setCellStyle(headerStyle);

        String[] categories = {"Rent", "Groceries", "Utilities", "Dining", "Shopping", "Travel", "Transport"};
        int[]    amounts    = {66000,   6500,         4300,        3900,     5200,       8500,     3100};

        int pieDataStart = startRow + 2;

        for (int i = 0; i < categories.length; i++) {
            Row row = sheet.createRow(pieDataStart + i);
            row.setHeightInPoints(18);
            boolean isAlt = (i % 2 == 0);
            Cell catCell = row.createCell(0);
            catCell.setCellValue(categories[i]);
            catCell.setCellStyle(isAlt ? altStyle : dataStyle);
            Cell amtCell = row.createCell(1);
            amtCell.setCellValue(amounts[i]);
            amtCell.setCellStyle(createMoneyStyle(workbook, isAlt));
        }

        int pieDataEnd = pieDataStart + categories.length - 1;

        // Chart anchor
        XSSFDrawing drawing  = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor =
                drawing.createAnchor(0, 0, 0, 0, 4, startRow + 1, 12, startRow + 16);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Expense Category Split");
        chart.setTitleOverlay(false);
        chart.getOrAddLegend().setPosition(LegendPosition.RIGHT);

        XDDFDataSource<String> cat =
                XDDFDataSourcesFactory.fromStringCellRange(sheet,
                        new CellRangeAddress(pieDataStart, pieDataEnd, 0, 0));

        XDDFNumericalDataSource<Double> val =
                XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                        new CellRangeAddress(pieDataStart, pieDataEnd, 1, 1));

//        XDDFPieChartData data =
//                (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
//
//        XDDFPieChartData.Series series =
//                (XDDFPieChartData.Series) data.addSeries(cat, val);
//        series.setTitle("Expenses", null);
//        series.setExplosion(3L);
//
//        chart.plot(data);
    }

    // =========================================================
    // STYLES
    // =========================================================

    /** Full-width dark navy banner at the top of each sheet */
    private CellStyle createBannerStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(14);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(C_WHITE), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes(C_NAVY), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    /** Light italic sub-header row beneath the banner */
    private CellStyle createSubHeaderStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setItalic(true);
        f.setFontHeight(9);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes("718096"), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes("F7FAFC"), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    /** Large centred title for the Dashboard sheet */
    private CellStyle createMainTitleStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(16);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(C_WHITE), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes(C_NAVY), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    /** Teal column header cells */
    private CellStyle createColumnHeaderStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(10);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(C_WHITE), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes(C_TEAL), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorder(s);
        return s;
    }

    /** Section label bar (slightly lighter than column header) */
    private CellStyle createSectionHeaderStyle(XSSFWorkbook wb) {
        CellStyle s = createColumnHeaderStyle(wb);
        s.setFillForegroundColor(new XSSFColor(hexToBytes("145A5C"), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        return s;
    }

    /** Standard data cell; isAlt = zebra-striped row */
    private CellStyle createDataStyle(XSSFWorkbook wb, boolean isAlt) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setFontHeight(10);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(C_TEXT_DARK), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(
                hexToBytes(isAlt ? C_ROW_ALT : C_ROW_WHITE), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorder(s);
        return s;
    }

    /** INR currency format – inherits row background */
    private CellStyle createMoneyStyle(XSSFWorkbook wb, boolean isAlt) {
        CellStyle s = createDataStyle(wb, isAlt);
        DataFormat fmt = wb.createDataFormat();
        s.setDataFormat(fmt.getFormat(INR_FORMAT));
        s.setAlignment(HorizontalAlignment.RIGHT);
        return s;
    }

    /**
     * Amount cell coloured green (credit) or red (debit).
     * Background still follows zebra-stripe rule.
     */
    private CellStyle createAmountStyle(XSSFWorkbook wb,
                                        boolean isCredit, boolean isAlt) {
        CellStyle s = createMoneyStyle(wb, isAlt);
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(10);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(
                hexToBytes(isCredit ? C_STATUS_PAID_FG : C_STATUS_PEND_FG), null));
        s.setFont(f);
        return s;
    }

    /** Dark navy totals / summary row */
    private CellStyle createTotalsRowStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(10);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(C_WHITE), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes(C_NAVY), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorder(s);
        return s;
    }

    /** Totals row – money format variant */
    private CellStyle createTotalsMoneyStyle(XSSFWorkbook wb) {
        CellStyle s = createTotalsRowStyle(wb);
        DataFormat fmt = wb.createDataFormat();
        s.setDataFormat(fmt.getFormat(INR_FORMAT));
        s.setAlignment(HorizontalAlignment.RIGHT);
        return s;
    }

    // ── KPI card styles ──────────────────────────────────────

    private CellStyle createKpiLabelStyle(XSSFWorkbook wb,
                                          String bgHex, String fgHex) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(10);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(fgHex), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes(bgHex), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorder(s);
        return s;
    }

    private CellStyle createKpiValueStyle(XSSFWorkbook wb,
                                          String bgHex, String fgHex) {
        CellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(hexToBytes(bgHex), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorder(s);
        return s;
    }

    /** KPI value cell: large bold INR number */
    private CellStyle createKpiValueFilledStyle(XSSFWorkbook wb, String bgHex) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(14);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(C_TEXT_DARK), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes(bgHex), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        DataFormat fmt = wb.createDataFormat();
        s.setDataFormat(fmt.getFormat(INR_FORMAT));
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorder(s);
        return s;
    }

    // =========================================================
    // LOAN STATUS BADGE CELL
    // =========================================================

    /**
     * Creates a styled "badge" cell for loan status.
     * PAID → green, PARTIAL → amber, PENDING → red.
     */
    private void createLoanStatusCell(Row row, int col,
                                      String status, XSSFWorkbook wb) {
        String bg, fg;
        switch (status.toUpperCase()) {
            case "PAID"    -> { bg = C_STATUS_PAID_BG; fg = C_STATUS_PAID_FG; }
            case "PARTIAL" -> { bg = C_STATUS_PART_BG; fg = C_STATUS_PART_FG; }
            default        -> { bg = C_STATUS_PEND_BG; fg = C_STATUS_PEND_FG; }
        }

        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(10);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(hexToBytes(fg), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(hexToBytes(bg), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorder(s);

        Cell cell = row.createCell(col);
        cell.setCellValue(status);
        cell.setCellStyle(s);
    }

    // =========================================================
    // TRANSACTION TYPE BADGE CELL
    // =========================================================

    /**
     * Creates a bold coloured cell for transaction type.
     * CREDIT → green text, DEBIT → red text.
     */
    private void createTypeBadgeCell(Row row, int col,
                                     String type, XSSFWorkbook wb,
                                     boolean isAlt, boolean isCredit) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f  = wb.createFont();
        f.setBold(true);
        f.setFontHeight(10);
        f.setFontName(FONT_NAME);
        f.setColor(new XSSFColor(
                hexToBytes(isCredit ? C_STATUS_PAID_FG : C_STATUS_PEND_FG), null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(
                hexToBytes(isAlt ? C_ROW_ALT : C_ROW_WHITE), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        applyThinBorder(s);

        Cell cell = row.createCell(col);
        cell.setCellValue(type);
        cell.setCellStyle(s);
    }

    // =========================================================
    // BORDER HELPER
    // =========================================================

    private void applyThinBorder(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        ((XSSFCellStyle) s).setBorderColor(
                XSSFCellBorder.BorderSide.TOP,
                new XSSFColor(hexToBytes(C_BORDER), null));
        ((XSSFCellStyle) s).setBorderColor(
                XSSFCellBorder.BorderSide.BOTTOM,
                new XSSFColor(hexToBytes(C_BORDER), null));
        ((XSSFCellStyle) s).setBorderColor(
                XSSFCellBorder.BorderSide.LEFT,
                new XSSFColor(hexToBytes(C_BORDER), null));
        ((XSSFCellStyle) s).setBorderColor(
                XSSFCellBorder.BorderSide.RIGHT,
                new XSSFColor(hexToBytes(C_BORDER), null));
    }

    // =========================================================
    // CELL CREATION HELPERS
    // =========================================================

    private void createStyledCell(Row row, int col,
                                  String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createMoneyCell(Row row, int col,
                                 double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    // =========================================================
    // HEX COLOUR UTILITY
    // =========================================================

    /**
     * Converts a 6-character hex string (e.g. "1B2A4A") to a 3-byte RGB array
     * for use with {@link XSSFColor}.
     */
    private static byte[] hexToBytes(String hex) {
        return new byte[]{
                (byte) Integer.parseInt(hex.substring(0, 2), 16),
                (byte) Integer.parseInt(hex.substring(2, 4), 16),
                (byte) Integer.parseInt(hex.substring(4, 6), 16)
        };
    }
}