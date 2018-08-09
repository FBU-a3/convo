package a3.com.convo.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import a3.com.convo.R;
import a3.com.convo.fragments.AddInfoFragment;

public class ProfileActivity extends AppCompatActivity {
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.show_profile_fragment, new AddInfoFragment());
        ft.commit();
    }
}
