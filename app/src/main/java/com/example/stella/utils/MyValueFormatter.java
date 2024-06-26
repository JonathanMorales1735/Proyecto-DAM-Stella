package com.example.stella.utils;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;

/**
 * MyValueFormatter formatea valores numericos en porcentajes
 */

public class MyValueFormatter extends PercentFormatter {
    DecimalFormat mFormat;
    PieChart mPieChart;

    public MyValueFormatter(DecimalFormat format, PieChart pieChart){
        mFormat = format;
        mPieChart = pieChart;
    }


    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + "%";
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        if (mPieChart != null && mPieChart.isUsePercentValuesEnabled()) {
            return getFormattedValue(value);
        } else {
            return mFormat.format(value);
        }
    }
}
