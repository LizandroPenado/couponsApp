package com.example.couponsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.couponsapp.controladores.UsuarioControl;
import com.example.couponsapp.modelos.Usuario;
import com.example.couponsapp.vistas.CanjearCuponFragment;
import com.example.couponsapp.vistas.GestionarCuponFragment;
import com.example.couponsapp.vistas.HomeFragment;
import com.example.couponsapp.vistas.GestionarUsuarioFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class InicioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView nombre, email;
    ImageView foto;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    Bundle extras;

    UsuarioControl usuarioControl = new UsuarioControl(this);
    Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        extras = getIntent().getExtras();

        //Obtener datos generales
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount cuenta = GoogleSignIn.getLastSignedInAccount(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        nombre = navigationView.getHeaderView(0).findViewById(R.id.nombre_usuario);
        email = navigationView.getHeaderView(0).findViewById(R.id.email_usuario);
        foto = navigationView.getHeaderView(0).findViewById(R.id.foto_user);

        //Mostrar datos del usuario
        if (cuenta != null) {
            String nombreUser = cuenta.getDisplayName();
            String emailUser = cuenta.getEmail();

            if(usuarioControl.userExist(cuenta.getGivenName(), cuenta.getEmail()) == 0){

                int indexUser = cuenta.getEmail().indexOf('@');
                String userString = cuenta.getEmail().substring(0, indexUser);

                usuarioControl.insertUsuario(new Usuario(
                        0, //no tiene relevancia para insert
                        3,
                        0,
                        userString,
                        "bixxortnnuis34",
                        cuenta.getEmail(),
                        cuenta.getDisplayName(),
                        "",
                        "",
                        1
                ));
            }

            Uri userPhoto = cuenta.getPhotoUrl();
            nombre.setText(nombreUser);
            email.setText(emailUser);
            Glide.with(this).load(String.valueOf(userPhoto)).into(foto);
        }

        if(extras != null){
            String user = extras.getString("username");
            String password = extras.getString("password");
            usuario = usuarioControl.traerUsuario(user, password);
            nombre.setText(usuario.getNombre() + " " + usuario.getApellido());
            email.setText(usuario.getEmail());
        }

        //Inicializar en la opcion home
        getSupportFragmentManager().beginTransaction().add(R.id.content, new HomeFragment()).commit();
        setTitle("Home");
        setSupportActionBar(toolbar);

        //Opciones para mostrar el icono de hamburger
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        //Seleccion de opcion
        navigationView.setNavigationItemSelectedListener(this);

        //Esconder opciones
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectItemNav(item);
        return true;
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void selectItemNav(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_gestion_cupon:
                replaceFragment(new GestionarCuponFragment());
                break;
            case R.id.nav_home:
                replaceFragment(new HomeFragment());
                break;
            case R.id.nav_gestion_usuario:
                replaceFragment(new GestionarUsuarioFragment());
                break;
            case R.id.nav_canjear_cupon:
                replaceFragment(new CanjearCuponFragment());
                break;
            case R.id.nav_cerrar_sesion:
                cerrarSesion();
                break;
        }
        setTitle(item.getTitle());
        drawerLayout.closeDrawers();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }

    void cerrarSesion() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                Intent intent = new Intent(InicioActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}