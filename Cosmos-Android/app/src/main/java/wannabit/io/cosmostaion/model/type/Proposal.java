package wannabit.io.cosmostaion.model.type;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Proposal {
    @SerializedName("proposal_id")
    public String proposal_id;

    @SerializedName("proposal_status")
    public String proposal_status;

    @SerializedName("proposal_content")
    public ProposalContent proposal_content;


    public class ProposalContent {
        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public Value value;

    }

    public class Value {
        @SerializedName("title")
        public String title;

        @SerializedName("description")
        public String description;
    }
}

/*
public class Proposal {

    @SerializedName("type")
    public String type;


    @SerializedName("value")
    public Value value;

    public class Value {
        @SerializedName("proposal_id")
        public String proposal_id;

        @SerializedName("title")
        public String title;

        @SerializedName("description")
        public String description;

        @SerializedName("proposal_status")
        public String proposal_status;

        @SerializedName("final_tally_result")
        public FinalTallyResult final_tally_result;

        @SerializedName("submit_time")
        public String submit_time;

        @SerializedName("deposit_end_time")
        public String deposit_end_time;

        @SerializedName("total_deposit")
        public ArrayList<Coin> total_deposit;

        @SerializedName("voting_start_time")
        public String voting_start_time;

        @SerializedName("voting_end_time")
        public String voting_end_time;


    }

    public class FinalTallyResult {

        @SerializedName("yes")
        public String yes;

        @SerializedName("abstain")
        public String abstain;

        @SerializedName("no")
        public String no;

        @SerializedName("no_with_veto")
        public String no_with_veto;
    }


}
*/
