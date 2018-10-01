package live.noxbox;

public interface Configuration {
    int REQUESTING_AND_ACCEPTING_TIMEOUT_IN_SECONDS = 60;
    int REQUESTING_AND_ACCEPTING_TIMEOUT_IN_MILLIS = 60 * 1000;
    String START_TIME = "00:00:00";
    int ACCEPTANCE_TIMEOUT_IN_SECONDS = 30;
    double RADIUS_IN_METERS = 50 * 1000;
    int MIN_RATE_IN_PERCENTAGE = 90;
    String CURRENCY = "Waves";
    int LOCATION_PERMISSION_REQUEST_CODE = 911;
    long MINIMUM_PAYMENT_TIME_MILLIS = 15 * 60 * 1000;
    float MINIMUM_FACE_SIZE = 0.6F;
    float MINIMUM_PROBABILITY_FOR_ACCEPTANCE = 0.6F;

}
