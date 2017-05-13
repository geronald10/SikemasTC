package id.ac.its.sikemastc.model;

public class PesertaPerkuliahan {

    private String idPerkuliahan;
    private String idMahasiswa;
    private String nrpMahasiswa;
    private String namaMahasiswa;
    private String statusKehadiran;

    public PesertaPerkuliahan(String idPerkuliahan, String idMahasiswa, String nrpMahasiswa, String namaMahasiswa, String statusKehadiran) {
        this.idPerkuliahan = idPerkuliahan;
        this.idMahasiswa = idMahasiswa;
        this.nrpMahasiswa = nrpMahasiswa;
        this.namaMahasiswa = namaMahasiswa;
        this.statusKehadiran = statusKehadiran;
    }

    public String getIdPerkuliahan() {
        return idPerkuliahan;
    }

    public void setIdPerkuliahan(String idPerkuliahan) {
        this.idPerkuliahan = idPerkuliahan;
    }

    public String getIdMahasiswa() {
        return idMahasiswa;
    }

    public void setIdMahasiswa(String idMahasiswa) {
        this.idMahasiswa = idMahasiswa;
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
