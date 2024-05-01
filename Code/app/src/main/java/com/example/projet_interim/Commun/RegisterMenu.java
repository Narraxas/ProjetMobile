package com.example.projet_interim.Commun;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import com.example.projet_interim.DB;
        import com.example.projet_interim.R;

public class RegisterMenu extends AppCompatActivity {

    private Button candidatButton;
    private Button employeurButton;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_menu);

        DB.exempleFillIfEmpty(getApplicationContext(), this);

        candidatButton = findViewById(R.id.candidat_button);
        employeurButton = findViewById(R.id.employeur_button);

        intent = new Intent(getApplicationContext(), RegisterFormulaire.class);
    }

    public void candidat_register(View v){
        intent.putExtra("role", "candidat");
        next();
    }
    public void employeur_register(View v){
        intent.putExtra("role", "employeur");
        next();
    }

    public void next(){
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }
}

