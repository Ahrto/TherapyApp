package com.alberto.medaap2.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alberto.medaap2.R;
import com.alberto.medaap2.models.Medicamento;

import java.util.ArrayList;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.ViewHolder>
        implements View.OnClickListener {


    private int resurce;
    private ArrayList<Medicamento> medicamentoArrayList;

    //    OnClick
    private View.OnClickListener listener;

    public MedicamentoAdapter(int resurce, ArrayList<Medicamento> medicamentoArrayList) {
        this.resurce = resurce;
        this.medicamentoArrayList = medicamentoArrayList;
    }

    @NonNull
    @Override
    public MedicamentoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resurce, viewGroup, false);

        view.setOnClickListener(this);

        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoAdapter.ViewHolder viewHolder, int index) {
        Medicamento medicamento = medicamentoArrayList.get(index);
        viewHolder.tvMedicamento.setText(medicamento.getNombre());
        int idAlarma = medicamento.getIdAlarma();
        if (idAlarma == 0) {
            viewHolder.tvIdAlarma.setText(R.string.tratamientoFinalizado);
            viewHolder.tvIdAlarma.setTextColor(Color.parseColor("#ec5858"));
        } else {
            viewHolder.tvIdAlarma.setText(R.string.tratamientoEnSeguimiento);
            viewHolder.tvIdAlarma.setTextColor(Color.parseColor("#59886b"));
        }
    }

    @Override
    public int getItemCount() {
        return medicamentoArrayList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMedicamento;
        private TextView tvIdAlarma;
        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.tvMedicamento = (TextView) view.findViewById(R.id.tvMedicamento);
            this.tvIdAlarma = (TextView) view.findViewById(R.id.tvIdAlarma);
        }
    }
}
