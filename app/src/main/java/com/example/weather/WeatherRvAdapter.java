package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRvAdapter extends RecyclerView.Adapter<WeatherRvAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WatherRvModel> watherRvModelArrayList;

    public WeatherRvAdapter(Context context, ArrayList<WatherRvModel> watherRvModelArrayList) {
        this.context = context;
        this.watherRvModelArrayList = watherRvModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WatherRvModel model=watherRvModelArrayList.get(position);
        holder.TemperatureTv.setText(model.getTemperature()+"°c");
        holder.windTV.setText(model.getWindSpeed()+"Km/h");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.conditionIV);
        SimpleDateFormat input=new SimpleDateFormat("YYYY-MM-dd hh:mm");
        SimpleDateFormat ouput=new SimpleDateFormat("hh:mm aa");
        try{
            Date t=input.parse(model.getTime());
            holder.TimeTv.setText(ouput.format(t));
        }catch (ParseException r){
            r.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return watherRvModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windTV,TimeTv,TemperatureTv;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            windTV=itemView.findViewById(R.id.idTVWindspeed);
            TimeTv=itemView.findViewById(R.id.idTvTime);
            TemperatureTv=itemView.findViewById(R.id.idTvTemperature);
            conditionIV=itemView.findViewById(R.id.idIvCondition);
        }
    }
}
