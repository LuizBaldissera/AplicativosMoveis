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

    public long inserir(Aluno aluno) {
        // Long porque retorna o id do aluno
        if (!cpfExistente(aluno.getCpf())) {
            ContentValues values = new ContentValues(); // Valores que irei inserir
            values.put("nome", aluno.getNome());
            values.put("cpf", aluno.getCpf());
            values.put("telefone", aluno.getTelefone());

            return banco.insert("aluno", null, values);
            // Tabela aluno, não terá colunas vazias, valores values
        } else {
            // CPF já existe, você pode lidar com isso de acordo com sua lógica
            return -1; // Retorno -1 para chamado do método inserir() no CadastroAlunoActivity
        }
    }

// ---------------------------------- VERIFICA SE O CPF EXISTE NO BANCO DE DADOS ----------------------------------

    public boolean cpfExistente(String cpf) {
        // Consulta no banco de dados para verificar se o CPF já existe
        Cursor cursor = banco.query("aluno", new String[]{"id"},
                "cpf = ?", new String[]{cpf},
                null, null, null);

        boolean cpfExiste = cursor.getCount() > 0;
        cursor.close();

        return cpfExiste;
    }

    public static boolean validaTelefone(String telefone) {
        // Remove espaços, parênteses, hífens e outros caracteres não numéricos
        telefone = telefone.replaceAll("[^0-9]", "");

        // Expressão regular para validar números de telefone no Brasil
        String regex = "^(?:[1-9]{2})?[2-9][0-9]{3,4}[0-9]{4}$";

        // Compila o padrão e faz a verificação
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(telefone);

        return matcher.matches();
    }

    public static class CPFValidator {
        public static boolean validaCpf(String CPF) {
            // Exibe a entrada recebida (usado para depuração)
            System.out.println("String de entrada do método: " + CPF);

            // Remove espaços e caracteres não numéricos
            CPF = CPF.replaceAll("[^0-9]", "");

            // Verifica se o CPF tem exatamente 11 dígitos
            if (CPF.length() != 11) {
                return false;
            }

            // Verifica se o CPF não é uma sequência repetida (ex: 00000000000, 11111111111)
            if (CPF.matches("(\\d)\\1{10}")) {
                return false;
            }

            char digito1, digito2;
            int soma, num, peso, resto;

            try {
                // Cálculo do Primeiro Dígito Verificador
                soma = 0;
                peso = 10;
                for (int i = 0; i < 9; i++) {
                    num = CPF.charAt(i) - '0';
                    soma += (num * peso);
                    peso--;
                }
                resto = soma % 11;
                digito1 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

                // Cálculo do Segundo Dígito Verificador
                soma = 0;
                peso = 11;
                for (int i = 0; i < 10; i++) {
                    num = CPF.charAt(i) - '0';
                    soma += (num * peso);
                    peso--;
                }
                resto = soma % 11;
                digito2 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

                // Compara os Dígitos Verificadores
                return (digito1 == CPF.charAt(9) && digito2 == CPF.charAt(10));
            } catch (Exception e) {
                return false;
            }
        }

        public static void main(String[] args) {
            String cpfTeste = "12345678909"; // Substitua por um CPF válido
            System.out.println("CPF válido? " + validaCpf(cpfTeste));
        }
    }


    public List<Aluno> obterTodos() {
        List<Aluno> alunos = new ArrayList<>();
        //cursor aponta para as linhas retornadas
        Cursor cursor = banco.query("aluno", new String[]{"id", "nome", "cpf", "telefone"},
                null, null, null, null, null); //nome da tabela, nome das colunas, completa com null o método
        //que por padrão pede esse número de colunas obrigatórias
        while (cursor.moveToNext()) { //verifica se consegue mover para o próximo ponteiro ou linha
            Aluno a = new Aluno();
            a.setId(cursor.getInt(0)); // new String[]{"id", "nome", "cpf", "telefone"}, id é coluna '0'
            a.setNome(cursor.getString(1)); // new String[]{"id", "nome", "cpf", "telefone"}, nome é coluna '1'
            a.setCpf(cursor.getString(2)); // new String[]{"id", "nome", "cpf", "telefone"}, cpf é coluna '2'
            a.setTelefone(cursor.getString(3)); // new String[]{"id", "nome", "cpf", "telefone"}, telefone é coluna '3'
            alunos.add(a);
        }
        return alunos;
    }
}
