import java.io.PrintWriter;
import com.ibm.jzos.CatalogSearch;
import com.ibm.jzos.CatalogSearchField;
import com.ibm.jzos.Format1DSCB;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZFile;

public class VolumeDatasetInfo {
    public static void main(String[] args) {
        String volser = "YOURVOLSER";  // 你已知的卷序列号
        PrintWriter writer = new PrintWriter(System.out);
        
        try {
            // 使用 CatalogSearch 过滤指定 VOLSER 的数据集
            CatalogSearch catSearch = new CatalogSearch("*", 64000);
            catSearch.addFieldName("ENTNAME");
            catSearch.addFieldName("VOLSER");
            catSearch.search();

            int datasetCount = 0;

            while (catSearch.hasNext()) {
                CatalogSearch.Entry entry = (CatalogSearch.Entry) catSearch.next();
                if (entry.isDatasetEntry()) {
                    String entryVolser = entry.getField("VOLSER").getFString().trim();

                    // 检查数据集是否在指定 VOLSER 上
                    if (volser.equals(entryVolser)) {
                        datasetCount++;
                        String dsn = entry.getField("ENTNAME").getFString().trim();
                        String qdsn = "'" + dsn + "'";  // 完全限定的 DSN 格式

                        // 获取 Format 1 DSCB 信息
                        try {
                            Format1DSCB dscb = ZFile.obtainDSN(qdsn, volser);
                            writer.println("Dataset: " + dsn + " on VOLSER: " + volser);
                            writer.println("LRECL=" + dscb.getDS1LRECL());
                            writer.println("BLKSIZE=" + dscb.getDS1BLKL());
                            writer.println("RECFM=" + dscb.getDS1RECFM());
                            writer.println("Allocated Space in KB=" + dscb.getAllocatedSpaceK());
                        } catch (RcException rce) {
                            if (rce.getRc() == 4) {
                                writer.println("Volume not mounted or DSN not found on specified volume.");
                            } else if (rce.getRc() == 8) {
                                writer.println("Volume does not contain a Format 1 DSCB for this dataset.");
                            } else {
                                rce.printStackTrace();
                            }
                        }
                    }
                }
            }

            writer.println("Total datasets on VOLSER " + volser + ": " + datasetCount);
            writer.flush();

        } catch (RcException e) {
            e.printStackTrace();
        }
    }
}
