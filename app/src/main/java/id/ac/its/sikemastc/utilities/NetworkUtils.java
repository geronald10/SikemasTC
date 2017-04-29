package id.ac.its.sikemastc.utilities;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    // Login API
    public static final String LOGIN_SIKEMAS =
            "http://10.151.31.201/sikemas/public/loginmobile";
    // Upload Dataset Wajah API
    public static final String UPLOAD_DATASET_WAJAH_SIKEMAS =
            "http://10.151.31.201/sikemas/public/mhs/uploadfoto";
    // List Kelas Diampu oleh Dosen API
    public static final String LIST_KELAS_DOSEN_SIKEMAS =
            "http://10.151.31.201/sikemas/public/kelasdiampu";
    // List Jadwal Kelas Hari ini
    public static final String LIST_JADWAL_KELAS_DOSEN_HARI_INI =
            "http://10.151.31.201/sikemas/public/kelashariini";
}
