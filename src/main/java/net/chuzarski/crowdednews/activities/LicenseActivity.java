package net.chuzarski.crowdednews.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.chuzarski.crowdednews.R;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LicenseActivity extends Activity {

    TextView licenseBody;
    ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        licenseBody = (TextView) findViewById(R.id.license_body);
        ab = getActionBar();

        ab.setTitle(getResources().getString(R.string.software_licenses_string));
        licenseBody.setText(readLicenses());
    }

    private String readLicenses() {
        InputStream file = null;
        String license;

        try {
            file = getAssets().open("LICENSES.txt");
            license = IOUtils.toString(new BufferedInputStream(file));
            file.close();
        } catch (IOException e) {
            return "Licenses are broken";
        }

        return license;
    }
}
