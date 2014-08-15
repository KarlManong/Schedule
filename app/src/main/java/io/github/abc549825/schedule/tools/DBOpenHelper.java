package io.github.abc549825.schedule.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import io.github.abc549825.schedule.R;
import io.github.abc549825.schedule.beans.ConfigDetail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * 这是SQLite的open类
 * Created by Young on 2014/8/13.
 */
public class DBOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "schedule.db";

    private ArrayList<Class<?>> beanClasses;

    private Context context;

    private HashMap<String, RuntimeExceptionDao<?, ?>> daoMap = new HashMap<String, RuntimeExceptionDao<?, ?>>();

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(this.getClass().getName(), "onCreate");
            createTables(connectionSource);
        } catch (SQLException e) {
            Log.e(this.getClass().getName(), "Can't create database", e);
        }

        RuntimeExceptionDao dao = getConfigDetailDao();

        dao.create(new ConfigDetail());

        dao.queryForAll();

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(this.getClass().getName(), "onUpgrade");
            dropTables();

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(this.getClass().getName(), "Can't create database", e);
        }

    }

    @Override
    public void close() {
        super.close();
        beanClasses = null;
    }


    /**
     * 动态加载所有beans
     *
     * @return 所有beans的Class
     */
    private List<Class<?>> getBeans() {
        if (beanClasses != null) {
            return beanClasses;
        }

        beanClasses = new ArrayList<Class<?>>();

        String packageName = "io.github.abc549825.schedule.beans";
        PathClassLoader loader = (PathClassLoader) Thread.currentThread().getContextClassLoader();

        try {
            DexFile dex = new DexFile(this.context.getPackageResourcePath());
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entry = entries.nextElement();
                if (entry.startsWith(packageName)) {
                    Class<?> clazz = dex.loadClass(entry, loader);
                    if (clazz != null && clazz.getAnnotation(DatabaseTable.class) != null) {
                        beanClasses.add(clazz);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return beanClasses;
    }

    private void createTables(ConnectionSource connectionSource) throws SQLException {
        for (Class<?> clazz : getBeans()) {
            TableUtils.createTable(connectionSource, clazz);
        }

    }

    private void dropTables() throws SQLException {
        TableUtils.dropTable(connectionSource, ConfigDetail.class, true);
    }

    public RuntimeExceptionDao<ConfigDetail, Integer> getConfigDetailDao() {
        RuntimeExceptionDao<ConfigDetail, Integer> dao = (RuntimeExceptionDao<ConfigDetail, Integer>) daoMap.get(ConfigDetail.class.getName());
        if (dao == null) {
            dao = getRuntimeExceptionDao(ConfigDetail.class);
            daoMap.put(ConfigDetail.class.getName(), dao);
        }
        return dao;
    }


}
