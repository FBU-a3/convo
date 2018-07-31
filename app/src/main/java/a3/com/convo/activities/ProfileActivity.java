package a3.com.convo.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import a3.com.convo.R;
import a3.com.convo.fragments.AddInfoFragment;
import a3.com.convo.fragments.ProfileDetailsFragment;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment profileFrag = new ProfileDetailsFragment();
        ft.replace(R.id.show_profile_fragment, profileFrag);
        ft.commit();
    }

    public void goToAddInfo() {
        Fragment fragment = new AddInfoFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.show_profile_fragment, fragment);
        fragmentTransaction.commit();
    }

    public void goBackToProfile() {
        Fragment fragment = new ProfileDetailsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.show_profile_fragment, fragment);
        fragmentTransaction.commit();
    }
}
