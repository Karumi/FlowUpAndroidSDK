/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.reporter.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;
import java.util.LinkedList;
import java.util.List;

@AutoValue abstract class SQLDelightMetric implements MetricModel {

  private static final MetricModel.Factory<SQLDelightMetric> FACTORY =
      new MetricModel.Factory<>(new MetricModel.Creator<SQLDelightMetric>() {

        @Override public SQLDelightMetric create(long id, long reportId, @NonNull String metricName,
            @Nullable Double value, @Nullable Double mean, @Nullable Double p10,
            @Nullable Double p90) {
          return new AutoValue_SQLDelightMetric(id, reportId, metricName, value, mean, p10, p90);
        }
      });

  private static final RowMapper<SQLDelightMetric> METRIC_MAPPER =
      FACTORY.get_metrics_by_report_timestampMapper();

  static void createMetric(SQLiteDatabase db, long reportId, String metricName, Double value,
      Double mean, Double p10, Double p90) {
    Create_metric createMetric = new Create_metric(db);
    createMetric.bind(reportId, metricName, value, mean, p10, p90);
    createMetric.program.executeInsert();
  }

  static List<SQLDelightMetric> getMetricsByReportId(SQLiteDatabase db, long reportId) {
    List<SQLDelightMetric> metrics = new LinkedList<>();
    Cursor cursor =
        db.rawQuery(MetricModel.GET_METRICS_BY_REPORT_ID, new String[] { reportId + "" });
    while (cursor.moveToNext()) {
      SQLDelightMetric metric = SQLDelightMetric.METRIC_MAPPER.map(cursor);
      metrics.add(metric);
    }
    cursor.close();
    return metrics;
  }

  static void removeAll(SQLiteDatabase db) {
    db.execSQL(MetricModel.REMOVE_ALL);
  }
}
