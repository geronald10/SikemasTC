package id.ac.its.sikemastc.model;

public class RuangKelas {

    private String idRuangan;
    private String namaRuangan;
    private String longitude;
    private String latitude;
    private String altitude;

    public RuangKelas(String idRuangan, String namaRuangan, String longitude, String latitude, String altitude) {
        this.idRuangan = idRuangan;
        this.namaRuangan = namaRuangan;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public RuangKelas(String idRuangan, String namaRuangan) {
        this.idRuangan = idRuangan;
        this.namaRuangan = namaRuangan;
    }

    public String getIdRuangan() {
        return idRuangan;
    }

    public void setIdRuangan(String idRuangan) {
        this.idRuangan = idRuangan;
    }

    public String getNamaRuangan() {
        return namaRuangan;
    }

    public void setNamaRuangan(String namaRuangan) {
        this.namaRuangan = namaRuangan;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }
}
