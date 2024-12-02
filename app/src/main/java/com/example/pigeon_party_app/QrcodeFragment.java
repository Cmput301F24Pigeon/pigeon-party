package com.example.pigeon_party_app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrcodeFragment extends Fragment {
    private ImageView qrCode;
    private String eventId;

    public static QrcodeFragment newInstance(String eventId){
        QrcodeFragment fragment = new QrcodeFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qrcode_fragment, container, false);

        ImageButton backButton = view.findViewById(R.id.button_back);
        qrCode = view.findViewById(R.id.eventQrcode);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new EditEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        generateQRCode(eventId);
        return view;
    }

    /**
     * This generates a qr code based on a string
     * @param text The text which the qr code will represent (in this case event id)
     */
    //possibly add option to save qr code to phone (implement later)
    private void generateQRCode(String text) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 500, 500);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
