package com.example.cadastros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

class MainActivity extends AppCompatActivity {
    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private AlunoDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.NomeD);
        cpf = findViewById(R.id.CpfD);
        telefone = findViewById(R.id.TelefoneD);

        dao = new AlunoDAO(this);
    }

    public void salvar(View view) {
        String nomeDigitado = nome.getText().toString().trim();
        String cpfDigitado = cpf.getText().toString().trim();
        String telefoneDigitado = telefone.getText().toString().trim();

        // Verifica se os campos estão vazios
        if (nomeDigitado.isEmpty() || cpfDigitado.isEmpty() || telefoneDigitado.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validação do CPF (verifica se o formato e os dígitos são válidos)
        if (!dao.validarCpf(cpfDigitado)) {
            Toast.makeText(this, "CPF inválido. Digite novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se o CPF já existe no banco de dados
        if (dao.cpfExistente(cpfDigitado)) {
            Toast.makeText(this, "CPF duplicado. Insira um CPF diferente.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validação do Telefone
        if (!dao.validarTelefone(telefoneDigitado)) {
            Toast.makeText(this, "Telefone inválido! Use o formato correto: (XX) XXXXX-XXXX.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Se passou por todas as validações, pode continuar com o salvamento
    }


    public void irParaListar(View view) {
        Intent intent = new Intent(this, ListarAlunos.class);
        startActivity(intent);
    }
}
