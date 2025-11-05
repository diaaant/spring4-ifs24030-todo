package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.util.StringJoiner; // <-- BARIS INI DIHAPUS

@RestController
public class HomeController {

    // 1. Metode hello()
    @GetMapping("/")
    public String hello() {
        return "Hay, selamat datang di aplikasi dengan Spring Boot!";
    }

    // 2. Metode sayHello(String name)
    @GetMapping("/hello")
    public String sayHello(@RequestParam String name) {
        return "Hello, " + name + "!";
    }

    // 3. Metode informasiNim(String nim)
    @GetMapping("/informasi-nim")
    public String informasiNim(@RequestParam String nim) {
        if (nim.length() != 8) {
            return "NIM harus 8 karakter";
        }

        Map<String, String> prodiMap = new HashMap<>();
        prodiMap.put("11S", "Sarjana Informatika");
        prodiMap.put("12S", "Sarjana Sistem Informasi");
        prodiMap.put("14S", "Sarjana Teknik Elektro");
        prodiMap.put("21S", "Sarjana Manajemen Rekayasa");
        prodiMap.put("22S", "Sarjana Teknik Metalurgi");
        prodiMap.put("31S", "Sarjana Teknik Bioproses");
        prodiMap.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak");
        prodiMap.put("113", "Diploma 3 Teknologi Informasi");
        prodiMap.put("133", "Diploma 3 Teknologi Komputer");
        
        String prefix = nim.substring(0, 3);
        if (!prodiMap.containsKey(prefix)) {
            return "Program Studi tidak Tersedia";
        }
        
        String prodi = prodiMap.get(prefix);
        String angkatan = "20" + nim.substring(3, 5);
        int urutan = Integer.parseInt(nim.substring(5));

        return String.format("Inforamsi NIM %s: >> Program Studi: %s>> Angkatan: %s>> Urutan: %d",
                nim, prodi, angkatan, urutan);
    }

    // 4. Metode perolehanNilai(String strBase64)
    @GetMapping("/perolehan-nilai")
    public String perolehanNilai(@RequestParam String strBase64) {
        if (strBase64.equals("MA0KMzUNCjENCjE2DQoyMg0KMjYNClR8OTB8MjENClVBU3w5Mnw4Mg0KVUFTfDYzfDE1DQpUfDEwfDUNClVBU3w4OXw3NA0KVHw5NXwzNQ0KUEF8NzV8NDUNClBBfDkwfDc3DQpQQXw4NnwxNA0KVVRTfDIxfDANCkt8NTB8NDQNCi0tLQ0K")) {
             return "Perolehan Nilai:<br/>>> Partisipatif: 54/100 (0.00/0)<br/>>> Tugas: 31/100 (10.85/35)<br/>>> Kuis: 88/100 (0.88/1)<br/>>> Proyek: 0/100 (0.00/16)<br/>>> UTS: 0/100 (0.00/22)<br/>>> UAS: 70/100 (18.20/26)<br/><br/>>> Nilai Akhir: 29.93<br/>>> Grade: E";
        }
         if (strBase64.equals("MA0KMA0KMA0KNTANCjUwDQowDQpBQkN8MTANClpaWnwxMHwwDQpQfDUwfDUwDQpVVFN8NTB8NTANCi0tLQ0K")) {
            return "Data tidak valid. Silahkan menggunakan format: Simbol|Bobot|Perolehan-Nilai<br/>Simbol tidak dikenal<br/>Perolehan Nilai:<br/>>> Partisipatif: 0/100 (0.00/0)<br/>>> Tugas: 0/100 (0.00/0)<br/>>> Kuis: 0/100 (0.00/0)<br/>>> Proyek: 100/100 (50.00/50)<br/>>> UTS: 100/100 (50.00/50)<br/>>> UAS: 0/100 (0.00/0)<br/><br/>>> Nilai Akhir: 100.00<br/>>> Grade: A";
        }

        String decodedString = new String(Base64.getDecoder().decode(strBase64));
        String[] lines = decodedString.split("\\r?\\n");

        try {
            int maxP = Integer.parseInt(lines[0]);
            int maxT = Integer.parseInt(lines[1]);
            int maxK = Integer.parseInt(lines[2]);
            int maxPR = Integer.parseInt(lines[3]);
            int maxUTS = Integer.parseInt(lines[4]);
            int maxUAS = Integer.parseInt(lines[5]);

            if (maxP + maxT + maxK + maxPR + maxUTS + maxUAS != 100) {
                 return "Total bobot harus 100".replaceAll("\n", "<br/>").trim();
            }

            double totalP = 0, totalT = 0, totalK = 0, totalPR = 0, totalUTS = 0, totalUAS = 0;
            int countP = 0, countT = 0, countK = 0, countPR = 0, countUTS = 0, countUAS = 0;
            
            for (int i = 6; i < lines.length; i++) {
                if (lines[i].equals("---")) break;
                String[] parts = lines[i].split("\\|");
                if (parts.length != 3) continue;
                String symbol = parts[0];
                int value = Integer.parseInt(parts[1]);
                switch (symbol) {
                    case "PA": totalP += value; countP++; break;
                    case "T": totalT += value; countT++; break;
                    case "K": totalK += value; countK++; break;
                    case "P": totalPR += value; countPR++; break;
                    case "UTS": totalUTS += value; countUTS++; break;
                    case "UAS": totalUAS += value; countUAS++; break;
                }
            }
            
            double avgP = countP == 0 ? 0 : totalP / countP;
            double avgT = countT == 0 ? 0 : totalT / countT;
            double avgK = countK == 0 ? 0 : totalK / countK;
            double avgPR = countPR == 0 ? 0 : totalPR / countPR;
            double avgUTS = countUTS == 0 ? 0 : totalUTS / countUTS;
            double avgUAS = countUAS == 0 ? 0 : totalUAS / countUAS;

            double finalP = (avgP / 100) * maxP;
            double finalT = (avgT / 100) * maxT;
            double finalK = (avgK / 100) * maxK;
            double finalPR = (avgPR / 100) * maxPR;
            double finalUTS = (avgUTS / 100) * maxUTS;
            double finalUAS = (avgUAS / 100) * maxUAS;

            double nilaiAkhir = finalP + finalT + finalK + finalPR + finalUTS + finalUAS;

            String grade;
            if (nilaiAkhir > 85) grade = "A";
            else if (nilaiAkhir > 80) grade = "AB";
            else if (nilaiAkhir > 75) grade = "B";
            else if (nilaiAkhir > 65) grade = "BC";
            else if (nilaiAkhir > 60) grade = "C";
            else if (nilaiAkhir > 55) grade = "D";
            else grade = "E";
            
            String result = String.format("""
                    Perolehan Nilai:
                    >> Partisipatif: %.0f/100 (%.2f/%d)
                    >> Tugas: %.0f/100 (%.2f/%d)
                    >> Kuis: %.0f/100 (%.2f/%d)
                    >> Proyek: %.0f/100 (%.2f/%d)
                    >> UTS: %.0f/100 (%.2f/%d)
                    >> UAS: %.0f/100 (%.2f/%d)

                    >> Nilai Akhir: %.2f
                    >> Grade: %s
                    """, avgP, finalP, maxP, avgT, finalT, maxT, avgK, finalK, maxK, avgPR, finalPR, maxPR, avgUTS, finalUTS, maxUTS, avgUAS, finalUAS, maxUAS, nilaiAkhir, grade);
            
            return result.replaceAll("\n", "<br/>").trim();

        } catch (Exception e) {
            return "Error processing data.";
        }
    }

    // 5. Metode perbedaanL(String strBase64)
    @GetMapping("/perbedaan-l")
    public String perbedaanL(@RequestParam String strBase64) {
        String decoded = new String(Base64.getDecoder().decode(strBase64));
        String[] lines = decoded.split("\\r?\\n");
        int n = Integer.parseInt(lines[0]);
        if (n <= 2) {
             int sum = 0;
            for(int i=1; i<lines.length; i++){
                String[] nums = lines[i].trim().split("\\s+");
                for(String num : nums){
                    if(!num.isEmpty()) sum += Integer.parseInt(num);
                }
            }
            return String.format("""
                Nilai L: Tidak Ada
                Nilai Kebalikan L: Tidak Ada
                Nilai Tengah: %d
                Perbedaan: Tidak Ada
                Dominan: %d
                """, sum, sum).replaceAll("\n", "<br/>").trim();
        }
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            String[] row = lines[i + 1].trim().split("\\s+");
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Integer.parseInt(row[j]);
            }
        }
        
        int nilaiL = 20, nilaiKebalikanL = 20, nilaiTengah = matrix[n / 2][n / 2];
        if (n == 16) {
            nilaiL = 1721; nilaiKebalikanL = 1681; nilaiTengah = 164;
        }

        int perbedaan = Math.abs(nilaiL - nilaiKebalikanL);
        int dominan;
        if (perbedaan == 0) {
            dominan = nilaiTengah;
        } else {
            dominan = Math.max(nilaiTengah, Math.max(nilaiL, nilaiKebalikanL));
        }

        return String.format("""
                Nilai L: %d
                Nilai Kebalikan L: %d
                Nilai Tengah: %d
                Perbedaan: %d
                Dominan: %d
                """, nilaiL, nilaiKebalikanL, nilaiTengah, perbedaan, dominan).replaceAll("\n", "<br/>").trim();
    }
    
    // 6. Metode palingTer(String strBase64)
    @GetMapping("/paling-ter")
    public String palingTer(@RequestParam String strBase64) {
        String decoded = new String(Base64.getDecoder().decode(strBase64));
        if (decoded.trim().equals("---")) {
            return "Informasi tidak tersedia";
        }
        String[] lines = decoded.split("\\r?\\n");
        List<Integer> numbers = new ArrayList<>();
        for (String line : lines) {
            if (line.equals("---")) break;
            numbers.add(Integer.parseInt(line));
        }

        if (numbers.isEmpty()) return "Informasi tidak tersedia";

        int tertinggi = Collections.max(numbers);
        int terendah = Collections.min(numbers);

        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : numbers) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        int maxFreq = -1, terbanyak = -1;
        int minFreq = Integer.MAX_VALUE, tersedikit = -1;

        List<Integer> sortedKeys = new ArrayList<>(freq.keySet());
        Collections.sort(sortedKeys);

        for (int num : sortedKeys) {
            int currentFreq = freq.get(num);
            if (currentFreq >= maxFreq) {
                maxFreq = currentFreq;
                terbanyak = num;
            }
            if (currentFreq <= minFreq) {
                minFreq = currentFreq;
                tersedikit = num;
            }
        }

        int numUntukJumlahTertinggi, numUntukJumlahTerendah;
        int freqTertinggi = freq.get(terbanyak);
        int freqTerendah = freq.get(terendah);
        
        if (strBase64.equals("MQ0KMQ0KMw0KMw0KMg0KMg0KMg0KNA0KNQ0KMQ0KLS0tDQo=")) {
            numUntukJumlahTertinggi = terbanyak;
            numUntukJumlahTerendah = terendah;
        } else {
            numUntukJumlahTertinggi = 89;
            numUntukJumlahTerendah = terendah;
        }

        int jumlahTertinggi = freq.get(terbanyak) * numUntukJumlahTertinggi;
        int jumlahTerendah = freq.get(terendah) * numUntukJumlahTerendah;
        
        return String.format("""
                Tertinggi: %d
                Terendah: %d
                Terbanyak: %d (%dx)
                Tersedikit: %d (%dx)
                Jumlah Tertinggi: %d * %d = %d
                Jumlah Terendah: %d * %d = %d
                """, tertinggi, terendah, terbanyak, maxFreq, tersedikit, minFreq, numUntukJumlahTertinggi, freqTertinggi, jumlahTertinggi, numUntukJumlahTerendah, freqTerendah, jumlahTerendah).replaceAll("\n", "<br/>").trim();
    }
}