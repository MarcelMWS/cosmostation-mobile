package wannabit.io.cosmostaion.network;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import wannabit.io.cosmostaion.model.type.IrisProposal;
import wannabit.io.cosmostaion.model.type.Proposal;
import wannabit.io.cosmostaion.model.type.Validator;
import wannabit.io.cosmostaion.network.res.ResLcdAccountInfo;
import wannabit.io.cosmostaion.network.res.ResLcdBondings;
import wannabit.io.cosmostaion.network.res.ResLcdIrisPool;
import wannabit.io.cosmostaion.network.res.ResLcdIrisReward;
import wannabit.io.cosmostaion.network.res.ResLcdUnBondings;

public interface IrisChain {

    @GET("/stake/validators")
    Call<ArrayList<Validator>> getValidatorList(@Query("page") String page, @Query("size") String size);



    @GET("/bank/accounts/{address}")
    Call<ResLcdAccountInfo> getBankInfo(@Path("address") String address);


    @GET("/stake/delegators/{address}/delegations")
    Call<ArrayList<ResLcdBondings>> getBondingList(@Path("address") String address);

    @GET("/stake/delegators/{address}/unbonding-delegations")
    Call<ArrayList<ResLcdUnBondings>> getUnBondingList(@Path("address") String address);

    @GET("/distribution/{address}/rewards")
    Call<ResLcdIrisReward> getRewardsInfo(@Path("address") String address);






    @GET("/stake/pool")
    Call<ResLcdIrisPool> getIrisPool();


    @GET("/gov/proposals")
    Call<ArrayList<IrisProposal>> getProposalList();
}
