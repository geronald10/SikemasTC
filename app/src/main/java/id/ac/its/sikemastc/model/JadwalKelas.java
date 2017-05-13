package id.ac.its.sikemastc.model;

import java.util.List;

public class JadwalKelas {

    private String idListJadwalMK;
    private String kodeMK;
    private String namaMK;
    private String kelasMK;
    private String ruangMK;
    private String tanggalPertemuan;
    private String pertemuanKe;
    private String statusPerkuliahan;
    private List<PesertaPerkuliahan> peserta;
    private List<WaktuKelas> kelasWaktu;
    private List<DosenKelas> dosenKelas;

    public JadwalKelas() {
    }

    public JadwalKelas(String idListJadwalMK, String namaMK, String kelasMK, String ruangMK,
                       String tanggalPertemuan, String pertemuanKe, List<WaktuKelas> kelasWaktu,
                       String statusPerkuliahan) {
        this.idListJadwalMK = idListJadwalMK;
        this.namaMK = namaMK;
        this.kelasMK = kelasMK;
        this.ruangMK = ruangMK;
        this.tanggalPertemuan = tanggalPertemuan;
        this.pertemuanKe = pertemuanKe;
        this.kelasWaktu = kelasWaktu;
        this.statusPerkuliahan = statusPerkuliahan;
    }

    public JadwalKelas(String idListJadwalMK, String kodeMK, String namaMK, String kelasMK,
                       String ruangMK, List<PesertaPerkuliahan> peserta, List<WaktuKelas> kelasWaktu,
                       List<DosenKelas> dosenKelas) {
        this.idListJadwalMK = idListJadwalMK;
        this.kodeMK = kodeMK;
        this.namaMK = namaMK;
        this.kelasMK = kelasMK;
        this.ruangMK = ruangMK;
        this.peserta = peserta;
        this.kelasWaktu = kelasWaktu;
        this.dosenKelas = dosenKelas;
    }

    public String getIdListJadwalMK() {
        return idListJadwalMK;
    }

    public void setIdListJadwalMK(String idListJadwalMK) {
        this.idListJadwalMK = idListJadwalMK;
    }

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

    public String getRuangMK() {
        return ruangMK;
    }

    public void setRuangMK(String ruangMK) {
        this.ruangMK = ruangMK;
    }

    public List<PesertaPerkuliahan> getPeserta() {
        return peserta;
    }

    public void setPeserta(List<PesertaPerkuliahan> peserta) {
        this.peserta = peserta;
    }

    public List<WaktuKelas> getKelasWaktu() {
        return kelasWaktu;
    }

    public void setKelasWaktu(List<WaktuKelas> kelasWaktu) {
        this.kelasWaktu = kelasWaktu;
    }

    public List<DosenKelas> getDosenKelas() {
        return dosenKelas;
    }

    public void setDosenKelas(List<DosenKelas> dosenKelas) {
        this.dosenKelas = dosenKelas;
    }

    public String getTanggalPertemuan() {
        return tanggalPertemuan;
    }

    public void setTanggalPertemuan(String tanggalPertemuan) {
        this.tanggalPertemuan = tanggalPertemuan;
    }

    public String getPertemuanKe() {
        return pertemuanKe;
    }

    public void setPertemuanKe(String pertemuanKe) {
        this.pertemuanKe = pertemuanKe;
    }

    public String getStatusPerkuliahan() {
        return statusPerkuliahan;
    }

    public void setStatusPerkuliahan(String statusPerkuliahan) {
        this.statusPerkuliahan = statusPerkuliahan;
    }
}
