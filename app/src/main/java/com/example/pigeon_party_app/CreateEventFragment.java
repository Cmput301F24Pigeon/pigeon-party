package com.example.pigeon_party_app;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEventFragment newInstance(String param1, String param2) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private ImageView qrCode;
    User user = new User("john doe", "johndoe@gmail.com");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        //add an addimage button later
        Button createEventButton = view.findViewById(R.id.button_create_event);
        ImageButton backButton = view.findViewById(R.id.button_back);
        EditText eventTitle = view.findViewById(R.id.edit_event_title);
        //String eventAddress = user.facility.getFacilityAddress(); //need to figure this out still
        EditText eventDetails = view.findViewById(R.id.edit_event_details);
        EditText waitlistCap = view.findViewById(R.id.edit_waitlist_cap);
        Switch requiresLocation = view.findViewById(R.id.switch_require_location);
        qrCode = view.findViewById(R.id.eventQrcode);

        createEventButton.setOnClickListener(v -> {
            String eventId = UUID.randomUUID().toString();
            generateQRCode(eventId);

        });

            //Event event = new Event(eventId,eventTitle.getText().toString(), null , parseInt(waitlistCap.getText().toString()), eventDetails.getText().toString(), null, requiresLocation.isChecked());
            //turn event id into qr code

        backButton.setOnClickListener(v -> {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new OrganizerFragment()) // Change fragment_container to your actual container
                .addToBackStack(null)
                .commit();
    });


        return view;
    }
    private void generateQRCode(String text){
    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
        // Generate a QR code with size 400x400 pixels
        Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
        qrCode.setImageBitmap(bitmap); // Display the QR code in the ImageView
    } catch (WriterException e) {
        e.printStackTrace();
    }
}
}