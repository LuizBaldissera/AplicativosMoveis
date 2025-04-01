package com.example.cadastros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListarCepActivity extends AppCompatActivity {

    private EditText editTextCep;
    private Button buttonBuscar;
    private EditText editTextLogradouro;
    private EditText editTextComplemento;
    private EditText editTextBairro;
    private EditText editTextCidade;
    private EditText editTextEstado;
    private Button buttoSalvarEndereco;

    private ViaCEPApi viaCEPApi;
    // Nome do arquivo SharedPreferences
    private static final String PREFS_NAME = "CepPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_cep); // Seu layout

        // Inicializar elementos da UI
        editTextCep = findViewById(R.id.txtCep);
        buttonBuscar = findViewById(R.id.BuscarCEP);
        editTextLogradouro = findViewById(R.id.txtLog);
        editTextComplemento = findViewById(R.id.txtComp);
        editTextBairro = findViewById(R.id.txtBai);
        editTextCidade = findViewById(R.id.txtCid);
        editTextEstado = findViewById(R.id.txtEs);
        buttoSalvarEndereco = findViewById(R.id.SalvarCEP);

        // Configurar Retrofit
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        viaCEPApi = retrofit.create(ViaCEPApi.class);

        // Listener do botão Buscar
        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cep = editTextCep.getText().toString();
                if (cep.length() == 8) {
                    buscarEndereco(cep);
                } else {
                    Toast.makeText(ListarCepActivity.this, "CEP inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listener do botão Salvar
        buttoSalvarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarEndereco(view);
            }
        });
    }

    // Buscar o endereço pelo CEP
    private void buscarEndereco(String cep) {
        Call<ViaCEPResponse> call = viaCEPApi.getEndereco(cep);
        call.enqueue(new Callback<ViaCEPResponse>() {
            @Override
            public void onResponse(Call<ViaCEPResponse> call, Response<ViaCEPResponse> response) {
                if (response.isSuccessful()) {
                    ViaCEPResponse viaCEPResponse = response.body();
                    preencherCampos(viaCEPResponse);
                } else {
                    Toast.makeText(ListarCepActivity.this, "Erro ao buscar endereço", Toast.LENGTH_SHORT).show();
                    Log.e("ListarCepActivity", "Erro na requisição: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ViaCEPResponse> call, Throwable t) {
                Toast.makeText(ListarCepActivity.this, "Erro na requisição", Toast.LENGTH_SHORT).show();
                Log.e("ListarCepActivity", "Erro na requisição", t);
            }
        });
    }

    // Preencher os campos com os dados do endereço
    private void preencherCampos(ViaCEPResponse viaCEPResponse) {
        if (viaCEPResponse != null) {
            editTextLogradouro.setText(viaCEPResponse.getLogradouro());
            editTextComplemento.setText(viaCEPResponse.getComplemento());
            editTextBairro.setText(viaCEPResponse.getBairro());
            editTextCidade.setText(viaCEPResponse.getLocalidade());
            editTextEstado.setText(viaCEPResponse.getUf());
        }
    }

    //Salvar Endereço
    public void salvarEndereco(View view) {
        String cep = editTextCep.getText().toString();
        String logradouro = editTextLogradouro.getText().toString();
        String complemento = editTextComplemento.getText().toString();
        String bairro = editTextBairro.getText().toString();
        String cidade = editTextCidade.getText().toString();
        String estado = editTextEstado.getText().toString();

        StringBuilder enderecoCompleto = new StringBuilder();
        enderecoCompleto.append(logradouro);
        if (!complemento.isEmpty()) {
            enderecoCompleto.append(", ").append(complemento);
        }
        enderecoCompleto.append("\n").append(bairro);
        enderecoCompleto.append("\n").append(cidade).append(" - ").append(estado);
        enderecoCompleto.append("\nCEP: ").append(cep);
        // Obtém o SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Salva a string do endereço
        editor.putString("enderecoCompleto", enderecoCompleto.toString());
        editor.apply(); // Ou editor.commit();

        Toast.makeText(this, "Endereço salvo com sucesso!", Toast.LENGTH_SHORT).show();

    }
    //Função para voltar e finalizar a Activity
    public void voltar(View view){
        finish();
    }
}