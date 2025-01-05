import java.time.LocalDate;

class TrackRecord {
    private LocalDate date;
    private int currentAvailableChunks; 
    private int next1Day;
    private int next7Days;
    private int next30Days;
    private int next60Days;
    private int next90Days;
    private int next180Days;

    // Constructor
    public TrackRecord(LocalDate date, int currentAvailableChunks, int next1Day, int next7Days, 
                       int next30Days, int next60Days, int next90Days, int next180Days) {
        this.date = date;
        this.currentAvailableChunks = currentAvailableChunks;
        this.next1Day = next1Day;
        this.next7Days = next7Days;
        this.next30Days = next30Days;
        this.next60Days = next60Days;
        this.next90Days = next90Days;
        this.next180Days = next180Days;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
        return date + "," + currentAvailableChunks + "," + next1Day + "," + next7Days + "," + 
               next30Days + "," + next60Days + "," + next90Days + "," + next180Days;
    }
}
