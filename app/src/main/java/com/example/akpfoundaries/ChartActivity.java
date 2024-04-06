package com.example.akpfoundaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class ChartActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    LineChart lineChart ;
//    Button btn;
    LineDataSet dataSet;
    LineData data;
    SharedPreferences sharedPreferences;
    TextView tempText;

    TextView countText;
    TextView timeTextView;
    TextView batchCountTextView;
    TextView totalCountTextView;

    int lastPoint=0;

    private final String chartColor = "CHART_COLOR";
    private final String chartAnimation = "CHART_ANIMATION";
    int x = 1,y = 0;
    boolean start = true;

    Random random = new Random();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://akp-foundaries-default-rtdb.firebaseio.com/");

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        sharedPreferences = getSharedPreferences("AKPSharedPreferenceFile",MODE_PRIVATE);

        //Initialization
        lineChart = findViewById(R.id.lineChart);
        tempText=findViewById(R.id.currentTemp);
        countText=findViewById(R.id.currentBatch);
        batchCountTextView=findViewById(R.id.batchCount);
        totalCountTextView=findViewById(R.id.totalCount);
        timeTextView=findViewById(R.id.timeView);
//        btn = findViewById(R.id.add_btn);
        BottomNavigationView BottomNavigation = findViewById(R.id.bottom_navigation);
        BottomNavigation.setOnItemSelectedListener(this);
        BottomNavigation.setSelectedItemId(R.id.bottom_chart);

        //LineChart
        lineChart.setPinchZoom(true);
        lineChart.getDescription().setEnabled(false);


        //Initializing empty dataset and data
        dataSet = new LineDataSet(new ArrayList<Entry>(),"Temperature data");

        //LimitLine bottom
        LimitLine limitLine = new LimitLine(1350);
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(2f);
        limitLine.setLabel("Bottom Limit");
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);

        //LimitLine top
        LimitLine limitLine1=new LimitLine(1420);
        limitLine1.setLineColor(Color.RED);
        limitLine1.setLineWidth(2f);
        limitLine1.setLabel("Top Limit");
        limitLine1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);


        //LineData
        data=new LineData(dataSet);

        //X-Axis
        XAxis xaxis = lineChart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setAxisLineWidth(2f);

        //Y-Axis right
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        //Y-Axis left
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.addLimitLine(limitLine);
        yAxisLeft.addLimitLine(limitLine1);



        getInitialDataAndAddLIstener();


        Toast.makeText(this, sharedPreferences.getString(chartColor,""), Toast.LENGTH_SHORT).show();



    }



    private void getInitialDataAndAddLIstener(){
        databaseReference.child(getDate()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> firebaseData = new ArrayList<>();
                ArrayList<DataValuesModelClass> counterVar=new ArrayList<>();
                int batchCount=0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Temperature temperature=dataSnapshot.getValue(Temperature.class);
//                    firebaseData.add(new Entry(temperature.getTemperature(),temperature.getTime()));
                    DataValuesModelClass dataValuesModelClass = dataSnapshot.getValue(DataValuesModelClass.class);
                    counterVar.add(dataValuesModelClass);
                    firebaseData.add(new Entry(dataValuesModelClass.getX(),dataValuesModelClass.getY()));
                    batchCount=dataValuesModelClass.getBatch();
                    timeTextView.setText(dataSnapshot.getKey()+"");
                }
                Toast.makeText(ChartActivity.this, ""+counterVar.size(), Toast.LENGTH_SHORT).show();
                countText.setText(batchCount+"");
                batchCountTextView.setText(batchCount+"."+getCurrentBatchCount(counterVar,batchCount));
                totalCountTextView.setText(getTotalValidCount(counterVar)+"");
                Toast.makeText(ChartActivity.this, "data read sucessfully", Toast.LENGTH_SHORT).show();
                tempText.setText(firebaseData.get(firebaseData.size()-1).getY()+" ");
                showChart(firebaseData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lineChart.clear();
                lineChart.invalidate();
                Toast.makeText(ChartActivity.this, "Unable to read data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAndSaveChartIMagePreview(){
        Bitmap bitmap = lineChart.getChartBitmap();
        AlertDialog.Builder builder = new AlertDialog.Builder(ChartActivity.this);
        builder.setTitle("Chart Image");

        final View cutomLayout = getLayoutInflater().inflate(R.layout.alert_dialog,null);

        builder.setView(cutomLayout);
        builder.setPositiveButton("Save", ((dialogInterface, i) -> {
            MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"CImage","");
            Toast.makeText(ChartActivity.this, "Image Saved To Gallery", Toast.LENGTH_SHORT).show();
        }));
        builder.setNegativeButton("cancel",((dialogInterface, i) -> {
            Toast.makeText(ChartActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
        }));
        builder.setCancelable(true);
        builder.setIcon(R.drawable.akp_foundaries_logo);
        builder.setMessage("Save Chart Image To Library");
        ImageView imageView = cutomLayout.findViewById(R.id.alert_dialog_imageView);
        imageView.setImageBitmap(bitmap);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        switch (item.getItemId()){
            case R.id.joyfull_color:
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                edit.putString(chartColor,"JOYFUL_COLORS");
                edit.commit();
                lineChart.invalidate();
                return true;
            case R.id.colorful_color:
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                edit.putString(chartColor,"COLORFUL_COLORS");
                edit.commit();
                lineChart.invalidate();
                return true;
            case R.id.vordiplom_color:
                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                edit.putString(chartColor,"VORDIPLOM_COLORS");
                edit.commit();
                lineChart.invalidate();
                return true;
            case R.id.Material_color:
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                edit.putString(chartColor,"MATERIAL_COLORS");
                edit.commit();
                lineChart.invalidate();
                return true;
            case R.id.save_to_gallery_menu:
                showAndSaveChartIMagePreview();
                return true;
            case R.id.animateX_menu:
                lineChart.animateX(2000);
                edit.putString(chartAnimation,"animateX");
                edit.commit();
                Toast.makeText(this, "animateX", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.animateY_menu:
                lineChart.animateY(2000);
                edit.putString(chartAnimation,"animateY");
                edit.commit();
                Toast.makeText(this, "animateY", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.animateXY_menu:
                lineChart.animateXY(1600,1800);
                edit.putString(chartAnimation,"animateXY");
                edit.commit();
                Toast.makeText(this, "animateXY", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_settings,menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bottom_home:
                startActivity(new Intent(ChartActivity.this,HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                return true;
            case R.id.bottom_chart:
                return true;
            case R.id.bottom_profile:
                startActivity(new Intent(ChartActivity.this,ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                return true;
        }
        return false;
    }

    private void showChart(ArrayList<Entry> firebaseData){
        dataSet.clear();
        dataSet.setValues(firebaseData);
        dataSet.notifyDataSetChanged();
        data.notifyDataChanged();
        lineChart.clear();
        lineChart.setData(data);
        lineChart.setVisibleXRangeMaximum(10);
        lineChart.setDrawGridBackground(false);
        lineChart.moveViewToX(57121);
        setDataSetColor();
        if(start){
            animateChart();
            start = false;
        }
        lineChart.invalidate();
    }

    private void setDataSetColor() {
        String color = sharedPreferences.getString(chartColor,"");
        if(color.length() != 0){
            if(color.equals("JOYFUL_COLORS")){
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            } else if (color.equals("MATERIAL_COLORS")) {
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            } else if (color.equals("VORDIPLOM_COLORS")) {
                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            }else if(color.equals("COLORFUL_COLORS")){
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            }
            else{
            }
        }
    }

    private void animateChart() {
        String animation = sharedPreferences.getString(chartAnimation,"");
        Toast.makeText(this, animation, Toast.LENGTH_SHORT).show();
        if(animation.length() != 0){
            switch (animation){
                case "animateX":
                    lineChart.animateX(2000);
                    break;
                case "animateY":
                    lineChart.animateY(2000);
                    break;
                case "animateXY":
                    lineChart.animateXY(1600,1800, Easing.Linear);
                    break;
                default:
                    lineChart.animateXY(1600,1800, Easing.Linear);
            }
        }
    }

    public String getDate(){
        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return currentDateTime.format(formatter);
        }
        return "";
    }

    public int getCurrentBatchCount(ArrayList<DataValuesModelClass> data,int batch){
        int count=0;
        for(int i=0;i<data.size();i++){
            if(data.get(i).getBatch()==batch && (data.get(i).getY()>=1350 && data.get(i).getY()<=1420)){
                count++;
            }
        }
        return count;
    }

    public int getTotalValidCount(ArrayList<DataValuesModelClass> data){
        int cnt=0;
        for(int i=0;i<data.size();i++){
            if(data.get(i).getY()>=1350 && data.get(i).getY()<=1420){
                cnt++;
            }
        }
        return cnt;
    }


}