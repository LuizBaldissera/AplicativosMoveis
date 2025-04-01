package com.example.cadastros;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCEPApi {
    @GET("{cep}/json/")
    Call<ViaCEPResponse> getEndereco(@Path("cep") String cep);
}