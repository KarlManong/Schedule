package io.github.abc549825.schedule.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Young on 2014/8/13.
 */

@DatabaseTable(tableName = "TB_CONFIG")
public class ConfigDetail {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private boolean network_state;

    @DatabaseField(canBeNull = false)
    private boolean wifi_state;

    @DatabaseField(canBeNull = false)
    private boolean sound_state;

    @DatabaseField(canBeNull = false)
    private boolean shock_state;

    @DatabaseField
    private Date start_time;

    @DatabaseField
    private Date end_time;

    public ConfigDetail() {
        this.network_state = false;
        this.wifi_state = false;
        this.sound_state = false;
        this.shock_state = false;
        this.start_time = new Date();
        this.end_time = new Date();
    }

    @Override
    public String toString() {
        return String.format("id=%s, start=%s, end=%s", id, start_time, end_time);
    }
}
