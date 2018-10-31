package live.noxbox.model;

import live.noxbox.R;

public enum NoxboxType {
    nanny(0, R.string.nanny, R.drawable.ic_nanny, R.drawable.illustration_nanny, R.string.nannyDescription, R.string.nannyDuration, "60"),
    photographer(1, R.string.photographer, R.drawable.ic_photographer, R.drawable.illustration_photographer, R.string.photographerDescription, R.string.photographerDuration, "60"),
    meditation(2, R.string.meditation, R.drawable.ic_meditation, R.drawable.illustration_meditation, R.string.meditationDescription, R.string.meditationDuration, "60"),
    water(3, R.string.water, R.drawable.ic_water, R.drawable.illustration_water, R.string.waterDescription, R.string.waterDuration, "60"),
    plant(4, R.string.plant, R.drawable.ic_plant, R.drawable.illustration_plant, R.string.plantDescription, R.string.plantDuration, "60");

    private int id;
    private int name;
    private int image;
    private int illustration;
    private int description;
    private int duration;
    private String minutes;

    NoxboxType(int id, int name, int image, int illustration, int description, int duration, String minutes) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.illustration = illustration;
        this.description = description;
        this.duration = duration;
        this.minutes = minutes;
    }

    public int getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getIllustration() {
        return illustration;
    }

    public int getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public String getMinutes() {
        return minutes;
    }

    public int getDescription() {
        return description;
    }

    public static NoxboxType byId(int id) {
        for (NoxboxType noxboxType : NoxboxType.values()) {
            if (noxboxType.id == id) {
                return noxboxType;
            }
        }
        return nanny;
    }
}
