package wannabit.io.cosmostaion.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseActivity;

public class Dialog_ShareType extends DialogFragment {

    private LinearLayout mQR, mText;

    public static Dialog_ShareType newInstance(Bundle bundle) {
        Dialog_ShareType frag = new Dialog_ShareType();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_share_type, null);

        mQR = view.findViewById(R.id.share_qr);
        mText = view.findViewById(R.id.share_tx);

        mQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("coin", BaseConstant.COSMOS_ATOM);
//                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, resultIntent);
//                getDialog().dismiss();
                ((BaseActivity)getActivity()).onShare(false);
                getDialog().dismiss();
            }
        });

        mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("coin", BaseConstant.COSMOS_PHOTON);
//                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, resultIntent);
//                getDialog().dismiss();
                ((BaseActivity)getActivity()).onShare(true);
                getDialog().dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }
}