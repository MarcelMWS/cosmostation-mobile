package wannabit.io.cosmostaion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.SendActivity;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.dao.Balance;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WLog;

public class SendStep4Fragment extends BaseFragment implements View.OnClickListener {

    private TextView        mSendAmount;
    private TextView        mFeeAmount;
    private TextView        mTotalSpendAmount, mTotalPrice;
    private TextView        mCurrentBalance, mRemainingBalance, mRemainingPrice;
    private TextView        mRecipientAddress, mMemo;
    private Button          mBeforeBtn, mConfirmBtn;

    public static SendStep4Fragment newInstance(Bundle bundle) {
        SendStep4Fragment fragment = new SendStep4Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView       = inflater.inflate(R.layout.fragment_send_step4, container, false);
        mSendAmount         = rootView.findViewById(R.id.send_atom);
        mFeeAmount          = rootView.findViewById(R.id.send_fees);
        mTotalSpendAmount   = rootView.findViewById(R.id.spend_total);
        mTotalPrice         = rootView.findViewById(R.id.spend_total_price);
        mCurrentBalance     = rootView.findViewById(R.id.current_available_atom);
        mRemainingBalance   = rootView.findViewById(R.id.remaining_available_atom);
        mRemainingPrice     = rootView.findViewById(R.id.remaining_price);
        mRecipientAddress   = rootView.findViewById(R.id.recipient_address);
        mMemo               = rootView.findViewById(R.id.memo);


        mBeforeBtn = rootView.findViewById(R.id.btn_before);
        mConfirmBtn = rootView.findViewById(R.id.btn_confirm);
        mBeforeBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onRefreshTab() {
        BigDecimal toSendAtom   = new BigDecimal(getSActivity().mTargetCoins.get(0).amount);
        BigDecimal feeAtom      = new BigDecimal(getSActivity().mTargetFee.amount.get(0).amount);
        BigDecimal currentAvai  = getSActivity().mAccount.getAtomBalance();

        mSendAmount.setText(WDp.getDpAmount(getContext(), toSendAtom, 6, BaseChain.getChain(getSActivity().mAccount.baseChain)));
        mFeeAmount.setText(WDp.getDpAmount(getContext(), feeAtom, 6, BaseChain.getChain(getSActivity().mAccount.baseChain)));
        mTotalSpendAmount.setText(WDp.getDpAmount(getContext(), feeAtom.add(toSendAtom), 6, BaseChain.getChain(getSActivity().mAccount.baseChain)));
        BigDecimal spendTotal = BigDecimal.ZERO;
        if(getBaseDao().getCurrency() != 5) {
            spendTotal = feeAtom.add(toSendAtom).multiply(new BigDecimal(""+getBaseDao().getLastAtomTic())).divide(new BigDecimal("1000000"), 2, RoundingMode.DOWN);
        } else {
            spendTotal = feeAtom.add(toSendAtom).multiply(new BigDecimal(""+getBaseDao().getLastAtomTic())).divide(new BigDecimal("1000000"), 8, RoundingMode.DOWN);
        }
        mTotalPrice.setText(WDp.getPriceApproximatelyDp(getSActivity(), spendTotal, getBaseDao().getCurrencySymbol(), getBaseDao().getCurrency()));

        mCurrentBalance.setText(WDp.getDpAmount(getContext(), currentAvai, 6, BaseChain.getChain(getSActivity().mAccount.baseChain)));
        mRemainingBalance.setText(WDp.getDpAmount(getContext(), currentAvai.subtract(toSendAtom).subtract(feeAtom), 6, BaseChain.getChain(getSActivity().mAccount.baseChain)));
        BigDecimal remainTotal = BigDecimal.ZERO;
        if(getBaseDao().getCurrency() != 5) {
            remainTotal = currentAvai.subtract(toSendAtom).subtract(feeAtom).multiply(new BigDecimal(""+getBaseDao().getLastAtomTic())).divide(new BigDecimal("1000000"), 2, RoundingMode.DOWN);
        } else {
            remainTotal = currentAvai.subtract(toSendAtom).subtract(feeAtom).multiply(new BigDecimal(""+getBaseDao().getLastAtomTic())).divide(new BigDecimal("1000000"), 8, RoundingMode.DOWN);
        }
        mRemainingPrice.setText(WDp.getPriceApproximatelyDp(getSActivity(), remainTotal, getBaseDao().getCurrencySymbol(), getBaseDao().getCurrency()));

        mRecipientAddress.setText(getSActivity().mTagetAddress);
        mMemo.setText(getSActivity().mTargetMemo);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(mBeforeBtn)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mConfirmBtn)) {
            getSActivity().onStartSend();

        }

    }

    private SendActivity getSActivity() {
        return (SendActivity)getBaseActivity();
    }
}
