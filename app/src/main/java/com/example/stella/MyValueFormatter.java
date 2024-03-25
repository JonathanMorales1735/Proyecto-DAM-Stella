package com.example.stella;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.DecimalFormat;

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
            // Converted to percent
            return getFormattedValue(value);
        } else {
            // raw value, skip percent sign
            return mFormat.format(value);
        }
    }
}
