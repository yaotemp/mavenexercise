import java.sql.*;
import java.math.BigDecimal;

public class TaxReturnStats {

    private BigDecimal retUnqKey;
    private String testId;
    private String taxReturnType;
    private String classificationCd;
    private BigDecimal docLength;
    private short docSegmentNum;
    private int docNum;
    private BigDecimal attLength;
    private short attNum;
    private BigDecimal hdrLength;
    private BigDecimal tocLength;
    private BigDecimal napReqLength;
    private BigDecimal napRespLength;
    private BigDecimal avsRespLength;
    private int k1Num;
    private Timestamp beginStoreTs;
    private Timestamp endStoreTs;
    private BigDecimal timeToStoreClob;
    private BigDecimal napResponseTime;
    private Timestamp taxReturnCreateTs;
    private BigDecimal ceReqLength;
    private BigDecimal ceRespLength;
    private BigDecimal ceResponseTime;

    public BigDecimal getRetUnqKey() {
        return retUnqKey;
    }

    public void setRetUnqKey(BigDecimal retUnqKey) {
        this.retUnqKey = retUnqKey;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTaxReturnType() {
        return taxReturnType;
    }

    public void setTaxReturnType(String taxReturnType) {
        this.taxReturnType = taxReturnType;
    }

    public String getClassificationCd() {
        return classificationCd;
    }

    public void setClassificationCd(String classificationCd) {
        this.classificationCd = classificationCd;
    }

    public BigDecimal getDocLength() {
        return docLength;
    }

    public void setDocLength(BigDecimal docLength) {
        this.docLength = docLength;
    }

    public short getDocSegmentNum() {
        return docSegmentNum;
    }

    public void setDocSegmentNum(short docSegmentNum) {
        this.docSegmentNum = docSegmentNum;
    }

    public int getDocNum() {
        return docNum;
    }

    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    public BigDecimal getAttLength() {
        return attLength;
    }

    public void setAttLength(BigDecimal attLength) {
        this.attLength = attLength;
    }

    public short getAttNum() {
        return attNum;
    }

    public void setAttNum(short attNum) {
        this.attNum = attNum;
    }

    public BigDecimal getHdrLength() {
        return hdrLength;
    }

    public void setHdrLength(BigDecimal hdrLength) {
        this.hdrLength = hdrLength;
    }

    public BigDecimal getTocLength() {
        return tocLength;
    }

    public void setTocLength(BigDecimal tocLength) {
        this.tocLength = tocLength;
    }

    public BigDecimal getNapReqLength() {
        return napReqLength;
    }

    public void setNapReqLength(BigDecimal napReqLength) {
        this.napReqLength = napReqLength;
    }

    public BigDecimal getNapRespLength() {
        return napRespLength;
    }

    public void setNapRespLength(BigDecimal napRespLength) {
        this.napRespLength = napRespLength;
    }

    public BigDecimal getAvsRespLength() {
        return avsRespLength;
    }

    public void setAvsRespLength(BigDecimal avsRespLength) {
        this.avsRespLength = avsRespLength;
    }

    public int getK1Num() {
        return k1Num;
    }

    public void setK1Num(int k1Num) {
        this.k1Num = k1Num;
    }

    public Timestamp getBeginStoreTs() {
        return beginStoreTs;
    }

    public void setBeginStoreTs(Timestamp beginStoreTs) {
        this.beginStoreTs = beginStoreTs;
    }

    public Timestamp getEndStoreTs() {
        return endStoreTs;
    }

    public void setEndStoreTs(Timestamp endStoreTs) {
        this.endStoreTs = endStoreTs;
    }

    public BigDecimal getTimeToStoreClob() {
        return timeToStoreClob;
    }

    public void setTimeToStoreClob(BigDecimal timeToStoreClob) {
        this.timeToStoreClob = timeToStoreClob;
    }

    public BigDecimal getNapResponseTime() {
        return napResponseTime;
    }

    public void setNapResponseTime(BigDecimal napResponseTime) {
        this.napResponseTime = napResponseTime;
    }

    public Timestamp getTaxReturnCreateTs() {
        return taxReturnCreateTs;
    }

    public void setTaxReturnCreateTs(Timestamp taxReturnCreateTs) {
        this.taxReturnCreateTs = taxReturnCreateTs;
    }

    public BigDecimal getCeReqLength() {
        return ceReqLength;
    }

    public void setCeReqLength(BigDecimal ceReqLength) {
        this.ceReqLength = ceReqLength;
    }

    public BigDecimal getCeRespLength() {
        return ceRespLength;
    }

    public void setCeRespLength(BigDecimal ceRespLength) {
        this.ceRespLength = ceRespLength;
    }

    public BigDecimal getCeResponseTime() {
        return ceResponseTime;
    }

    public void setCeResponseTime(BigDecimal ceResponseTime) {
        this.ceResponseTime = ceResponseTime;
    }

    public static TaxReturnStats fromResultSet(ResultSet rs) throws SQLException {
        TaxReturnStats instance = new TaxReturnStats();
        instance.retUnqKey = rs.getBigDecimal("RET_UNQ_KEY");
        instance.testId = rs.getString("TEST_ID");
        instance.taxReturnType = rs.getString("TAX_RETURN_TYPE");
        instance.classificationCd = rs.getString("CLASSIFICATION_CD");
        instance.docLength = rs.getBigDecimal("DOC_LENGTH");
        instance.docSegmentNum = rs.getShort("DOC_SEGMENT_NUM");
        instance.docNum = rs.getInt("DOC_NUM");
        instance.attLength = rs.getBigDecimal("ATT_LENGTH");
        instance.attNum = rs.getShort("ATT_NUM");
        instance.hdrLength = rs.getBigDecimal("HDR_LENGTH");
        instance.tocLength = rs.getBigDecimal("TOC_LENGTH");
        instance.napReqLength = rs.getBigDecimal("NAP_REQ_LENGTH");
        instance.napRespLength = rs.getBigDecimal("NAP_RESP_LENGTH");
        instance.avsRespLength = rs.getBigDecimal("AVS_RESP_LENGTH");
        instance.k1Num = rs.getInt("K1_NUM");
        instance.beginStoreTs = rs.getTimestamp("BEGIN_STORE_TS");
        instance.endStoreTs = rs.getTimestamp("END_STORE_TS");
        instance.timeToStoreClob = rs.getBigDecimal("TIME_TO_STORE_CLOB");
        instance.napResponseTime = rs.getBigDecimal("NAP_RESPONSE_TIME");
        instance.taxReturnCreateTs = rs.getTimestamp("TAX_RETURN_CREATE_TS");
        instance.ceReqLength = rs.getBigDecimal("CE_REQ_LENGTH");
        instance.ceRespLength = rs.getBigDecimal("CE_RESP_LENGTH");
        instance.ceResponseTime = rs.getBigDecimal("CE_RESPONSE_TIME");
        return instance;
    }
}
