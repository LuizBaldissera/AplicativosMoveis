package com.example.cadastros;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlunoDAO {
    private SQLiteDatabase banco;
    private Conexao conexao;

    public AlunoDAO(Context context) {
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }

    public void excluir(Aluno a){
        banco.delete("aluno", "id = ?", new String[]{String.valueOf(a.getId())});
        // no lugar do ? vai colocar o id do aluno
    }
    public long inserir(Aluno aluno) {
        if (!cpfExistente(aluno.getCpf())) {
            ContentValues values = new ContentValues();
            values.put("nome", aluno.getNome());
            values.put("cpf", aluno.getCpf());
            values.put("telefone", aluno.getTelefone());
            values.put("fotoBytes",aluno.getFotoBytes());
            return banco.insert("aluno", null, values);
        } else {
            return -1;
        }
    }

    public boolean cpfExistente(String cpf) {
        Cursor cursor = banco.query("aluno", new String[]{"id"},
                "cpf = ?", new String[]{cpf},
                null, null, null);

        boolean cpfExiste = cursor.getCount() > 0;
        cursor.close();
        return cpfExiste;
    }

    public boolean validarTelefone(String telefone) {
        telefone = telefone.replaceAll("[^0-9]", "");
        String regex = "^(?:[1-9]{2})?[2-9][0-9]{3,4}[0-9]{4}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(telefone);

        return matcher.matches();
    }

    public boolean validarCpf(String CPF) {
        CPF = CPF.replaceAll("[^0-9]", "");

        if (CPF.length() != 11 || CPF.matches("(\\d)\\1{10}")) {
            return false;
        }

        char digito1, digito2;
        int soma, num, peso, resto;

        try {
            soma = 0;
            peso = 10;
            for (int i = 0; i < 9; i++) {
                num = CPF.charAt(i) - '0';
                soma += (num * peso);
                peso--;
            }
            resto = soma % 11;
            digito1 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                num = CPF.charAt(i) - '0';
                soma += (num * peso);
                peso--;
            }
            resto = soma % 11;
            digito2 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

            return (digito1 == CPF.charAt(9) && digito2 == CPF.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    public List<Aluno> obterTodos() {
        List<Aluno> alunos = new ArrayList<>();
        Cursor cursor = banco.query("aluno", new String[]{"id", "nome", "cpf", "telefone", "fotoBytes"},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            Aluno a = new Aluno();
            a.setId(cursor.getInt(0));
            a.setNome(cursor.getString(1));
            a.setCpf(cursor.getString(2));
            a.setTelefone(cursor.getString(3));
            a.setFotoBytes(cursor.getBlob(4));
            alunos.add(a);
        }
        cursor.close(); // Fechar o cursor após o uso
        return alunos;
    }

    public void atualizar(Aluno aluno){
        ContentValues values = new ContentValues(); //valores que irei inserir
        values.put("nome", aluno.getNome());
        values.put("cpf", aluno.getCpf());
        values.put("telefone", aluno.getTelefone());
        values.put("fotoBytes",aluno.getFotoBytes());
        banco.update("aluno", values, "id = ?", new String[]{String.valueOf(aluno.getId())});

    }

}
