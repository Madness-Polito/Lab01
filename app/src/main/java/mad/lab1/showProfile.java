package mad.lab1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class showProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
    }

    // create the edit bar next to the app name
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

      getMenuInflater().inflate(R.menu.profile_edit, menu);
      return true;
    }

    // associate eventlistener to the edit bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent i = new Intent(getApplicationContext(), editProfile.class);
        startActivity(i);

        return super.onOptionsItemSelected(item);
    }
}
