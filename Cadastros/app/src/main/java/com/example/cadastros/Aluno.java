package com.example.cadastros;

import java.io.Serializable;

public class Aluno implements Serializable {
        private int id;
        private String telefone;
        private String cpf;
        private String nome;

        private byte[] fotoBytes;

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


        public byte[] getFotoBytes() {
            return fotoBytes;
        }

        public void setFotoBytes(byte[] fotoBytes) {
            this.fotoBytes = fotoBytes;
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

