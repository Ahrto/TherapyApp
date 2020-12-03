package com.alberto.medaap2.medicamentosApi;

import com.alberto.medaap2.models.Medicamento;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MedicamentoService {

    @GET("{cn}")
    Call<Medicamento> obtenerMedicamentoApi(@Path("cn") String cn);

}
