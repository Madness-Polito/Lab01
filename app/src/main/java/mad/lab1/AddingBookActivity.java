package mad.lab1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matteo on 18/04/2018.
 */

public class AddingBookActivity extends AppCompatActivity {

    private TextView txt_bookTitle;
    private TextView txt_author;
    private TextView txt_description;
    private Spinner spin_condition;
    private List<String> spinList;

    private Button btn_ok;
    private Button btn_cancel;


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        spinList = new ArrayList<>();

        setContentView(R.layout.add_book_layout);
        txt_bookTitle = findViewById(R.id.txt_bookTitle);
        txt_author = findViewById(R.id.txt_author);
        txt_description = findViewById(R.id.txt_description);
        spin_condition = findViewById(R.id.spin_condition);
        btn_ok = findViewById(R.id.btn_ok);
        btn_cancel = findViewById(R.id.btn_cancel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            txt_author.setText(extras.getString("author"));
            txt_description.setText(extras.getString("description"));
            txt_bookTitle.setText(extras.getString("title"));
        }

        spinList.add(getString(R.string.cond_vUsed));
        spinList.add(getString(R.string.cond_used));
        spinList.add(getString(R.string.cond_new));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_condition.setAdapter(adapter);



        btn_ok.setOnClickListener(view -> {
            String condition = spin_condition.getSelectedItem().toString();
            if(condition != null){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("condition", condition);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }else {
                //condition is missing
                Toast.makeText(this, getString(R.string.missingCondition), Toast.LENGTH_LONG).show();
            }
        });

        btn_cancel.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        });


    }

}
