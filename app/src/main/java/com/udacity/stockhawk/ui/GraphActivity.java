package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    LineChart chart;
    String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        symbol=getIntent().getStringExtra("symbol");
        List<Entry> yvalues=new ArrayList<>();
        List<String> xvals=new ArrayList<>();
        chart= (LineChart) findViewById(R.id.graph);
        Toast.makeText(this, ""+symbol, Toast.LENGTH_SHORT).show();
        String[] projection={Contract.Quote.COLUMN_HISTORY};
        Cursor c=getContentResolver().query(Contract.Quote.makeUriForStock(symbol),projection,null,null,null);
        String history="";
        while (c!=null && c.moveToNext())
        {
            history=c.getString(c.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

        }
        c.close();
        String[] val=history.split("\\n");
        List<String>a= Arrays.asList(val);
        int j=0;
        for(String x : a)
        {

            String[] f=x.split(",");
            Entry i=new Entry(Float.parseFloat(f[1].substring(1)),j);
            j++;
            yvalues.add(i);
            xvals.add(f[0]);

        }





        LineDataSet dataSet=new LineDataSet(yvalues,"point");
        LineData data=new LineData(xvals,dataSet);
        chart.setData(data);
        chart.invalidate();

    }
}
