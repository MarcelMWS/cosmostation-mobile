package wannabit.io.cosmostaion.task.FetchTask;

import java.util.ArrayList;

import retrofit2.Response;
import wannabit.io.cosmostaion.base.BaseApplication;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.network.ApiClient;
import wannabit.io.cosmostaion.network.res.ResLcdUnBondings;
import wannabit.io.cosmostaion.task.CommonTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.utils.WLog;
import wannabit.io.cosmostaion.utils.WUtil;

public class UnBondingStateTask extends CommonTask {

    private Account mAccount;

    public UnBondingStateTask(BaseApplication app, TaskListener listener, Account account) {
        super(app, listener);
        this.mAccount           = account;
        this.mResult.taskType   = BaseConstant.TASK_FETCH_UNBONDING_STATE;
    }

    @Override
    protected TaskResult doInBackground(String... strings) {
        try {
            if (mAccount.baseChain.equals(BaseChain.COSMOS_MAIN.getChain())) {
                Response<ArrayList<ResLcdUnBondings>> response = ApiClient.getCosmosChain(mApp).getUnBondingList(mAccount.address).execute();
                if(response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        mApp.getBaseDao().onUpdateUnbondingStates(mAccount.id, WUtil.getUnbondingFromLcds(mApp, BaseChain.COSMOS_MAIN, mAccount.id, response.body()));
                    } else {
                        mApp.getBaseDao().onDeleteUnbondingStates(mAccount.id);
                    }
                }
            } else if (mAccount.baseChain.equals(BaseChain.IRIS_MAIN.getChain())) {
                Response<ArrayList<ResLcdUnBondings>> response = ApiClient.getIrisChain(mApp).getUnBondingList(mAccount.address).execute();
                if(response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        mApp.getBaseDao().onUpdateUnbondingStates(mAccount.id, WUtil.getUnbondingFromLcds(mApp, BaseChain.IRIS_MAIN, mAccount.id, response.body()));
                    } else {
                        mApp.getBaseDao().onDeleteUnbondingStates(mAccount.id);
                    }
                }
            }
            mResult.isSuccess = true;

        } catch (Exception e) {
            WLog.w("UnBondingStateTask Error " + e.getMessage());
        }
        return mResult;
    }
}
