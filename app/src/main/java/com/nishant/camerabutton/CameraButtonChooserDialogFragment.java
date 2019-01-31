package com.nishant.camerabutton;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/*
 * Created by Nishant on 31-03-2015.
 */
public class CameraButtonChooserDialogFragment extends DialogFragment {

    private ChooserDialogClickListener mDialogClickListener;
    private List<ResolveInfo> resolveInfos;

    public CameraButtonChooserDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setAdapter(new ChooserAdapter(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        mDialogClickListener.onDialogItemClick(position);
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setResolveInfoData(List<ResolveInfo> resolveInfos) {
        this.resolveInfos = resolveInfos;
    }

    public void setDialogClickListener(ChooserDialogClickListener listemer) {
        mDialogClickListener = listemer;
    }

    public interface ChooserDialogClickListener {
        void onDialogItemClick(int position);
    }

    private class ChooserAdapter extends ArrayAdapter<ResolveInfo> {

        public ChooserAdapter() {
            super(getActivity(), R.layout.item_camerabutton_chooserdialog_row, resolveInfos);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_camerabutton_chooserdialog_row, parent, false);
            }

            if (getItem(position) == null) {
                convertView.findViewById(R.id.item_camerabutton_chooserdialog_layout).setVisibility(View.GONE);
                convertView.findViewById(R.id.item_camerabutton_chooserdialog_separator).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.item_camerabutton_chooserdialog_layout).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.item_camerabutton_chooserdialog_separator).setVisibility(View.GONE);

                ResolveInfo resolveInfo = getItem(position);

                if (resolveInfo != null) {
                    ((TextView) convertView.findViewById(R.id.item_camerabutton_chooserdialog_tv_label)).setText(resolveInfo.loadLabel(getActivity().getPackageManager()));
                    ((ImageView) convertView.findViewById(R.id.item_camerabutton_chooserdialog_iv_icon)).setImageDrawable(resolveInfo.loadIcon(getActivity().getPackageManager()));
                }
            }

            return convertView;
        }
    }

}
