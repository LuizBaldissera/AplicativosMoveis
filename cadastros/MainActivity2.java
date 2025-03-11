package com.example.cadastros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

class ListarAlunos extends AppCompatActivity {
    private ListView listView;
    private AlunoDAO dao;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = findViewById(R.id.lista_alunos);
        dao = new AlunoDAO(this);
        alunos = dao.obterTodos();
        alunosFiltrados.addAll(alunos);

        ArrayAdapter<Aluno> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alunos);
        listView.setAdapter(adaptador);
    }

    public void voltar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}