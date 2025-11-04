package org.delcom.starter.controllers;

import java.util.Base64;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeControllerTest {

    private HomeController controller;

    @BeforeEach
    void setUp() {
        controller = new HomeController();
    }

    @Test
    void testInformasiNim_AllCases() {
        String resultValid = controller.informasiNim("11S23001");
        assertTrue(resultValid.contains("Sarjana Informatika") && resultValid.contains("Angkatan: 2023"));
        assertTrue(controller.informasiNim("123").contains("minimal 8 karakter"));
        assertTrue(controller.informasiNim(null).contains("minimal 8 karakter"));
        assertTrue(controller.informasiNim("99X23123").contains("Unknown"));
    }

    @Test
    void testPerolehanNilai_Valid() {
        String data = "UAS|85|40\nUTS|75|30\nPA|90|20\nK|100|10";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertTrue(result.contains("84.50") && result.contains("Grade: B"));
    }

    @Test
    void testPerolehanNilai_FullBranchCoverage() {
        String data = "UAS|90|50\n\nTugas|80|0\nInvalid Line\nHanya|Dua\nNilai|abc|def\n---\nIni|tidak|dihitung";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertEquals("Nilai Akhir: 45.00 (Total Bobot: 50%)\nGrade: E", result);
    }
    
    @Test
    void testPerolehanNilai_InvalidBase64() {
        assertThrows(IllegalArgumentException.class, () -> controller.perolehanNilai("!@#"));
    }
    
    @Test
    void testPerolehanNilai_GradeA() {
        String data = "Project|95|100";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertTrue(result.contains("95.00") && result.contains("Grade: A"));
    }

    @Test
    void testPerolehanNilai_GradeC() {
        String data = "Kuis|70|100";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertTrue(result.contains("70.00") && result.contains("Grade: C"));
    }

    @Test
    void testPerolehanNilai_GradeD() {
        String data = "Praktikum|60|100";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertTrue(result.contains("60.00") && result.contains("Grade: D"));
    }

    @Test
    void testPerbedaanL_AllCases() {
        String b64Valid = Base64.getEncoder().encodeToString("UULL".getBytes());
        assertTrue(controller.perbedaanL(b64Valid).contains("Perbedaan Jarak: 8"));
        String b64InvalidChar = Base64.getEncoder().encodeToString("U R D L X Y Z".getBytes());
        assertTrue(controller.perbedaanL(b64InvalidChar).contains("Perbedaan Jarak: 0"));
    }

    @Test
    void testPerbedaanL_InvalidBase64() {
        assertThrows(IllegalArgumentException.class, () -> controller.perbedaanL("!@#"));
    }

    @Test
    void testPalingTer_Valid() {
        String text = "terbaik terbaik termahal";
        String b64 = Base64.getEncoder().encodeToString(text.getBytes());
        assertTrue(controller.palingTer(b64).contains("'terbaik' (muncul 2 kali)"));
    }

    @Test
    void testPalingTer_FullBranchCoverage() {
        // Kasus: Tidak ada kata "ter"
        String noTer = Base64.getEncoder().encodeToString("hello world".getBytes());
        assertEquals("Tidak ditemukan kata yang berawalan 'ter'.", controller.palingTer(noTer));
        
        // --- PERBAIKAN UNTUK COVERAGE ---
        // Kasus: Input yang menghasilkan string kosong ("") setelah di-split oleh "\\W+"
        // Ini akan menguji cabang `!word.isEmpty()` secara eksplisit.
        String emptyWordCase = Base64.getEncoder().encodeToString("!tercepat terlambat".getBytes());
        assertTrue(controller.palingTer(emptyWordCase).contains("'tercepat' (muncul 1 kali)"));

        // Kasus: Beberapa kata "ter" dengan berbagai pemisah untuk memastikan frekuensi benar
        String multiple = "terbaik terendah terbaik terburuk terendah terbaik";
        String b64Multiple = Base64.getEncoder().encodeToString(multiple.getBytes());
        assertTrue(controller.palingTer(b64Multiple).contains("'terbaik' (muncul 3 kali)"));
    }
    
    @Test
    void testPalingTer_InvalidBase64() {
        assertThrows(IllegalArgumentException.class, () -> controller.palingTer("!@#"));
    }
}