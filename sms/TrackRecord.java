import java.time.LocalDate;
import java.time.LocalTime;

class TrackRecord {
    private String volSer; // Volume Serial
    private LocalDate date;
    private LocalTime time;
    private double freePercentage;
    private int freeCylinder;
    private int cylinderThreshold;
    private int matchedVolumes;
    private int volumesBelowThreshold;
    private int currentAvailableChunks; // Moved as the second field
    private int next1Day;
    private int next7Days;
    private int next30Days;
    private int next60Days;
    private int next90Days;
    private int next180Days;

    // Constructor
    public TrackRecord(String volSer, LocalDate date, LocalTime time, double freePercentage, 
                       int freeCylinder, int cylinderThreshold, int matchedVolumes, 
                       int volumesBelowThreshold, int currentAvailableChunks, int next1Day, 
                       int next7Days, int next30Days, int next60Days, int next90Days, int next180Days) {
        this.volSer = volSer;
        this.date = date;
        this.time = time;
        this.freePercentage = freePercentage;
        this.freeCylinder = freeCylinder;
        this.cylinderThreshold = cylinderThreshold;
        this.matchedVolumes = matchedVolumes;
        this.volumesBelowThreshold = volumesBelowThreshold;
        this.currentAvailableChunks = currentAvailableChunks;
        this.next1Day = next1Day;
        this.next7Days = next7Days;
        this.next30Days = next30Days;
        this.next60Days = next60Days;
        this.next90Days = next90Days;
        this.next180Days = next180Days;
    }

    // Getters and Setters
    public String getVolSer() {
        return volSer;
    }

    public void setVolSer(String volSer) {
        this.volSer = volSer;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public double getFreePercentage() {
        return freePercentage;
    }

    public void setFreePercentage(double freePercentage) {
        this.freePercentage = freePercentage;
    }

    public int getFreeCylinder() {
        return freeCylinder;
    }

    public void setFreeCylinder(int freeCylinder) {
        this.freeCylinder = freeCylinder;
    }

    public int getCylinderThreshold() {
        return cylinderThreshold;
    }

    public void setCylinderThreshold(int cylinderThreshold) {
        this.cylinderThreshold = cylinderThreshold;
    }

    public int getMatchedVolumes() {
        return matchedVolumes;
    }

    public void setMatchedVolumes(int matchedVolumes) {
        this.matchedVolumes = matchedVolumes;
    }

    public int getVolumesBelowThreshold() {
        return volumesBelowThreshold;
    }

    public void setVolumesBelowThreshold(int volumesBelowThreshold) {
        this.volumesBelowThreshold = volumesBelowThreshold;
    }

    public int getCurrentAvailableChunks() {
        return currentAvailableChunks;
    }

    public void setCurrentAvailableChunks(int currentAvailableChunks) {
        this.currentAvailableChunks = currentAvailableChunks;
    }

    public int getNext1Day() {
        return next1Day;
    }

    public void setNext1Day(int next1Day) {
        this.next1Day = next1Day;
    }

    public int getNext7Days() {
        return next7Days;
    }

    public void setNext7Days(int next7Days) {
        this.next7Days = next7Days;
    }

    public int getNext30Days() {
        return next30Days;
    }

    public void setNext30Days(int next30Days) {
        this.next30Days = next30Days;
    }

    public int getNext60Days() {
        return next60Days;
    }

    public void setNext60Days(int next60Days) {
        this.next60Days = next60Days;
    }

    public int getNext90Days() {
        return next90Days;
    }

    public void setNext90Days(int next90Days) {
        this.next90Days = next90Days;
    }

    public int getNext180Days() {
        return next180Days;
    }

    public void setNext180Days(int next180Days) {
        this.next180Days = next180Days;
    }

    @Override
    public String toString() {
        return volSer + "," + date + "," + time + "," + freePercentage + "," +
               freeCylinder + "," + cylinderThreshold + "," + matchedVolumes + "," +
               volumesBelowThreshold + "," + currentAvailableChunks + "," +
               next1Day + "," + next7Days + "," + next30Days + "," +
               next60Days + "," + next90Days + "," + next180Days;
    }
}
