package noloan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.galgo.noloan.protobuf.UserProto.loguser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    RecyclerView userView;

    FirebaseFirestore mfirestore;
    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initFirestore();

        name = findViewById(R.id.ET_name);

        Button add = findViewById(R.id.B_adduser);
        add.setOnClickListener(this);

        userView = findViewById(R.id.RV_userview);
        UserAdapter adapter = new UserAdapter(query);
        userView.setAdapter(adapter);
        userView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }

    private void initFirestore() {
        mfirestore = FirebaseFirestore.getInstance();
        query = mfirestore.collection("Users");
    }

    //Write the user to the Firestore
    private void writeUser(loguser user) {
        FireStoreElement userElement = encodeUser(user);

        CollectionReference users = mfirestore.collection("Users");
        users.add(userElement);
    }

    //Encode user proto to base64 for storing in Firestore
    private FireStoreElement encodeUser(loguser user) {
        byte[] protoBytes = user.toByteArray();
        String base64BinaryString = Base64.encodeToString(protoBytes, Base64.DEFAULT);
        return new FireStoreElement(base64BinaryString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.B_adduser:
                loguser user = loguser.newBuilder()
                        .setName(name.getText().toString())
                        .build();
                writeUser(user);
        }
    }
}
