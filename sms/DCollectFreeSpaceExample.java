package sms;

import com.ibm.jzos.AccessMethodServices;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DCollectFreeSpaceExample {
    public static void main(String[] args) {
        try {
            // 创建 AccessMethodServices 实例
            AccessMethodServices ams = new AccessMethodServices();

            // 添加 DCOLLECT 命令，收集指定卷的信息
            // 将 DCOLLECT 的输出保存到指定的数据集
            ams.addInputLine("DCOLLECT OFILE(USER.DCOLLECT.OUTPUT) VOLUME(VOL001)");

            // 执行 DCOLLECT 命令并获取返回代码
            int returnCode = ams.execute();

            // 检查返回代码
            if (returnCode == 0) {
                System.out.println("DCOLLECT command executed successfully.");
            } else {
                System.out.println("DCOLLECT command failed with return code: " + returnCode);
            }

            // 获取并解析输出结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(ams.getOutputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // 示例：解析并输出包含 VOLSER 和 FREE SPACE 的行
                if (line.contains("VOL=") && line.contains("FREE=")) {
                    System.out.println("Volume and Free Space Info: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

