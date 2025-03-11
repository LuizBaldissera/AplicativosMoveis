package com.example.cadastros;

    public class Aluno {
        private int id;
        private String telefone;
        private String cpf;
        private String nome;


        public int getId() {
            return id;
        }

        public void setId(int anInt) {
            this.id = id;
        }

        public String getTelefone() {
            return telefone;
        }

        public void setTelefone(String telefone) {
            this.telefone = telefone;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        @Override
        public String toString() {
            return "Aluno{" +
                    "id=" + id +
                    ", telefone='" + telefone + '\'' +
                    ", cpf='" + cpf + '\'' +
                    ", nome='" + nome + '\'' +
                    '}';
        }
    }

