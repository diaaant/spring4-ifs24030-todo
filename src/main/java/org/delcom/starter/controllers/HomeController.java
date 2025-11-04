package org.delcom.starter.controllers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private static final DecimalFormat df;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
    }

    // ... (Metode informasiNim dan perolehanNilai tidak berubah) ...
    @GetMapping("/informasi-nim")
    public String informasiNim(@RequestParam String nim) {
        if (nim == null || nim.length() < 8) {
            return "NIM tidak valid: minimal 8 karakter.";
        }

        String prefix = nim.substring(0, 3);
        String angkatan = "20" + nim.substring(3, 5);
        String lastThree = nim.substring(nim.length() - 3);
        int urutan = Integer.parseInt(lastThree);

        Map<String, String> prodiMap = new HashMap<>();
        prodiMap.put("11S", "Sarjana Informatika");
        prodiMap.put("12S", "Sarjana Sistem Informasi");
        prodiMap.put("14S", "Sarjana Teknik Elektro");

        String prodi = prodiMap.getOrDefault(prefix, "Unknown");

        return String.format("Informasi NIM %s:\n>> Program Studi: %s\n>> Angkatan: %s\n>> Urutan: %d",
                nim, prodi, angkatan, urutan);
    }

    @GetMapping("/perolehan-nilai")
    public String perolehanNilai(@RequestParam String strBase64) {
        String data;
        try {
            byte[] bytes = Base64.getDecoder().decode(strBase64);
            data = new String(bytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input Base64 tidak valid.");
        }

        String[] lines = data.split("\n");
        double totalNilai = 0.0;
        int totalBobot = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.equals("---")) continue;

            if (line.contains("|")) {
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    try {
                        double nilai = Double.parseDouble(parts[1].trim());
                        int bobot = Integer.parseInt(parts[2].trim());
                        if (bobot > 0) {
                            totalNilai += nilai * (bobot / 100.0);
                            totalBobot += bobot;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        String grade;
        if (totalNilai >= 85) grade = "A";
        else if (totalNilai >= 75) grade = "B";
        else if (totalNilai >= 65) grade = "C";
        else if (totalNilai >= 55) grade = "D";
        else grade = "E";

        return String.format("Nilai Akhir: %s (Total Bobot: %d%%)\nGrade: %s",
                df.format(totalNilai), totalBobot, grade);
    }

    @GetMapping("/perbedaan-l")
    public String perbedaanL(@RequestParam String strBase64) {
        String path;
        try {
            byte[] bytes = Base64.getDecoder().decode(strBase64);
            path = new String(bytes).trim();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input Base64 tidak valid.");
        }

        int x1 = 0, y1 = 0;
        for (char c : path.toCharArray()) {
            switch (c) {
                case 'U': y1++; break;
                case 'D': y1--; break;
                case 'L': x1--; break;
                case 'R': x1++; break;
                default: break;
            }
        }
        int[] end1 = new int[]{x1, y1};

        // --- PERBAIKAN LOGIKA REVERSE PATH ---
        StringBuilder sb = new StringBuilder();
        for (char c : path.toCharArray()) {
            switch (c) {
                case 'U': sb.append('D'); break;
                case 'D': sb.append('U'); break;
                case 'L': sb.append('R'); break;
                case 'R': sb.append('L'); break;
                // Biarkan karakter lain lolos agar switch kedua bisa mengujinya
                default: sb.append(c); break;
            }
        }
        String opposite = sb.toString();

        int x2 = 0, y2 = 0;
        for (char c : opposite.toCharArray()) {
            switch (c) {
                case 'U': y2++; break;
                case 'D': y2--; break;
                case 'L': x2--; break;
                case 'R': x2++; break;
                default: break;
            }
        }
        int[] end2 = new int[]{x2, y2};

        int distance = Math.abs(end1[0] - end2[0]) + Math.abs(end1[1] - end2[1]);

        return String.format("Path Original: %s -> (%d, %d)\nPath Kebalikan: %s -> (%d, %d)\nPerbedaan Jarak: %d",
                path, end1[0], end1[1], opposite, end2[0], end2[1], distance);
    }
    
    // ... (Metode palingTer tidak berubah) ...
    @GetMapping("/paling-ter")
    public String palingTer(@RequestParam String strBase64) {
        String text;
        try {
            byte[] bytes = Base64.getDecoder().decode(strBase64);
            text = new String(bytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input Base64 tidak valid.");
        }
        
        Map<String, Integer> freq = new HashMap<>();
        String[] words = text.toLowerCase().split("\\W+");

        for (String word : words) {
            if (!word.isEmpty() && word.startsWith("ter")) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }

        if (freq.isEmpty()) {
            return "Tidak ditemukan kata yang berawalan 'ter'.";
        }

        String topWord = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                topWord = e.getKey();
            }
        }

        return String.format("Kata 'ter' yang paling sering muncul adalah '%s' (muncul %d kali).", topWord, maxCount);
    }
}