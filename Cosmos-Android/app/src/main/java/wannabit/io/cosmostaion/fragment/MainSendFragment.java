package wannabit.io.cosmostaion.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.MainActivity;
import wannabit.io.cosmostaion.activities.WebActivity;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.dialog.Dialog_AccountShow;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WLog;


public class MainSendFragment extends BaseFragment implements View.OnClickListener {

    private SwipeRefreshLayout  mSwipeRefreshLayout;
    private NestedScrollView    mNestedScrollView;

    private ImageView           mBtnWebDetail, mBtnAddressDetail;
    private ImageView           mKeyState;
    private TextView            mAddress;

    private CardView            mAtomCard, mPhotonCard, mIrisCard, mPriceCard;

    private TextView            mTvAtomTotal, mTvAtomValue, mTvAtomUndelegated,
                                mTvAtomDelegated, mTvAtomUnBonding, mTvAtomRewards;
    private TextView            mTvPhotonTotal, mTvPhotonBalance, mTvPhotonRewards;
    private TextView            mTvIrisTotal, mTvIrisValue, mTvIrisUndelegated,
                                mTvIrisDelegated, mTvIrisUnBonding, mTvIrisRewards;

    private TextView            mPerPrice, mUpDownPrice;
    private ImageView           mUpDownImg;

    private TextView            mInflation, mYield;

    private Button              mGuideBtn, mFaqBtn;



    public static MainSendFragment newInstance(Bundle bundle) {
        MainSendFragment fragment = new MainSendFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_send, container, false);
        mSwipeRefreshLayout     = rootView.findViewById(R.id.layer_refresher);
        mNestedScrollView       = rootView.findViewById(R.id.layer_scrollview);
        mBtnWebDetail           = rootView.findViewById(R.id.web_detail);
        mBtnAddressDetail       = rootView.findViewById(R.id.address_detail);
        mKeyState               = rootView.findViewById(R.id.img_account);
        mAddress                = rootView.findViewById(R.id.account_Address);

        mAtomCard               = rootView.findViewById(R.id.card_atom);
        mTvAtomTotal            = rootView.findViewById(R.id.dash_atom_amount);
        mTvAtomValue            = rootView.findViewById(R.id.dash_atom_value);
        mTvAtomUndelegated      = rootView.findViewById(R.id.dash_atom_undelegate);
        mTvAtomDelegated        = rootView.findViewById(R.id.dash_atom_delegate);
        mTvAtomUnBonding        = rootView.findViewById(R.id.dash_atom_unbonding);
        mTvAtomRewards          = rootView.findViewById(R.id.dash_atom_reward);

        mPhotonCard             = rootView.findViewById(R.id.card_photon);
        mTvPhotonTotal          = rootView.findViewById(R.id.dash_photon_amount);
        mTvPhotonBalance        = rootView.findViewById(R.id.dash_photon_balance);
        mTvPhotonRewards        = rootView.findViewById(R.id.dash_photon_reward);

        mIrisCard               = rootView.findViewById(R.id.card_iris);
        mTvIrisTotal            = rootView.findViewById(R.id.dash_iris_amount);
        mTvIrisValue            = rootView.findViewById(R.id.dash_iris_value);
        mTvIrisUndelegated      = rootView.findViewById(R.id.dash_iris_undelegate);
        mTvIrisDelegated        = rootView.findViewById(R.id.dash_iris_delegate);
        mTvIrisUnBonding        = rootView.findViewById(R.id.dash_iris_unbonding);
        mTvIrisRewards          = rootView.findViewById(R.id.dash_iris_reward);

        mPriceCard              = rootView.findViewById(R.id.card_price);
        mPerPrice               = rootView.findViewById(R.id.dash_per_price);
        mUpDownPrice            = rootView.findViewById(R.id.dash_price_updown_tx);
        mUpDownImg              = rootView.findViewById(R.id.ic_price_updown);

        mInflation              = rootView.findViewById(R.id.dash_inflation);
        mYield                  = rootView.findViewById(R.id.dash_yield);

        mGuideBtn               = rootView.findViewById(R.id.btn_guide);
        mFaqBtn                 = rootView.findViewById(R.id.btn_faq);


        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!getMainActivity().onFetchAccountInfo()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mNestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    if (getMainActivity().mFloatBtn.isShown()) {
                        getMainActivity().mFloatBtn.hide();
                    }
                }
                if (scrollY < oldScrollY) {
                    if (!getMainActivity().mFloatBtn.isShown()) {
                        getMainActivity().mFloatBtn.show();
                    }
                }
            }
        });

        mBtnWebDetail.setOnClickListener(this);
        mBtnAddressDetail.setOnClickListener(this);
        mGuideBtn.setOnClickListener(this);
        mFaqBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onUpdateView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_accounts :
                getMainActivity().onShowTopAccountsView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefreshTab() {
        if(!isAdded()) return;
        mSwipeRefreshLayout.setRefreshing(false);
        onUpdateView();
    }

    private void onUpdateView() {
        if(getMainActivity() == null || getMainActivity().mAccount == null) return;

        mAddress.setText(getMainActivity().mAccount.address);
        mKeyState.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGray0), android.graphics.PorterDuff.Mode.SRC_IN);

        if (getMainActivity().mAccount.baseChain.equals(BaseChain.COSMOS_MAIN.getChain())) {
            mAtomCard.setVisibility(View.VISIBLE);
            mIrisCard.setVisibility(View.GONE);
            if (getMainActivity().mAccount.hasPrivateKey) {
                mKeyState.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAtom), android.graphics.PorterDuff.Mode.SRC_IN);
            }

        } else if (getMainActivity().mAccount.baseChain.equals(BaseChain.IRIS_MAIN.getChain())) {
            mAtomCard.setVisibility(View.GONE);
            mIrisCard.setVisibility(View.VISIBLE);
            if (getMainActivity().mAccount.hasPrivateKey) {
                mKeyState.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorIris), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }


//        WLog.w("mBalances " + getMainActivity().mBalances.size());
//        WLog.w("mBondings " + getMainActivity().mBondings.size());
//        WLog.w("mUnbondings " + getMainActivity().mUnbondings.size());


        if (getMainActivity().mAccount.baseChain.equals(BaseChain.COSMOS_MAIN.getChain())) {
            mTvAtomTotal.setText(WDp.getDpAllAtom(getContext(), getMainActivity().mBalances, getMainActivity().mBondings, getMainActivity().mUnbondings, getMainActivity().mRewards, getMainActivity().mAllValidators, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvAtomUndelegated.setText(WDp.getDpBalance(getContext(), getMainActivity().mBalances, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvAtomDelegated.setText(WDp.getDpAllDelegatedAmount(getContext(), getMainActivity().mBondings, getMainActivity().mAllValidators, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvAtomUnBonding.setText(WDp.getDpAllUnbondingAmount(getContext(), getMainActivity().mUnbondings, getMainActivity().mAllValidators, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvAtomRewards.setText(WDp.getDpAllAtomRewardAmount(getContext(), getMainActivity().mRewards, BaseChain.getChain(getMainActivity().mAccount.baseChain)));

            mTvPhotonTotal.setText(WDp.getDpAllPhoton(getContext(), getMainActivity().mBalances, getMainActivity().mRewards, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvPhotonBalance.setText(WDp.getDpPhotonBalance(getContext(), getMainActivity().mBalances, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvPhotonRewards.setText(WDp.getDpAllPhotonRewardAmount(getContext(), getMainActivity().mRewards, BaseChain.getChain(getMainActivity().mAccount.baseChain)));

            try {
                BigDecimal totalAmount = WDp.getAllAtom(getMainActivity().mBalances, getMainActivity().mBondings, getMainActivity().mUnbondings, getMainActivity().mRewards, getMainActivity().mAllValidators);
                BigDecimal totalPrice = BigDecimal.ZERO;
                if(getBaseDao().getCurrency() != 5) {
                    totalPrice = totalAmount.multiply(new BigDecimal(""+getBaseDao().getLastAtomTic())).movePointLeft(6).setScale(2, RoundingMode.DOWN);
                } else {
                    totalPrice = totalAmount.multiply(new BigDecimal(""+getBaseDao().getLastAtomTic())).movePointLeft(6).setScale(8, RoundingMode.DOWN);
                }
                mTvAtomValue.setText(WDp.getPriceDp(getContext(), totalPrice, getBaseDao().getCurrencySymbol(), getBaseDao().getCurrency()));
                mPerPrice.setText(WDp.getPriceDp(getContext(), new BigDecimal(""+getBaseDao().getLastAtomTic()), getBaseDao().getCurrencySymbol(), getBaseDao().getCurrency()));

                mUpDownPrice.setText(WDp.getPriceUpDown(new BigDecimal(""+getBaseDao().getLastAtomUpDown())));
                if(getBaseDao().getLastAtomUpDown() > 0) {
                    mUpDownImg.setVisibility(View.VISIBLE);
                    mUpDownImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_price_up));
                } else if (getBaseDao().getLastAtomUpDown() < 0){
                    mUpDownImg.setVisibility(View.VISIBLE);
                    mUpDownImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_price_down));
                } else {
                    mUpDownImg.setVisibility(View.GONE);
                }

                mInflation.setText(WDp.getPercentDp(getMainActivity().mInflation.multiply(new BigDecimal("100"))));
                mYield.setText(WDp.getYieldString(getMainActivity().mBondedToken, getMainActivity().mProvisions, BigDecimal.ZERO));


            } catch (Exception e) {
                mTvAtomValue.setText("???");
                mPerPrice.setText("???");
                mUpDownPrice.setText("???");
                mUpDownImg.setVisibility(View.GONE);
            }

        } else if (getMainActivity().mAccount.baseChain.equals(BaseChain.IRIS_MAIN.getChain())) {
            mTvIrisTotal.setText(WDp.getDpAllIris(getContext(), getMainActivity().mBalances, getMainActivity().mBondings, getMainActivity().mUnbondings, getMainActivity().mIrisReward, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvIrisUndelegated.setText(WDp.getDpBalance(getContext(), getMainActivity().mBalances, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvIrisDelegated.setText(WDp.getDpAllDelegatedAmount(getContext(), getMainActivity().mBondings, getMainActivity().mAllValidators, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            mTvIrisUnBonding.setText(WDp.getDpAllUnbondingAmount(getContext(), getMainActivity().mUnbondings, getMainActivity().mAllValidators, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            if(getMainActivity().mIrisReward != null) {
                mTvIrisRewards.setText(WDp.getDpAllIrisRewardAmount(getContext(), getMainActivity().mIrisReward, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            } else {
                mTvIrisRewards.setText(WDp.getDpAmount(getContext(), BigDecimal.ZERO, 6, BaseChain.getChain(getMainActivity().mAccount.baseChain)));
            }

            try {
                BigDecimal totalAmount = WDp.getAllIris(getMainActivity().mBalances, getMainActivity().mBondings, getMainActivity().mUnbondings, getMainActivity().mIrisReward);
                BigDecimal totalPrice = BigDecimal.ZERO;
                if(getBaseDao().getCurrency() != 5) {
                    totalPrice = totalAmount.multiply(new BigDecimal(""+getBaseDao().getLastIrisTic())).movePointLeft(18).setScale(2, RoundingMode.DOWN);
                } else {
                    totalPrice = totalAmount.multiply(new BigDecimal(""+getBaseDao().getLastIrisTic())).movePointLeft(18).setScale(8, RoundingMode.DOWN);
                }
                mTvIrisValue.setText(WDp.getPriceDp(getContext(), totalPrice, getBaseDao().getCurrencySymbol(), getBaseDao().getCurrency()));
                mPerPrice.setText(WDp.getPriceDp(getContext(), new BigDecimal(""+getBaseDao().getLastIrisTic()), getBaseDao().getCurrencySymbol(), getBaseDao().getCurrency()));

                mUpDownPrice.setText(WDp.getPriceUpDown(new BigDecimal(""+getBaseDao().getLastIrisUpDown())));
                if(getBaseDao().getLastIrisUpDown() > 0) {
                    mUpDownImg.setVisibility(View.VISIBLE);
                    mUpDownImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_price_up));
                } else if (getBaseDao().getLastIrisUpDown() < 0){
                    mUpDownImg.setVisibility(View.VISIBLE);
                    mUpDownImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_price_down));
                } else {
                    mUpDownImg.setVisibility(View.GONE);
                }

                mInflation.setText(WDp.getPercentDp(new BigDecimal("4")));
                mYield.setText(WDp.getIrisYieldString(getMainActivity().mIrisPool, BigDecimal.ZERO));


            } catch (Exception e) {
                mTvIrisValue.setText("???");
                mPerPrice.setText("???");
                mUpDownPrice.setText("???");
                mUpDownImg.setVisibility(View.GONE);
            }
        }
    }

    public MainActivity getMainActivity() {
        return (MainActivity)getBaseActivity();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(mBtnAddressDetail)) {
            Bundle bundle = new Bundle();
            bundle.putString("address", getMainActivity().mAccount.address);
            if(TextUtils.isEmpty(getMainActivity().mAccount.nickName)) bundle.putString("title", getString(R.string.str_my_wallet) + getMainActivity().mAccount.id);
            else bundle.putString("title", getMainActivity().mAccount.nickName);
            Dialog_AccountShow show = Dialog_AccountShow.newInstance(bundle);
            show.setCancelable(true);
            show.show(getFragmentManager().beginTransaction(), "dialog");

        } else if (v.equals(mBtnWebDetail)) {
            Intent webintent = new Intent(getMainActivity(), WebActivity.class);
            webintent.putExtra("address", getMainActivity().mAccount.address);
            webintent.putExtra("goMain", false);
            startActivity(webintent);

        } else if (v.equals(mGuideBtn)) {
            if(Locale.getDefault().getLanguage().toLowerCase().equals("ko")) {
                Intent guideIntent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://www.cosmostation.io/files/guide_KO.pdf"));
                startActivity(guideIntent);
            } else {
                Intent guideIntent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://www.cosmostation.io/files/guide_EN.pdf"));
                startActivity(guideIntent);
            }

        } else if (v.equals(mFaqBtn)) {
            if(Locale.getDefault().getLanguage().toLowerCase().equals("ko")) {
                Intent guideIntent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://guide.cosmostation.io/app_wallet_ko.html"));
                startActivity(guideIntent);
            } else {
                Intent guideIntent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://guide.cosmostation.io/app_wallet_en.html"));
                startActivity(guideIntent);
            }
        }

    }
}

