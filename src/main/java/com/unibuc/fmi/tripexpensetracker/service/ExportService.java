package com.unibuc.fmi.tripexpensetracker.service;

import com.unibuc.fmi.tripexpensetracker.dto.IndividualSpendingDto;
import com.unibuc.fmi.tripexpensetracker.dto.TripSpendingDto;
import com.unibuc.fmi.tripexpensetracker.model.Spending;
import com.unibuc.fmi.tripexpensetracker.model.Trip;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.model.UserTrip;
import com.unibuc.fmi.tripexpensetracker.repository.TripRepository;
import com.unibuc.fmi.tripexpensetracker.repository.UserRepository;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ExportService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Autowired
    public ExportService(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    @SneakyThrows
    public InputStreamResource exportIndividualSpendingReport(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        List<IndividualSpendingDto> userSpending = new ArrayList<>();
        for(UserTrip userTrip : user.getTrips()) {
            for(Spending spending : userTrip.getSpendings()) {
                if(spending.getType().equals("individual")) {
                    double amount = spending.getAmount();

                    userSpending.add(
                            new IndividualSpendingDto.IndividualSpendingDtoBuilder()
                                    .tripName(userTrip.getTrip().getTitle())
                                    .type(spending.getType())
                                    .amount(amount)
                                    .amountDue(amount)
                                    .build()
                    );
                }
            }
        }
        userSpending.sort(Comparator.comparing(IndividualSpendingDto::getTripName));

        Workbook workbook = new XSSFWorkbook();
        String sheetName = "Individual_Spending";
        workbook.createSheet(sheetName);

        buildIndividualSpendingSchema(workbook, sheetName, userSpending);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
    }

    @SneakyThrows
    public InputStreamResource exportTripSpendingReport(Long userId, Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found!"));

        boolean userBelongsToTrip = false;
        for(UserTrip userTrip : trip.getUsers()) {
            if(Objects.equals(userTrip.getUser().getId(), userId)) {
                userBelongsToTrip = true;
                break;
            }
        }

        if(!userBelongsToTrip) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<TripSpendingDto> tripSpending = new ArrayList<>();
        for(UserTrip userTrip : trip.getUsers()) {
            for(Spending spending : userTrip.getSpendings()) {
                double amount = spending.getAmount();
                double amountDue = amount / spending.getParticipants().size();

                tripSpending.add(
                        new TripSpendingDto.TripSpendingDtoBuilder()
                                .users(spending.getParticipants().stream().map(spendingGroup ->
                                        spendingGroup.getUser().getEmail()).collect(Collectors.toList()))
                                .type(spending.getType())
                                .amount(amount)
                                .amountDue(amountDue)
                                .build()
                );
            }
        }
        tripSpending.sort((o1, o2) -> {
            if (o1.getUsers().size() == 1 && o2.getUsers().size() == 1) {
                return o1.getUsers().get(0).compareTo(o2.getUsers().get(0));
            }
            return o1.getUsers().size() - o2.getUsers().size();
        });

        Workbook workbook = new XSSFWorkbook();
        String sheetName = "Trip_Spending";
        workbook.createSheet(sheetName);

        buildTripSpendingSchema(workbook, sheetName, tripSpending);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
    }

    private void buildIndividualSpendingSchema(Workbook workbook, String sheetName, List<IndividualSpendingDto> userSpending) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 8);

        CellStyle headerStyle = getCellStyle(workbook, font, IndexedColors.GREY_25_PERCENT);
        headerStyle.setBorderBottom(BorderStyle.THICK);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        Sheet sheet = workbook.getSheet(sheetName);

        Row header = sheet.createRow(0);
        CellStyle defaultStyle = getCellStyle(workbook, font, IndexedColors.WHITE);

        Cell tripCell = header.createCell(0);
        tripCell.setCellValue("Trip name");
        tripCell.setCellStyle(headerStyle);

        Cell spendingTypeCell = header.createCell(1);
        spendingTypeCell.setCellValue("Spending type");
        spendingTypeCell.setCellStyle(headerStyle);

        Cell spendingAmountCell = header.createCell(2);
        spendingAmountCell.setCellValue("Spending amount");
        spendingAmountCell.setCellStyle(headerStyle);

        Cell amountDueCell = header.createCell(3);
        amountDueCell.setCellValue("Due amount");
        amountDueCell.setCellStyle(headerStyle);

        int index = 0;
        for(IndividualSpendingDto individualSpendingDto : userSpending) {
            Row row = sheet.createRow(++index);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(individualSpendingDto.getTripName());
            cell0.setCellStyle(defaultStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(individualSpendingDto.getType());
            cell1.setCellStyle(defaultStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(individualSpendingDto.getAmount());
            cell2.setCellStyle(defaultStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(individualSpendingDto.getAmountDue());
            cell3.setCellStyle(defaultStyle);

            borderRowCells(sheet, index);
        }
    }

    private void buildTripSpendingSchema(Workbook workbook, String sheetName, List<TripSpendingDto> tripSpending) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 8);

        CellStyle headerStyle = getCellStyle(workbook, font, IndexedColors.GREY_25_PERCENT);
        headerStyle.setBorderBottom(BorderStyle.THICK);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        Sheet sheet = workbook.getSheet(sheetName);

        Row header = sheet.createRow(0);
        CellStyle defaultStyle = getCellStyle(workbook, font, IndexedColors.WHITE);

        Cell tripCell = header.createCell(0);
        tripCell.setCellValue("Users");
        tripCell.setCellStyle(headerStyle);

        Cell spendingTypeCell = header.createCell(1);
        spendingTypeCell.setCellValue("Spending type");
        spendingTypeCell.setCellStyle(headerStyle);

        Cell spendingAmountCell = header.createCell(2);
        spendingAmountCell.setCellValue("Spending amount");
        spendingAmountCell.setCellStyle(headerStyle);

        Cell amountDueCell = header.createCell(3);
        amountDueCell.setCellValue("Due amount");
        amountDueCell.setCellStyle(headerStyle);

        int index = 0;
        for(TripSpendingDto tripSpendingDto : tripSpending) {
            Row row = sheet.createRow(++index);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(String.join(", ", tripSpendingDto.getUsers()));
            cell0.setCellStyle(defaultStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(tripSpendingDto.getType());
            cell1.setCellStyle(defaultStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(tripSpendingDto.getAmount());
            cell2.setCellStyle(defaultStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(tripSpendingDto.getAmountDue());
            cell3.setCellStyle(defaultStyle);

            borderRowCells(sheet, index);
        }
    }


    private CellStyle getCellStyle(Workbook workbook, Font font, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        return style;
    }

    private void borderRowCells(Sheet sheet, int index) {
        CellRangeAddress cellRange0 = CellRangeAddress.valueOf("A" + index + ":A" + (index + 1));
        CellRangeAddress cellRange1 = CellRangeAddress.valueOf("B" + index + ":B" + (index + 1));
        CellRangeAddress cellRange2 = CellRangeAddress.valueOf("C" + index + ":C" + (index + 1));
        CellRangeAddress cellRange3 = CellRangeAddress.valueOf("D" + index + ":D" + (index + 1));

        borderMultipleRanges(sheet, BorderStyle.THIN, cellRange0, cellRange1, cellRange2, cellRange3);
    }

    private void borderMultipleRanges(Sheet sheet, BorderStyle borderStyle, CellRangeAddress... cellRangeAddresses) {
        for (CellRangeAddress rangeAddress : cellRangeAddresses) {
            borderRange(sheet, borderStyle, rangeAddress);
        }
    }

    private void borderRange(Sheet sheet, BorderStyle borderStyle, CellRangeAddress rangeAddress) {
        RegionUtil.setBorderTop(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderRight(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderBottom(borderStyle, rangeAddress, sheet);
        RegionUtil.setBorderLeft(borderStyle, rangeAddress, sheet);
    }
}
