/**
 *
 * Copyright 2014 Cody Huzarski (chuzarski.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *
 */

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        licenseBody = (TextView) findViewById(R.id.license_body);
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
