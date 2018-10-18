package com.example.vaibhavchahal93788.myapplication.billdesk.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.utility.Constants;

public class UserProfileActivity extends AppCompatActivity {

    public static void startActivity(Activity activity) {
        Intent intent= new Intent(activity, UserProfileActivity.class);
        intent.putExtra(activity.getResources().getString(R.string.parent_class_name), activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getToolbar();
    }

    private void getToolbar() {

        getSupportActionBar().setHomeButtonEnabled(true);
       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.rupee_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_icon_pos);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));

    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent = getIntent();
        String className = parentIntent.getStringExtra(getResources().getString(R.string.parent_class_name)); //getting the parent class name
        Intent newIntent = null;
        try {
            //you need to define the class with package name
            newIntent = new Intent(UserProfileActivity.this, Class.forName(Constants.PACKAGE + className));
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            overridePendingTransition(R.anim.animation_enter_backward,R.anim.animation_leave_backward);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }
}
