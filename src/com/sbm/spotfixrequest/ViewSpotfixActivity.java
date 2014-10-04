package com.sbm.spotfixrequest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.sbm.R;
import com.sbm.model.Spotfix;
import com.sbm.repository.SpotfixRepository;

import static com.sbm.Global.SPOTFIX_ID;

public class ViewSpotfixActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_spotfix);

        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        TextView textViewEstimatedHours = (TextView) findViewById(R.id.textViewEstimatedHours);
        TextView textViewEstimatedPeople = (TextView) findViewById(R.id.textViewEstimatedPeople);
        TextView textViewFixDate = (TextView) findViewById(R.id.textViewFixDate);

        long spotfix_id = getIntent().getLongExtra(SPOTFIX_ID, 0);
        SpotfixRepository repository = new SpotfixRepository(this);
        Spotfix spotfix = repository.getSpotfix(spotfix_id);

        textViewTitle.setText(spotfix.getTitle());
        textViewDescription.setText(spotfix.getDescription());
        textViewEstimatedHours.setText(Long.valueOf(spotfix.getEstimatedHours()).toString());
        textViewEstimatedPeople.setText(Long.valueOf(spotfix.getEstimatedPeople()).toString());
        textViewFixDate.setText(spotfix.getFixDateInString());
    }

}
