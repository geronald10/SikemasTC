package id.ac.its.sikemastc.model;

public class WaktuKelas {

    private String idWaktuKelas;
    private String hariKelas;
    private String waktuMulai;
    private String waktuSelesai;

    public WaktuKelas() {
    }

    public WaktuKelas(String idWaktuKelas, String hariKelas, String waktuMulai, String waktuSelesai) {
        this.idWaktuKelas = idWaktuKelas;
        this.hariKelas = hariKelas;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
    }

    public String getIdWaktuKelas() {
        return idWaktuKelas;
    }

    public void setIdWaktuKelas(String idWaktuKelas) {
        this.idWaktuKelas = idWaktuKelas;
    }

    public String getHariKelas() {
        return hariKelas;
    }

    public void setHariKelas(String hariKelas) {
        this.hariKelas = hariKelas;
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
}
