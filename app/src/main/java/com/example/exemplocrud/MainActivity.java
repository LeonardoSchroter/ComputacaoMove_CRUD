package com.example.exemplocrud;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity {

    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private AlunoDao dao;

    private Aluno aluno = null;

    private ImageView imageView; // Adicione a ImageView para exibir a foto tirada
    //DADOS DE PERMISSAO PARA ACESSAR A CAMERA
// Constante para identificar a solicitação de permissão para usar a câmera.
// Usada para verificar se o usuário concedeu permissão de acesso à câmera.
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    // Objeto que lança a intenção de captura de imagem e lida com o retorno dos
    // Ele é usado para iniciar a câmera e receber o resultado da imagem capturada.
    private ActivityResultLauncher<Intent> cameraLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.editNome);
        cpf = findViewById(R.id.editCPF);
        telefone = findViewById(R.id.editTelefone);

        dao = new AlunoDao(this);

        Intent it = getIntent(); //pega intenção
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCpf());
            telefone.setText(aluno.getTelefone());

            byte[] fotoBytes = aluno.getFotoBytes();
            if(fotoBytes != null && fotoBytes.length>0){
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes,0,fotoBytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }

        imageView = findViewById(R.id.imageView);

        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);

        cameraLauncher = registerForActivityResult(
                // Registro para iniciar uma atividade que espera um resultado. Neste caso, iniciando a captura de foto.
                new ActivityResultContracts.StartActivityForResult(),
                // Função que será executada quando a atividade de captura de foto retornar com o resultado.
                result -> {

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

    public void salvar(View view){
        String nomeDigitado = nome.getText().toString().trim();
        String cpfDigitado = cpf.getText().toString().trim();
        String telefoneDigitado = telefone.getText().toString().trim();

        if(nomeDigitado.isEmpty() || cpfDigitado.isEmpty() || telefoneDigitado.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dao.validaCpf(cpfDigitado)){
            Toast.makeText(this, "CPF invalido!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dao.cpfExistente(cpfDigitado)){
            Toast.makeText(this, "CPF ja existe no banco de dados!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dao.validaTelefone(telefoneDigitado)){
            Toast.makeText(this, "Telefone invalido! Use o formato (XX) 9XXXX-XXXX", Toast.LENGTH_SHORT).show();
            return;
        }

        if(aluno==null) {
            Aluno a = new Aluno();
            a.setNome(nome.getText().toString());
            a.setCpf(cpf.getText().toString());
            a.setTelefone(telefone.getText().toString());

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Comprime o Bitmap no formato PNG e escreve os dados comprimidos no stream de bytes
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                // Converte os dados da imagem para um array de bytes
                byte[] fotoBytes = stream.toByteArray();
                // Armazena o array de bytes no objeto aluno para que a foto seja salva no banco de dados
                aluno.setFotoBytes(fotoBytes);
            }

            long id = dao.inserir(a);
            Toast.makeText(this,"Aluno inserido com id: "+id,Toast.LENGTH_SHORT).show();
        }
        else {
            aluno.setNome(nome.getText().toString());
            aluno.setCpf(cpf.getText().toString());
            aluno.setTelefone(telefone.getText().toString());
            dao.atualizar(aluno); //inserir o aluno
            Toast.makeText(this,"Aluno Atualizado!! com id: ", Toast.LENGTH_SHORT).show();
        }



    }

    public void irParaListar(View view){
        Intent intent = new Intent(this,MainActivityListar.class);
        startActivity(intent);
    }

    public void tirarFoto() {
        checkCameraPermissionAndStart();
    }

    private void checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "A permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private Bitmap corrigirOrientacao(Bitmap bitmap) {
        if (bitmap == null) return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // Rotaciona a imagem em 90 graus (padrão para fotos invertidas)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }
}