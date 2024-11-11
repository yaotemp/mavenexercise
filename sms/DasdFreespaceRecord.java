package sms;

public class DasdFreespaceRecord {
    private String volumeSerial;
    private String mountAttr;
    private String deviceType;
    private int freeExts;
    private int freeTracks;
    private int freeCyls;
    private int lrgFreeTracks;
    private int lgFreeCyls;
    private String dasdNmbr;
    private String vtoc;
    private String smsIndx;
    private String smsInd;
    private String density;
    private String storageGroup;
    private double volumeFreePercent;
    private double dscbFreePercent;
    private double virsFreePercent;
    private int cylsOnVol;
    private String smsVolStatus;
    private String allocCntr;
    private String fcDsFlag;

    // Constructors, getters, and setters omitted for brevity
    @Override
    public String toString() {
        return String.format(
            "Volume Serial: %s, Mount Attr: %s, Device Type: %s, Free Exts: %d, Free Tracks: %d, Free Cyls: %d, " +
            "Lrg Free Tracks: %d, LgFree Cyls: %d, DASD Nmbr: %s, VTOC: %s, SMS Indx: %s, SMS Ind: %s, Density: %s, " +
            "Storage Group: %s, Volume Free %%: %.1f, DSCB Free %%: %.1f, VIRS Free %%: %.1f, Cyls on Vol: %d, " +
            "SMS Vol Status: %s, Alloc Cntr: %s, FCDS Flag: %s",
            volumeSerial, mountAttr, deviceType, freeExts, freeTracks, freeCyls, lrgFreeTracks, lgFreeCyls,
            dasdNmbr, vtoc, smsIndx, smsInd, density, storageGroup, volumeFreePercent, dscbFreePercent,
            virsFreePercent, cylsOnVol, smsVolStatus, allocCntr, fcDsFlag
        );
    }
}

