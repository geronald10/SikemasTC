package id.ac.its.sikemastc.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class SikemasContract {

    public static final String CONTENT_AUTHORITY = "id.ac.its.sikemastc";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USER = "user";

    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER)
                .build();

        public static final String TABLE_NAME = "user";
        public static final String KEY_USER_NAME = "user_nama";
        public static final String KEY_USER_EMAIL = "user_email";
        public static final String KEY_USER_ID = "user_id";
        public static final String KEY_USER_ROLE = "user_role";
        public static final String KEY_USER_PASSWORD = "user_password";
    }
}
