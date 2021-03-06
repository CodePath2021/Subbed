package com.example.subbed;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    Subscription sub;
    boolean updated = false;    // keep track of whether the sub has been updated

    // views that are visible before editing
    TextView price;
    TextView type;
    TextView date;
    FloatingActionButton FAB;

    // views that are visible during editing
    EditText etPrice;
    RadioGroup rdType;
    RadioButton monthBtn;
    RadioButton yearBtn;
    TextView clickableDate;
    Button finishBtn;

    // other stuff
    String newType = "";        // default is empty string
    private DatePickerDialog.OnDateSetListener mDateSetListener;    // for the date picker

    int next_billing_year;
    int next_billing_month;
    int next_billing_day;

    Button colorBtn;
    int mDefaultColor;      // for the color picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // views that are visible before editing
        price = findViewById(R.id.price);
        type = findViewById(R.id.type);
        date = findViewById(R.id.date);
        FAB = findViewById(R.id.floatingActionButton);

        // views that are visible when editing
        etPrice = findViewById(R.id.etPrice);
        rdType = findViewById(R.id.rdType);
        monthBtn = findViewById(R.id.month);
        yearBtn = findViewById(R.id.year);
        finishBtn = findViewById(R.id.button);
        clickableDate = findViewById(R.id.clickableDate);

        // get the corresponding subscription that the user clicked on
        sub = Parcels.unwrap(getIntent().getParcelableExtra("subscription"));
        price.setText("$" + sub.getPrice());
        type.setText(sub.getType());
        date.setText(sub.getNextBillingDate());

        // make the editable views invisible
        etPrice.setVisibility(View.GONE);
        rdType.setVisibility(View.GONE);
        finishBtn.setVisibility(View.GONE);
        clickableDate.setVisibility(View.GONE);

        // Rename the actionBar
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(sub.getName());

        // For now, the user can edit color in whatever situation
        colorBtn = findViewById(R.id.colorBtn);
        mDefaultColor = Color.parseColor(sub.getColor());
        colorBtn.setBackgroundColor(mDefaultColor);
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }

    /**
     * Open the color picker
     */
    public void openColorPicker() {
        AmbilWarnaDialog colorPicker =new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) { }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                colorBtn.setBackgroundColor(mDefaultColor);
                // update the subscription's color and the updated boolean
                sub.setColor(String.format("#%06X", (0xFFFFFF & mDefaultColor)));
                updated = true;
            }
        });
        colorPicker.show();
    }

    /**
     * Onclick method for the floating action button
     * Switch visibility of views
     * @param view
     */
    public void makeEditable(View view) {
        FAB.setVisibility(View.GONE);
        price.setVisibility(View.GONE);
        type.setVisibility(View.GONE);
        date.setVisibility(View.GONE);

        finishBtn.setVisibility(View.VISIBLE);

        etPrice.setVisibility(View.VISIBLE);
        etPrice.setHint(price.getText().toString().substring(1));
        etPrice.setText(etPrice.getText().toString());

        rdType.setVisibility(View.VISIBLE);
        // check the original type
        if (type.getText().toString().equals("Monthly")) {
            monthBtn.setChecked(true);
        } else {
            yearBtn.setChecked(true);
        }

        clickableDate.setText("");
        clickableDate.setHint("Select a date");
        clickableDate.setVisibility(View.VISIBLE);
        // select a new date
        clickableDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(DetailActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        sub.getNextBillingYear(), sub.getNextBillingMonth()-1, sub.getNextBillingDay());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            // month starts with 0, so have to add 1
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                next_billing_year = year;
                next_billing_month = month + 1;
                next_billing_day = dayOfMonth;
                // update the subscription, the clickable TextView, and the updated boolean
                sub.setNextBillingDate(next_billing_year, next_billing_month, next_billing_day);
                clickableDate.setText(sub.getNextBillingDate());
                updated = true;
            }
        };
    }

    /**
     * Onclick method for the finishing editing button
     * Switch visibility of views and render the new views
     * @param view
     */
    public void makeUneditable(View view) {
        FAB.setVisibility(View.VISIBLE);
        price.setVisibility(View.VISIBLE);
        type.setVisibility(View.VISIBLE);
        date.setVisibility(View.VISIBLE);

        finishBtn.setVisibility(View.GONE);
        etPrice.setVisibility(View.GONE);
        rdType.setVisibility(View.GONE);
        clickableDate.setVisibility(View.GONE);

        // update the attributes if modified, and render the views
        if (!etPrice.getText().toString().equals("")) {
            // update the subscription price
            sub.setPrice(Double.parseDouble(etPrice.getText().toString()));
            updated = true;
        }
        price.setText("$" + sub.getPrice());

        if (!newType.equals("")) {
            sub.setType(newType);
            updated = true;
        }
        type.setText(sub.getType());

        // nextBillingDate has already been updated above
        date.setText(sub.getNextBillingDate());
    }

    /**
     * Get the result of the radio button
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked.
        switch (view.getId()) {
            case R.id.month:
                if (checked)
                    newType = "Monthly";
                break;
            case R.id.year:
                if (checked)
                    newType = "Yearly";
                break;
            default:
                // Do nothing.
                break;
        }
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's activity hierarchy from the action bar
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        checkUpdates();
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    /**
     * Check if the subscription has been updated
     */
    private void checkUpdates() {
        if (updated) {
            // send the updated subscription back to the fragment
            Intent replyIntent = new Intent();
            replyIntent.putExtra("Update subscription", Parcels.wrap(sub));
            updateSubscription(sub);
            setResult(2, replyIntent);
        }
    }

    private void updateSubscription(Subscription sub) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        sub.setUser(currentUser);
        sub.saveInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Error while updating", e);
                // TODO: error handling
            }
            Log.d(TAG, "Subscription update was successful!");
        });
    }

    // TODO: A problem - using the exit button at the bottom left won't update the subscription
}