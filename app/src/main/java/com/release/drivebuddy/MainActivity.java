package com.release.drivebuddy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.skyfishjy.library.RippleBackground;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView gifImage;
    RippleBackground rippleBackground;
    SwitchMaterial switchMaterial;
    MainViewModel viewModel;
    public static WeakReference<MainActivity> weakActivity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askingPermissions();
        initializations();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        gifImage.setOnClickListener(view -> {
            switchMaterial.setChecked(!viewModel.isFlag());
        });

        switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                falseFun();
            } else {
                trueFun();
            }
        });

    }

    private void askingPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.PROCESS_OUTGOING_CALLS
                            , Manifest.permission.READ_CALL_LOG
                            , Manifest.permission.SEND_SMS
                            , Manifest.permission.READ_PHONE_NUMBERS}, 11);
        }
    }

    private void trueFun() {
        viewModel.setFlag(false);
        Toast.makeText(this, "Drive Buddy off", Toast.LENGTH_SHORT).show();
        Glide.with(this)
                .load(R.drawable.chilling)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.driving)
                .into(gifImage);
        if (rippleBackground.isRippleAnimationRunning())
            rippleBackground.stopRippleAnimation();
        switchMaterial.setChecked(false);
    }

    private void falseFun() {
        viewModel.setFlag(true);
        Toast.makeText(this, "Drive Buddy on", Toast.LENGTH_SHORT).show();
        Glide.with(this)
                .load(R.drawable.driving)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.chilling)
                .into(gifImage);
        rippleBackground.startRippleAnimation();
        switchMaterial.setChecked(true);
    }

    private void initializations() {
        weakActivity = new WeakReference<>(MainActivity.this);
        gifImage = findViewById(R.id.gif_imageview);
        rippleBackground = findViewById(R.id.ripple_background);
        switchMaterial = findViewById(R.id.buddy_turn_on_switch);
        Glide.with(this)
                .load(R.drawable.chilling)
                .placeholder(R.drawable.circular_tab)
                .into(gifImage);
    }

    public static MainActivity getInstanceActivity() {
        return weakActivity.get();
    }

    public void sendMessage(String number) {
        TextInputEditText textInputEditText = findViewById(R.id.edit_text);
        String message = Objects.requireNonNull(textInputEditText.getText()).toString().trim();
        if (viewModel.isFlag())
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, message, null, null);
                Toast.makeText(MainActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
            }
    }

    public static class CallReceiver extends BroadcastReceiver {
        @SuppressLint("UnsafeProtectedBroadcastReceiver")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (number != null) {
                    MainActivity.getInstanceActivity().sendMessage(number);
                    Toast.makeText(context, "incoming number = " + number, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}