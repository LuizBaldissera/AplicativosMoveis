package com.example.cadastros;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private AlunoDAO dao;

    private Aluno aluno = null;

    private ImageView imageView;
    private TextView enderecoTextView; // TextView para mostrar o endereço
    // Nome do arquivo SharedPreferences
    private static final String PREFS_NAME = "CepPrefs";
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private ActivityResultLauncher<Intent> cameraLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.NomeD);
        cpf = findViewById(R.id.CpfD);
        telefone = findViewById(R.id.TelefoneD);
        imageView = findViewById(R.id.imageView);
        dao = new AlunoDAO(this);
        enderecoTextView = findViewById(R.id.Endereco);


        Intent it = getIntent(); //pega intenção
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCpf());
            telefone.setText(aluno.getTelefone());

            // Carregar a foto no ImageView no momento que carregar os dados para atualizar
            byte[] fotoBytes = aluno.getFotoBytes();
            if (fotoBytes != null && fotoBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imageView.setImageBitmap(bitmap);
            }

        }

        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        // CAMERA LAUNCHER - Configura o launcher que será utilizado para capturar o resultado da atividade de tirar foto.
        cameraLauncher = registerForActivityResult(
                // Registro para iniciar uma atividade que espera um resultado. Neste caso, iniciando a captura de foto.

                // Função que será executada quando a atividade de captura de foto retornar com o resultado.
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Verificar se a operação foi bem-sucedida == 'RESULT_OK', se for RESULT_CANCELED (0) //deu erro ou fechou
                    if (result.getResultCode() == RESULT_OK) {
                        // Obter os dados da intenção de retorno
                        Intent data = result.getData();
                        // Obter os extras da intenção (que contêm a imagem capturada)
                        Bundle extras = data.getExtras();
                        // Obter a imagem capturada como um objeto Bitmap
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        // Corrige a orientação antes de exibir – Giro de 90 graus
                        Bitmap imagemCorrigida = corrigirOrientacao(imageBitmap);
                        // Exibir a imagem na ImageView
                        imageView.setImageBitmap(imagemCorrigida);
                    }
                }
        );

    }


    private void startCamera() {
        Log.d("DEBUG", "Chamando Intent de Câmera...");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Log.d("DEBUG", "Nenhuma atividade de câmera disponível!");
        }
    }


    private void checkCameraPermissionAndStart() {
        // Verifica se a permissão para usar a câmera já foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão não foi concedida, solicite-a
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Se a permissão já foi concedida, inicie a câmera
            startCamera();
        }
    }

    public void tirarFoto(View view) {
        checkCameraPermissionAndStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Obtém o SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Obtém o endereço salvo (se existir)
        String enderecoCompleto = sharedPreferences.getString("enderecoCompleto", null);
        // Se o endereço existir, exibe no TextView
        if (enderecoCompleto != null) {
            enderecoTextView.setText(enderecoCompleto);
        } else {
            enderecoTextView.setText("Nenhum endereço salvo.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG", "Permissão concedida! Iniciando câmera...");
                startCamera();
            } else {
                Log.d("DEBUG", "Permissão negada.");
                Toast.makeText(this, "A permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private Bitmap corrigirOrientacao(Bitmap bitmap) {
        if (bitmap == null) return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // Rotaciona a imagem em 90 graus (padrão para fotos invertidas)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public void irParaListar (View view) {
        Intent intent = new Intent(this, ListarAlunosActivity.class);
        startActivity(intent);
    }

    public void irParaCEP (View view) {
        Intent intent = new Intent(this, ListarCepActivity.class);
        startActivity(intent);
        Log.d("Botao", "Clicou no botão irParaCEP");
    }

    public void salvar (View view) {

            String nomeDigitado = nome.getText().toString().trim();
            String cpfDigitado = cpf.getText().toString().trim();
            String telefoneDigitado = telefone.getText().toString().trim();

            // Verifica se os campos estão vazios
            if (nomeDigitado.isEmpty() || cpfDigitado.isEmpty() || telefoneDigitado.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validação do CPF (verifica se o formato e os dígitos são válidos)
            System.out.println("CPF antes da validação: " + cpfDigitado);
            if (!dao.validarCpf(cpfDigitado)) {
                Toast.makeText(this, "CPF inválido. Digite novamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Se for cadastrar novo aluno ou se for atualizar os dados ignora o CPF se for igual do próprio aluno
            // Se o aluno atualizar um CPF diferente daí sim será verificado
            if (aluno == null || !cpfDigitado.equals(aluno.getCpf())) {
                // Verifica se o CPF já existe no banco
                if (dao.cpfExistente(cpfDigitado)) {
                    Toast.makeText(this, "CPF duplicado. Insira um CPF diferente.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Validação de Telefone
            if (!dao.validarTelefone(telefoneDigitado)) {
                Toast.makeText(this, "Telefone inválido! Use o formato correto: (XX) 9XXXX-XXXX", Toast.LENGTH_SHORT).show();
                return;
            }

            if (aluno == null) {
                // Criar objeto Aluno
                Aluno aluno = new Aluno();
                aluno.setNome(nomeDigitado);
                aluno.setCpf(cpfDigitado);
                aluno.setTelefone(telefoneDigitado);

                // Verifica se existe uma imagem válida no ImageView
                if (imageView.getDrawable() != null) { // Evita erro caso não tenha imagem
                    BitmapDrawable drawable = (BitmapDrawable)
                            imageView.getDrawable();
                    // Converte o drawable em um objeto Bitmap
                    Bitmap bitmap = drawable.getBitmap();
                    // Cria um stream de saída para armazenar os bytes da imagem
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Comprime o Bitmap no formato PNG e escreve os dados comprimidos no stream de bytes
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    // Converte os dados da imagem para um array de bytes
                    byte[] fotoBytes = stream.toByteArray();
                    // Armazena o array de bytes no objeto aluno para que a foto seja salva no banco de dados
                    aluno.setFotoBytes(fotoBytes);
                }

                // Inserir aluno no banco de dados
                long id = dao.inserir(aluno);

                if (id != -1) {
                    Toast.makeText(this, "Aluno inserido com id: " + id, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erro ao inserir aluno. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Atualização de um aluno existente
                aluno.setNome(nomeDigitado);
                aluno.setCpf(cpfDigitado);
                aluno.setTelefone(telefoneDigitado);

                // Verifica se existe uma imagem válida no ImageView
                if (imageView.getDrawable() != null) { // Evita erro caso não tenha imagem
                    BitmapDrawable drawable = (BitmapDrawable)
                            imageView.getDrawable();
                    // Converte o drawable em um objeto Bitmap
                    Bitmap bitmap = drawable.getBitmap();
                    // Cria um stream de saída para armazenar os bytes da imagem
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Comprime o Bitmap no formato PNG e escreve os dados comprimidos no stream de bytes
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    // Converte os dados da imagem para um array de bytes
                    byte[] fotoBytes = stream.toByteArray();
                    // Armazena o array de bytes no objeto aluno para que a foto seja salva no banco de dados
                    aluno.setFotoBytes(fotoBytes);
                }

                dao.atualizar(aluno);
                Toast.makeText(this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            }

// Fecha a tela de cadastro e volta para a listagem
            finish();

        }




}
