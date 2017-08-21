package id.ac.its.sikemastc.model;

public class Perkuliahan {

    private String idPerkuliahan;
    private String kodeRuangan;
    private String kodeMk;
    private String kodeSemester;
    private String namaMk;
    private String kelasMk;
    private String ruangMk;
    private String pertemuanKe;
    private String tanggal;
    private String hari;
    private String waktuMulai;
    private String waktuSelesai;
    private String statusDosen;
    private String statusPerkuliahan;
    private String statusKehadiran;

    public Perkuliahan(String idPerkuliahan, String kodeRuangan, String kodeMk, String kodeSemester, String namaMK,
                       String kelasMK, String ruangMK, String pertemuanKe, String hari,
                       String waktuMulai, String waktuSelesai, String statusDosen,
                       String statusPerkuliahan) {
        this.idPerkuliahan = idPerkuliahan;
        this.kodeRuangan = kodeRuangan;
        this.kodeMk = kodeMk;
        this.kodeSemester = kodeSemester;
        this.namaMk = namaMK;
        this.kelasMk = kelasMK;
        this.ruangMk = ruangMK;
        this.pertemuanKe = pertemuanKe;
        this.hari = hari;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.statusDosen = statusDosen;
        this.statusPerkuliahan = statusPerkuliahan;
    }

    public Perkuliahan(String idPerkuliahan, String kodeRuangan, String kodeMk, String kodeSemester, String namaMK,
                       String kelasMK, String ruangMK, String pertemuanKe, String hari,
                       String waktuMulai, String waktuSelesai, String statusDosen,
                       String statusPerkuliahan, String statusKehadiran) {
        this.idPerkuliahan = idPerkuliahan;
        this.kodeRuangan = kodeRuangan;
        this.kodeMk = kodeMk;
        this.kodeSemester = kodeSemester;
        this.namaMk = namaMK;
        this.kelasMk = kelasMK;
        this.ruangMk = ruangMK;
        this.pertemuanKe = pertemuanKe;
        this.hari = hari;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.statusDosen = statusDosen;
        this.statusPerkuliahan = statusPerkuliahan;
        this.statusKehadiran = statusKehadiran;
    }

    public Perkuliahan(String idPerkuliahan, String kodeMk, String namaMk, String kelasMk,
                       String ruangMk, String pertemuanKe, String tanggal, String waktuMulai,
                       String waktuSelesai) {
        this.idPerkuliahan = idPerkuliahan;
        this.kodeMk = kodeMk;
        this.namaMk = namaMk;
        this.kelasMk = kelasMk;
        this.ruangMk = ruangMk;
        this.pertemuanKe = pertemuanKe;
        this.tanggal = tanggal;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    public String getStatusKehadiran() {
        return statusKehadiran;
    }

    public void setStatusKehdiran(String statusKehdiran) {
        this.statusKehadiran = statusKehdiran;
    }

    public String getIdPerkuliahan() {
        return idPerkuliahan;
    }

    public void setIdPerkuliahan(String idPerkuliahan) {
        this.idPerkuliahan = idPerkuliahan;
    }

    public String getKodeRuangan() {
        return kodeRuangan;
    }

    public void setKodeRuangan(String kodeRuangan) {
        this.kodeRuangan = kodeRuangan;
    }

    public String getKodeMk() {
        return kodeMk;
    }

    public void setKodeMk(String kodeMk) {
        this.kodeMk = kodeMk;
    }

    public String getKodeSemester() {
        return kodeSemester;
    }

    public void setKodeSemester(String kodeSemester) {
        this.kodeSemester = kodeSemester;
    }

    public String getNamaMk() {
        return namaMk;
    }

    public void setNamaMk(String namaMk) {
        this.namaMk = namaMk;
    }

    public String getKelasMk() {
        return kelasMk;
    }

    public void setKelasMk(String kelasMk) {
        this.kelasMk = kelasMk;
    }

    public String getRuangMk() {
        return ruangMk;
    }

    public void setRuangMk(String ruangMk) {
        this.ruangMk = ruangMk;
    }

    public String getPertemuanKe() {
        return pertemuanKe;
    }

    public void setPertemuanKe(String pertemuanKe) {
        this.pertemuanKe = pertemuanKe;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(String waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(String waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getStatusDosen() {
        return statusDosen;
    }

    public void setStatusDosen(String statusDosen) {
        this.statusDosen = statusDosen;
    }

    public String getStatusPerkuliahan() {
        return statusPerkuliahan;
    }

    public void setStatusPerkuliahan(String statusPerkuliahan) {
        this.statusPerkuliahan = statusPerkuliahan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
