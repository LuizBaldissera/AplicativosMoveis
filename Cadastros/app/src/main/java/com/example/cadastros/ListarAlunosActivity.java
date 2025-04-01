package com.example.cadastros;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ListarAlunosActivity extends AppCompatActivity {
    private ListView listView;
    private AlunoDAO dao;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();
    private EditText txtBuscarAluno;
    private Button btnBuscarAluno;
    private ArrayAdapter<Aluno> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alunos);

        listView = findViewById(R.id.lista_alunos);
        txtBuscarAluno = findViewById(R.id.txtBuscarCpf);
        btnBuscarAluno = findViewById(R.id.BtnBuscarCpf);

        dao = new AlunoDAO(this);

        // Log do onCreate
        Log.d("ListarAlunosActivity", "onCreate: Fetching all alunos, Thread: " + Thread.currentThread().getName());
        alunos = dao.obterTodos();
        Log.d("ListarAlunosActivity", "onCreate: Adding all alunos to alunosFiltrados, Thread: " + Thread.currentThread().getName());
        alunosFiltrados.addAll(alunos);

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alunosFiltrados);
        listView.setAdapter(adaptador);

        registerForContextMenu(listView);

        btnBuscarAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ListarAlunosActivity", "onClick: btnBuscarAluno clicked, Thread: " + Thread.currentThread().getName());
                String nomeBusca = txtBuscarAluno.getText().toString().trim();
                Log.d("ListarAlunosActivity", "onClick: Nome de busca - " + nomeBusca);

                Log.d("ListarAlunosActivity", "onClick: Before clearing, alunosFiltrados size: " + alunosFiltrados.size());
                alunosFiltrados.clear();
                Log.d("ListarAlunosActivity", "onClick: After clearing, alunosFiltrados size: " + alunosFiltrados.size());

                if (!nomeBusca.isEmpty()) {

                    Log.d("ListarAlunosActivity", "onClick: Adding filtered alunos, Thread: " + Thread.currentThread().getName());
                    alunosFiltrados.addAll(dao.buscarAluno(nomeBusca));
                    Log.d("ListarAlunosActivity", "onClick: After adding filtered, alunosFiltrados size: " + alunosFiltrados.size());
                } else {
                    // se estiver vazio, exibir todos os dados

                    Log.d("ListarAlunosActivity", "onClick: Adding all alunos, Thread: " + Thread.currentThread().getName());
                    alunosFiltrados.addAll(alunos);
                    Log.d("ListarAlunosActivity", "onClick: After adding all, alunosFiltrados size: " + alunosFiltrados.size());
                }
                Log.d("ListarAlunosActivity", "onClick: Calling notifyDataSetChanged, Thread: " + Thread.currentThread().getName());
                adaptador.notifyDataSetChanged();
                Log.d("ListarAlunosActivity", "onClick: notifyDataSetChanged called");
            }
        });
    }

    public void voltar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto, menu);
    }

    //METODO EXCLUIR
    public void excluir(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Realmente deseja excluir o aluno?")
                .setNegativeButton("NÃO", null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("ListarAlunosActivity", "excluir: Before remove alunoFiltrado size : " + alunosFiltrados.size()+ ", Thread: " + Thread.currentThread().getName());
                        alunosFiltrados.remove(alunoExcluir);
                        Log.d("ListarAlunosActivity", "excluir: After remove alunoFiltrado size : " + alunosFiltrados.size()+ ", Thread: " + Thread.currentThread().getName());
                        Log.d("ListarAlunosActivity", "excluir: Before remove aluno size : " + alunos.size()+ ", Thread: " + Thread.currentThread().getName());
                        alunos.remove(alunoExcluir);
                        Log.d("ListarAlunosActivity", "excluir: After remove aluno size : " + alunos.size()+ ", Thread: " + Thread.currentThread().getName());
                        dao.excluir(alunoExcluir);
                        Log.d("ListarAlunosActivity", "excluir: Calling notifyDataSetChanged, Thread: " + Thread.currentThread().getName());
                        adaptador.notifyDataSetChanged();
                        Log.d("ListarAlunosActivity", "excluir: notifyDataSetChanged called");
                    }
                }).create();
        dialog.show();
    }

    public void atualizar(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);
        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("aluno", alunoAtualizar);
        startActivity(it);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log do onResume
        Log.d("ListarAlunosActivity", "onResume: Fetching all alunos, Thread: " + Thread.currentThread().getName());
        alunos = dao.obterTodos();
        Log.d("ListarAlunosActivity", "onResume: Before clearing, alunosFiltrados size: " + alunosFiltrados.size() + ", Thread: " + Thread.currentThread().getName());
        alunosFiltrados.clear();
        Log.d("ListarAlunosActivity", "onResume: After clearing, alunosFiltrados size: " + alunosFiltrados.size() + ", Thread: " + Thread.currentThread().getName());
        Log.d("ListarAlunosActivity", "onResume: Adding all alunos, Thread: " + Thread.currentThread().getName());
        alunosFiltrados.addAll(alunos);
        Log.d("ListarAlunosActivity", "onResume: After adding all, alunosFiltrados size: " + alunosFiltrados.size() + ", Thread: " + Thread.currentThread().getName());
        Log.d("ListarAlunosActivity", "onResume: Calling notifyDataSetChanged, Thread: " + Thread.currentThread().getName());
        adaptador.notifyDataSetChanged();
        Log.d("ListarAlunosActivity", "onResume: notifyDataSetChanged called");

    }
}