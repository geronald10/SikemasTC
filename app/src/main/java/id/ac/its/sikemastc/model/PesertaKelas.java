package id.ac.its.sikemastc.model;

public class PesertaKelas {

    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String statusKehadiran;

    public PesertaKelas() {
    }

    public PesertaKelas(String nrpMahasiswa, String namaMahasiswa) {
        this.nrpMahasiswa = nrpMahasiswa;
        this.namaMahasiswa = namaMahasiswa;
    }

    public PesertaKelas(String nrpMahasiswa, String namaMahasiswa, String statusKehadiran) {
        this.nrpMahasiswa = nrpMahasiswa;
        this.namaMahasiswa = namaMahasiswa;
        this.statusKehadiran = statusKehadiran;
    }

    public String getNrpMahasiswa() {
        return nrpMahasiswa;
    }

    public void setNrpMahasiswa(String nrpMahasiswa) {
        this.nrpMahasiswa = nrpMahasiswa;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public String getStatusKehadiran() {
        return statusKehadiran;
    }

    public void setStatusKehadiran(String statusKehadiran) {
        this.statusKehadiran = statusKehadiran;
    }
}
