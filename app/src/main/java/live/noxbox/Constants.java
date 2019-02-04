package live.noxbox;

import java.math.BigDecimal;

public interface Constants {
    int REQUESTING_AND_ACCEPTING_TIMEOUT_IN_SECONDS = 60;
    long REQUESTING_AND_ACCEPTING_TIMEOUT_IN_MILLIS = 60 * 1000;
    String START_TIME = "00:00:00";
    double RADIUS_IN_METERS = 50 * 1000;
    int MIN_RATE_IN_PERCENTAGE = 90;

    int LOCATION_PERMISSION_REQUEST_CODE = 911;
    int LOCATION_PERMISSION_REQUEST_CODE_ON_PUBLISH = 912;
    int CAMERA_PERMISSION_REQUEST_CODE = 000;
    int REQUEST_IMAGE_CAPTURE = 001;

    long MINIMUM_PAYMENT_TIME_MILLIS = 15 * 60 * 1000;
    float MINIMUM_FACE_SIZE = 0.05F;
    float MINIMUM_PROBABILITY_FOR_ACCEPTANCE = 0.05F;
    String FIVE_MINUTES_PART_OF_HOUR = "12";
    int DEFAULT_BALANCE_SCALE = 10;
    BigDecimal QUARTER = new BigDecimal("0.25");
    double ADDRESS_SEARCH_RADIUS_IN_METERS = 100000D;
    int MAX_ZOOM_LEVEL = 18;
    String CHANNEL_ID = "noxbox_channel";
    int CLUSTER_RENDERING_MIN_FREQUENCY = 3000;
    int CLUSTER_RENDERING_MAX_FREQUENCY = 400;
    float COMISSION_FEE = 0.1F;

    int MINIMUM_TIME_INTERVAL_BETWEEN_GPS_ACCESS_IN_SECONDS = 3;
    int MINIMUM_CHANGE_DISTANCE_BETWEEN_RECEIVE_IN_METERS = 12;

    String DEFAULT_PRICE = "5";

    String TUTORIAL_KEY = "FIRSTRUN";
}
