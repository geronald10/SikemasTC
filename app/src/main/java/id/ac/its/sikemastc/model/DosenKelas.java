package id.ac.its.sikemastc.model;

public class DosenKelas {

    private String idDosen;
    private String nipDosen;
    private String namaDosen;
    private String kodeDosen;

    public DosenKelas() {
    }

    public DosenKelas(String idDosen, String nipDosen, String namaDosen, String kodeDosen) {
        this.idDosen = idDosen;
        this.nipDosen = nipDosen;
        this.namaDosen = namaDosen;
        this.kodeDosen = kodeDosen;
    }

    public String getIdDosen() {
        return idDosen;
    }

    public void setIdDosen(String idDosen) {
        this.idDosen = idDosen;
    }

    public String getNipDosen() {
        return nipDosen;
    }

    public void setNipDosen(String nipDosen) {
        this.nipDosen = nipDosen;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public String getKodeDosen() {
        return kodeDosen;
    }

    public void setKodeDosen(String kodeDosen) {
        this.kodeDosen = kodeDosen;
    }
}
