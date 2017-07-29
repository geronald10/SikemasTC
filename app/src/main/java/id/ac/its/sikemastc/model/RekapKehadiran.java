package id.ac.its.sikemastc.model;

public class RekapKehadiran {

    private String tanggalKehadiran;
    private String waktuKehadiran;
    private String tempatKehadiran;
    private String statusKehadiran;
    private String pertemuanKe;
    private String pesanKehadiran;

    public RekapKehadiran(String tanggalKehadiran, String waktuKehadiran, String tempatKehadiran,
                          String statusKehadiran, String pertemuanKe, String pesanKehadiran) {
        this.tanggalKehadiran = tanggalKehadiran;
        this.waktuKehadiran = waktuKehadiran;
        this.tempatKehadiran = tempatKehadiran;
        this.statusKehadiran = statusKehadiran;
        this.pertemuanKe = pertemuanKe;
        this.pesanKehadiran = pesanKehadiran;
    }

    public String getTanggalKehadiran() {
        return tanggalKehadiran;
    }

    public void setTanggalKehadiran(String tanggalKehadiran) {
        this.tanggalKehadiran = tanggalKehadiran;
    }

    public String getWaktuKehadiran() {
        return waktuKehadiran;
    }

    public void setWaktuKehadiran(String waktuKehadiran) {
        this.waktuKehadiran = waktuKehadiran;
    }

    public String getTempatKehadiran() {
        return tempatKehadiran;
    }

    public void setTempatKehadiran(String tempatKehadiran) {
        this.tempatKehadiran = tempatKehadiran;
    }

    public String getStatusKehadiran() {
        return statusKehadiran;
    }

    public void setStatusKehadiran(String statusKehadiran) {
        this.statusKehadiran = statusKehadiran;
    }

    public String getPertemuanKe() {
        return pertemuanKe;
    }

    public void setPertemuanKe(String pertemuanKe) {
        this.pertemuanKe = pertemuanKe;
    }

    public String getPesanKehadiran() {
        return pesanKehadiran;
    }

    public void setPesanKehadiran(String pesanKehadiran) {
        this.pesanKehadiran = pesanKehadiran;
    }
}
