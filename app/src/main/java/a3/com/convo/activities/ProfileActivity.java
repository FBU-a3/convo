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
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.show_profile_fragment, new ProfileDetailsFragment());
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
