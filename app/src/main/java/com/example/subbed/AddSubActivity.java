package com.example.subbed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconPack;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.parceler.Parcels;

import java.util.Calendar;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddSubActivity extends AppCompatActivity implements IconDialog.Callback {

    private static final String TAG = "AddSubActivity";
    private static final String ICON_DIALOG_TAG = "icon-dialog";

    private DatePickerDialog.OnDateSetListener mDateSetListener;    // for the date picker
    int mDefaultColor;      // for the color picker
    Button colorBtn;
    Button iconBtn;
    ImageView iconImg;

    EditText etSubscription;
    EditText etCost;
    TextView tvDate;

    String type = "";

    int next_billing_year = 0;
    int next_billing_month;
    int next_billing_day;

    int icon_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("Add a new subscription");

        etSubscription = findViewById(R.id.etSubscription);
        etCost = findViewById(R.id.etCost);

        iconBtn = findViewById(R.id.iconBtn);
        iconImg = findViewById(R.id.iconImg);

        // If icon dialog is already added to fragment manager, get it. If not, create a new instance.
        IconDialog dialog = (IconDialog) getSupportFragmentManager().findFragmentByTag(ICON_DIALOG_TAG);
        IconDialog iconDialog = dialog != null ? dialog
                : IconDialog.newInstance(new IconDialogSettings.Builder().build());

        iconBtn.setOnClickListener(v -> {
                    // Open icon dialog
                    iconDialog.show(getSupportFragmentManager(), ICON_DIALOG_TAG); });

        // select the next billing date
        tvDate = findViewById(R.id.tvDate);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddSubActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
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
                tvDate.setText((month+1) + "/" + dayOfMonth + "/" + year);
            }
        };

        // select the color
        mDefaultColor = ContextCompat.getColor(AddSubActivity.this, R.color.colorPrimary);
        colorBtn = findViewById(R.id.colorBtn);
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }


    @Nullable
    @Override
    /**
     * Get the IconPack
     */
    public IconPack getIconDialogIconPack() {
        App newApp = new App(getApplicationContext());
        return newApp.getIconPack();
    }

    @Override
    /**
     * Select an icon and load into the imageView
     */
    public void onIconDialogIconsSelected(@NonNull IconDialog dialog, @NonNull List<Icon> icons) {
        for (Icon icon : icons) {
            icon_id = icon.getId();
            iconImg.setImageDrawable(icon.getDrawable());
        }
    }

    @Override
    public void onIconDialogCancelled() {}


    /**
     * Open the color picker
     */
    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) { }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                colorBtn.setBackgroundColor(mDefaultColor);
            }
        });
        colorPicker.show();
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
            case R.id.monthly:
                if (checked)
                    type = "Monthly";
                break;
            case R.id.yearly:
                if (checked)
                    type = "Yearly";
                break;
            default:
                // Do nothing.
                break;
        }
    }

    /**
     * The Onclick method for the "Done" button
     * @param view
     */
    public void addNewSub(View view) {
        // handle invalid user inputs
        if (next_billing_year == 0) {
            Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etCost.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etSubscription.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a valid Subscription", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type.equals("")) {
            Toast.makeText(this, "Please enter a valid type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (icon_id == 0) {
            Toast.makeText(this, "Please pick a valid icon", Toast.LENGTH_SHORT).show();
            return;
        }

        Subscription new_sub = new Subscription();
        new_sub.setName(etSubscription.getText().toString());
        new_sub.setType(type);
        new_sub.setPrice(Double.parseDouble(etCost.getText().toString()));
        new_sub.setColor(String.format("#%06X", (0xFFFFFF & mDefaultColor))); // convert to hex String
        new_sub.setNextBillingDate(next_billing_year, next_billing_month, next_billing_day);
        new_sub.setIconId(icon_id);

        Intent replyIntent = new Intent();
        replyIntent.putExtra("A new subscription", Parcels.wrap(new_sub));
        setResult(RESULT_OK, replyIntent);
        finish();
    }



    /**
     * This method is called whenever the user chooses to navigate Up within your application's activity hierarchy from the action bar
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }
}