package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.example.TeamOptimizer.scoreTeams;

public class FileUtil {
    public static void loadPlayers(List<Player> providedPlayers, List<Player> providedReserve) {
        int dcNameIndex = 0;
        int steamNameIndex = 0;
        int currentPremEloIndex = 0;
        int maxPremEloIndex = 0;
        int currentFaceitEloIndex = 0;
        int maxFaceitEloIndex = 0;

        List<Player> playerList = new ArrayList<>();

        FileInputStream file;
        Workbook workbook;
        try {
            file = new FileInputStream("elo.xlsx");
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataFormatter formatter = new DataFormatter();

        Sheet sheet = workbook.getSheetAt(0);
        int index = 0;
        for (Cell headerCell : sheet.getRow(0)) {
            switch (headerCell.getStringCellValue()) {
                case "Discord Nickname":
                    dcNameIndex = index;
                    break;
                case "Steam Nickname":
                    steamNameIndex = index;
                    break;
                case "Premier Elo":
                    currentPremEloIndex = index;
                    break;
                case "Max premier Elo":
                    maxPremEloIndex = index;
                    break;
                case "Faceit Elo":
                    currentFaceitEloIndex = index;
                    break;
                case "Max faceit Elo":
                    maxFaceitEloIndex = index;
                    break;
            }
            index++;
        }

        int rowIndex = 0;
        for (Row row : sheet) {
            if (rowIndex == 0) {    //skipping headers
                rowIndex++;
                continue;
            }
            if (row.getCell(0) == null) {   //skipping metadata rows
                rowIndex++;
                continue;
            }

            int currentPremiereValue = parseInt(formatter.formatCellValue(row.getCell(currentPremEloIndex)));
            int maxPremiereValue = parseInt(formatter.formatCellValue(row.getCell(maxPremEloIndex)));
            int currentFaceitValue = parseInt(formatter.formatCellValue(row.getCell(currentFaceitEloIndex)));
            int maxFaceitValue = parseInt(formatter.formatCellValue(row.getCell(maxFaceitEloIndex)));

            // If missing max, taking current value and vice versa
            Player data = Player.builder()
                    .discordName(row.getCell(dcNameIndex).getStringCellValue())
                    .steamName(row.getCell(steamNameIndex).getStringCellValue())
                    .currentPremiere(currentPremiereValue == 0 ? maxPremiereValue : currentPremiereValue)
                    .maxPremiere(maxPremiereValue == 0 ? currentPremiereValue : maxPremiereValue)
                    .currentFaceit(currentFaceitValue == 0 ? maxFaceitValue : currentFaceitValue)
                    .maxFaceit(maxFaceitValue == 0 ? currentFaceitValue : maxFaceitValue)
                    .build();
            playerList.add(data);
            rowIndex++;
        }

        List<Player> reservePlayers = new ArrayList<>();
        for (int i = 0; i <= playerList.size() % 5; i++) {
            Player reservePlayer = playerList.getLast();
            reservePlayers.add(reservePlayer);
            playerList.remove(reservePlayer);
        }

        providedPlayers.addAll(playerList);
        providedReserve.addAll(reservePlayers);
    }

    public static void outputTeams(List<List<Team>> potentialTeams, List<Player> reserve) {
        try (FileWriter outputWriter = new FileWriter("teams.txt")) {
            for (int i = 0; i < 5; i++) {
                List<Team> option = potentialTeams.get(i);
                outputWriter.write("\nOptimized Teams (Faceit diff = " + scoreTeams(option) + "):\n");
                int num = 1;
                for (Team team : option) {
                    outputWriter.write("Team " + num++ + " - " + team.toString() + "\n");
                }
            }

            outputWriter.write("\nReserve players:\n");
            for (Player player : reserve) {
                outputWriter.write("* " + player.getDiscordName() + "(" + player.getCurrentFaceit() + ")\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
