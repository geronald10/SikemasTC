package id.ac.its.sikemastc.model;

import java.util.ArrayList;
import java.util.List;

public class JadwalMataKuliahModel {

    private String kodeMK;
    private String namaMK;
    private String kelasMK;
    private String hariMK;
    private String waktuMK;
    private String ruangMK;

    public String getKodeMK() {
        return kodeMK;
    }

    public void setKodeMK(String kodeMK) {
        this.kodeMK = kodeMK;
    }

    public String getNamaMK() {
        return namaMK;
    }

    public void setNamaMK(String namaMK) {
        this.namaMK = namaMK;
    }

    public String getKelasMK() {
        return kelasMK;
    }

    public void setKelasMK(String kelasMK) {
        this.kelasMK = kelasMK;
    }

    public String getHariMK() {
        return hariMK;
    }

    public void setHariMK(String hariMK) {
        this.hariMK = hariMK;
    }

    public String getWaktuMK() {
        return waktuMK;
    }

    public void setWaktuMK(String waktuMK) {
        this.waktuMK = waktuMK;
    }

    public String getRuangMK() {
        return ruangMK;
    }

    public void setRuangMK(String ruangMK) {
        this.ruangMK = ruangMK;
    }

    public static List<JadwalMataKuliahModel> getDummyDataList() {

        String[] kodeMK = new String[]{"KI141425", "KI141426", "KI141411", "KI141415", "KI141425"};
        String[] namaMK = new String[]{"Pemrograman Perangkat Bergerak", "Pemrograman Berbasis Kerangka Kerja",
                "Struktur Data", "Pemrograman Berbasis Objek", "Pemrgraman Perangkat Bergerak"};
        String[] kelasMK = new String[]{"A", "A", "C", "C", "C"};
        String[] hariMK = new String[]{"Kamis", "Senin", "Selasa", "Rabu", "Kamis"};
        String[] waktuMK = new String[]{"12.30 - 15.00", "07.30 - 10.00", "12.30 - 15.00", "10.00 - 12.30", "07.30 - 10.00"};
        String[] ruangMK = new String[]{"IF - 105a", "IF - 103", "IF - 105b", "IF - 108", "IF - 106"};

        List<JadwalMataKuliahModel> dataList = new ArrayList<>();
        for (int i = 0; i < kodeMK.length; i++) {
            JadwalMataKuliahModel jadwal = new JadwalMataKuliahModel();
            jadwal.setKodeMK(kodeMK[i]);
            jadwal.setNamaMK(namaMK[i]);
            jadwal.setKelasMK(kelasMK[i]);
            jadwal.setHariMK(hariMK[i]);
            jadwal.setWaktuMK(waktuMK[i]);
            jadwal.setRuangMK(ruangMK[i]);
            dataList.add(jadwal);
        }
        return dataList;
    }
}
