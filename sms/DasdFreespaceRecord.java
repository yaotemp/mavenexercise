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

    // Getters and setters for each field
    public String getVolumeSerial() { return volumeSerial; }
    public void setVolumeSerial(String volumeSerial) { this.volumeSerial = volumeSerial; }

    public String getMountAttr() { return mountAttr; }
    public void setMountAttr(String mountAttr) { this.mountAttr = mountAttr; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public int getFreeExts() { return freeExts; }
    public void setFreeExts(int freeExts) { this.freeExts = freeExts; }

    public int getFreeTracks() { return freeTracks; }
    public void setFreeTracks(int freeTracks) { this.freeTracks = freeTracks; }

    public int getFreeCyls() { return freeCyls; }
    public void setFreeCyls(int freeCyls) { this.freeCyls = freeCyls; }

    public int getLrgFreeTracks() { return lrgFreeTracks; }
    public void setLrgFreeTracks(int lrgFreeTracks) { this.lrgFreeTracks = lrgFreeTracks; }

    public int getLgFreeCyls() { return lgFreeCyls; }
    public void setLgFreeCyls(int lgFreeCyls) { this.lgFreeCyls = lgFreeCyls; }

    public String getDasdNmbr() { return dasdNmbr; }
    public void setDasdNmbr(String dasdNmbr) { this.dasdNmbr = dasdNmbr; }

    public String getVtoc() { return vtoc; }
    public void setVtoc(String vtoc) { this.vtoc = vtoc; }

    public String getSmsIndx() { return smsIndx; }
    public void setSmsIndx(String smsIndx) { this.smsIndx = smsIndx; }

    public String getSmsInd() { return smsInd; }
    public void setSmsInd(String smsInd) { this.smsInd = smsInd; }

    public String getDensity() { return density; }
    public void setDensity(String density) { this.density = density; }

    public String getStorageGroup() { return storageGroup; }
    public void setStorageGroup(String storageGroup) { this.storageGroup = storageGroup; }

    public double getVolumeFreePercent() { return volumeFreePercent; }
    public void setVolumeFreePercent(double volumeFreePercent) { this.volumeFreePercent = volumeFreePercent; }

    public double getDscbFreePercent() { return dscbFreePercent; }
    public void setDscbFreePercent(double dscbFreePercent) { this.dscbFreePercent = dscbFreePercent; }

    public double getVirsFreePercent() { return virsFreePercent; }
    public void setVirsFreePercent(double virsFreePercent) { this.virsFreePercent = virsFreePercent; }

    public int getCylsOnVol() { return cylsOnVol; }
    public void setCylsOnVol(int cylsOnVol) { this.cylsOnVol = cylsOnVol; }

    public String getSmsVolStatus() { return smsVolStatus; }
    public void setSmsVolStatus(String smsVolStatus) { this.smsVolStatus = smsVolStatus; }

    public String getAllocCntr() { return allocCntr; }
    public void setAllocCntr(String allocCntr) { this.allocCntr = allocCntr; }

    public String getFcDsFlag() { return fcDsFlag; }
    public void setFcDsFlag(String fcDsFlag) { this.fcDsFlag = fcDsFlag; }

    @Override
    public String toString() {
        return "DasdFreespaceRecord{" +
                "volumeSerial='" + volumeSerial + '\'' +
                ", mountAttr='" + mountAttr + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", freeExts=" + freeExts +
                ", freeTracks=" + freeTracks +
                ", freeCyls=" + freeCyls +
                ", lrgFreeTracks=" + lrgFreeTracks +
                ", lgFreeCyls=" + lgFreeCyls +
                ", dasdNmbr='" + dasdNmbr + '\'' +
                ", vtoc='" + vtoc + '\'' +
                ", smsIndx='" + smsIndx + '\'' +
                ", smsInd='" + smsInd + '\'' +
                ", density='" + density + '\'' +
                ", storageGroup='" + storageGroup + '\'' +
                ", volumeFreePercent=" + volumeFreePercent +
                ", dscbFreePercent=" + dscbFreePercent +
                ", virsFreePercent=" + virsFreePercent +
                ", cylsOnVol=" + cylsOnVol +
                ", smsVolStatus='" + smsVolStatus + '\'' +
                ", allocCntr='" + allocCntr + '\'' +
                ", fcDsFlag='" + fcDsFlag + '\'' +
                '}';
    }
}
