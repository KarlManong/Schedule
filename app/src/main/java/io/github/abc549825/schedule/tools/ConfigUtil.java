package io.github.abc549825.schedule.tools;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import io.github.abc549825.schedule.beans.ConfigDetail;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 生成ormlite_config.txt文件
 * Created by Young on 2014/8/13.
 */
public class ConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) {
        try {
            ArrayList<Class> list = new ArrayList<Class>();
            list.add(ConfigDetail.class);

            // new File(".").getCanonicalPath() 获取project的path
            File file = new File(new File(".").getCanonicalPath(), "app/src/main/res/raw/ormlite_config.txt");
            writeConfigFile(file, list.toArray(new Class[list.size()]));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
