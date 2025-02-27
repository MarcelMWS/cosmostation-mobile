//
//  GenTxResultViewController.swift
//  Cosmostation
//
//  Created by yongjoo on 12/04/2019.
//  Copyright © 2019 wannabit. All rights reserved.
//

import UIKit
import Alamofire
import SafariServices

class GenTxResultViewController: BaseViewController {
    @IBOutlet weak var sendResultView: CardView!
    @IBOutlet weak var sendResultType: UILabel!
    @IBOutlet weak var sendResultHash: UILabel!
    @IBOutlet weak var sendResultTime: UILabel!
    @IBOutlet weak var sendResultBlock: UILabel!
    @IBOutlet weak var sendResultAmount: UILabel!
    @IBOutlet weak var sendResultFee: UILabel!
    @IBOutlet weak var sendResultToAddress: UILabel!
    @IBOutlet weak var sendResultMemo: UILabel!
    
    @IBOutlet weak var delegateResultView: CardView!
    @IBOutlet weak var delegateResultType: UILabel!
    @IBOutlet weak var delegateResultHash: UILabel!
    @IBOutlet weak var delegateResultTime: UILabel!
    @IBOutlet weak var delegateResultBlock: UILabel!
    @IBOutlet weak var delegateResultAmount: UILabel!
    @IBOutlet weak var delegateResultFee: UILabel!
    @IBOutlet weak var delegateResultValAddress: UILabel!
    @IBOutlet weak var delegateResultMemo: UILabel!
    
    @IBOutlet weak var undelegateResultView: CardView!
    @IBOutlet weak var undelegateResultType: UILabel!
    @IBOutlet weak var undelegateResultHash: UILabel!
    @IBOutlet weak var undelegateResultTime: UILabel!
    @IBOutlet weak var undelegateResultBlock: UILabel!
    @IBOutlet weak var undelegateResultAmount: UILabel!
    @IBOutlet weak var undelegateResultFee: UILabel!
    @IBOutlet weak var undelegateResultValAddress: UILabel!
    @IBOutlet weak var undelegateResultMemo: UILabel!
    
    @IBOutlet weak var redelegateResultView: CardView!
    @IBOutlet weak var redelegateResultType: UILabel!
    @IBOutlet weak var redelegateResultHash: UILabel!
    @IBOutlet weak var redelegateResultTime: UILabel!
    @IBOutlet weak var redelegateResultBlock: UILabel!
    @IBOutlet weak var redelegateResultAmount: UILabel!
    @IBOutlet weak var redelegateResultFee: UILabel!
    @IBOutlet weak var redelegateResultFromValAddress: UILabel!
    @IBOutlet weak var redelegateResultToValAddress: UILabel!
    @IBOutlet weak var redelegateResultMemo: UILabel!
    
    @IBOutlet weak var rewardResultView: CardView!
    @IBOutlet weak var rewardResultType: UILabel!
    @IBOutlet weak var rewardResultHash: UILabel!
    @IBOutlet weak var rewardResultTime: UILabel!
    @IBOutlet weak var rewardResultBlock: UILabel!
    @IBOutlet weak var rewardResultFee: UILabel!
    @IBOutlet weak var rewardResultFromValAddress: UILabel!
    @IBOutlet weak var rewardResultMemo: UILabel!
    
    @IBOutlet weak var addressChangeResultView: CardView!
    @IBOutlet weak var addressChangeResultType: UILabel!
    @IBOutlet weak var addressChangeResultHash: UILabel!
    @IBOutlet weak var addressChangeResultTime: UILabel!
    @IBOutlet weak var addressChangeResultBlock: UILabel!
    @IBOutlet weak var addressChangeResultFee: UILabel!
    @IBOutlet weak var addressChangeResultAddress: UILabel!
    @IBOutlet weak var addressChangeResultMemo: UILabel!
    
    @IBOutlet weak var reInvestResultView: CardView!
    @IBOutlet weak var reInvestResultType: UILabel!
    @IBOutlet weak var reInvestResultHash: UILabel!
    @IBOutlet weak var reInvestResultTime: UILabel!
    @IBOutlet weak var reInvestResultBlock: UILabel!
    @IBOutlet weak var reInvestResultRewardAmount: UILabel!
    @IBOutlet weak var reInvestResultDelegateAmount: UILabel!
    @IBOutlet weak var reInvestResultFee: UILabel!
    @IBOutlet weak var reInvestValidatorAddress: UILabel!
    @IBOutlet weak var reInvestResultMemo: UILabel!
    
    
    
    
    var response:[String:Any]?

    @IBOutlet weak var txResultTitleLabel: UILabel!
    @IBOutlet weak var actionLayer: UIStackView!
    @IBOutlet weak var dismissBtn: UIButton!
    
    @IBOutlet weak var errorCardView: CardView!
    @IBOutlet weak var errorCode: UILabel!
    
    @IBOutlet weak var loadingView: UIView!
    @IBOutlet weak var loadingImgs: LoadingImageView!
    
    var mTxType: String?
    var mTxHash: String?
    
    var mTxInfo: TxInfo?
    var mStakTxInfo: StakeTxInfo?
//    var mBlockInfo: BlockInfo?
//
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        guard let txType = response?["type"] as? String, let txHash = response?["txhash"] as? String  else {
            self.onStartMainTab()
            return
        }
        mTxType = txType
        mTxHash = txHash

        if let code = response?["code"] as? Int {
//            print("code " , code)
            onShowErrorView(code)
            return
        }

        self.loadingImgs.onStartAnimation()
        self.onFetchTx(mTxHash!)
        
    }
    
    func onShowErrorView(_ code: Int) {
//        print("onShowErrorView")
        self.txResultTitleLabel.text = "Transaction Failed"
        self.txResultTitleLabel.textColor = UIColor.init(hexString: "f31963")
        self.errorCode.text =  "error code : " + String(code) + "\n" + ((response?["raw_log"] as? String)!)
        self.loadingView.isHidden = true
        self.errorCardView.isHidden = false
    }
    
    
    func onTxDetailView() {
//        print("onTxDetailView")
        if (mTxType == COSMOS_MSG_TYPE_DELEGATE) {
            self.delegateResultView.isHidden = false
            self.loadingView.isHidden = true
            
            delegateResultType.text = NSLocalizedString("tx_delegate", comment: "")
            delegateResultHash.text = mStakTxInfo?.txhash
            delegateResultBlock.text = mStakTxInfo?.height
            delegateResultTime.text = WUtils.txTimetoString(input: (mStakTxInfo?.txTime)!)
            
            delegateResultAmount.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.msg[0].value.amount?.amount)!, delegateResultAmount.font, 6)
            delegateResultFee.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.fee.amount[0].amount)!, delegateResultFee.font, 6)
            delegateResultValAddress.text = mStakTxInfo?.tx.value.msg[0].value.validator_address
            delegateResultValAddress.adjustsFontSizeToFitWidth = true
            delegateResultMemo.text = mStakTxInfo?.tx.value.memo
            
            
        } else if (mTxType == COSMOS_MSG_TYPE_UNDELEGATE2) {
            self.undelegateResultView.isHidden = false
            self.loadingView.isHidden = true
            
            undelegateResultType.text = NSLocalizedString("tx_undelegate", comment: "")
            undelegateResultHash.text = mStakTxInfo?.txhash
            undelegateResultBlock.text = mStakTxInfo?.height
            undelegateResultTime.text = WUtils.txTimetoString(input: (mStakTxInfo?.txTime)!)
            
            undelegateResultAmount.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.msg[0].value.amount?.amount)!, undelegateResultAmount.font, 6)
            undelegateResultFee.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.fee.amount[0].amount)!, undelegateResultFee.font, 6)
            undelegateResultValAddress.text = mStakTxInfo?.tx.value.msg[0].value.validator_address
            undelegateResultValAddress.adjustsFontSizeToFitWidth = true
            undelegateResultMemo.text = mStakTxInfo?.tx.value.memo
            
            
        } else if (mTxType == COSMOS_MSG_TYPE_REDELEGATE2) {
            self.redelegateResultView.isHidden = false
            self.loadingView.isHidden = true
            
            redelegateResultType.text = NSLocalizedString("tx_redelegate", comment: "")
            redelegateResultHash.text = mStakTxInfo?.txhash
            redelegateResultBlock.text = mStakTxInfo?.height
            redelegateResultTime.text = WUtils.txTimetoString(input: (mStakTxInfo?.txTime)!)
            
            redelegateResultAmount.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.msg[0].value.amount?.amount)!, redelegateResultAmount.font, 6)
            redelegateResultFee.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.fee.amount[0].amount)!, redelegateResultFee.font, 6)
            redelegateResultFromValAddress.text = mStakTxInfo?.tx.value.msg[0].value.validator_src_address
            redelegateResultFromValAddress.adjustsFontSizeToFitWidth = true
            redelegateResultToValAddress.text = mStakTxInfo?.tx.value.msg[0].value.validator_dst_address
            redelegateResultToValAddress.adjustsFontSizeToFitWidth = true
            redelegateResultMemo.text = mStakTxInfo?.tx.value.memo
            
        } else if (mTxType == COSMOS_MSG_TYPE_TRANSFER2) {
            self.sendResultView.isHidden = false
            self.loadingView.isHidden = true
            
            sendResultType.text = NSLocalizedString("tx_transfer", comment: "")
            sendResultHash.text = mTxInfo?.txhash
            sendResultBlock.text = mTxInfo?.height
            sendResultTime.text = WUtils.txTimetoString(input: (mTxInfo?.txTime)!)
            
            sendResultAmount.attributedText = WUtils.displayAmout((mTxInfo?.tx.value.msg[0].value.amount![0].amount)!, sendResultAmount.font, 6)
            sendResultFee.attributedText = WUtils.displayAmout((mTxInfo?.tx.value.fee.amount[0].amount)!, sendResultFee.font, 6)
            sendResultToAddress.text = mTxInfo?.tx.value.msg[0].value.to_address
            sendResultToAddress.adjustsFontSizeToFitWidth = true
            sendResultMemo.text = mTxInfo?.tx.value.memo
            
            
        } else if (mTxType == COSMOS_MSG_TYPE_WITHDRAW_DEL) {
            self.rewardResultView.isHidden = false
            self.loadingView.isHidden = true
            
            rewardResultType.text = NSLocalizedString("tx_get_reward", comment: "")
            rewardResultHash.text = mTxInfo?.txhash
            rewardResultBlock.text = mTxInfo?.height
            rewardResultTime.text = WUtils.txTimetoString(input: (mTxInfo?.txTime)!)
            
            rewardResultFee.attributedText = WUtils.displayAmout((mTxInfo?.tx.value.fee.amount[0].amount)!, rewardResultFee.font, 6)
            var rewardValAddress = ""
            if((mTxInfo?.tx.value.msg.count)! > 1) {
                rewardValAddress = (mTxInfo?.tx.value.msg[0].value.validator_address)! + " \n + " + String((mTxInfo?.tx.value.msg.count)! - 1)
            } else {
                rewardValAddress = (mTxInfo?.tx.value.msg[0].value.validator_address)!
            }
            rewardResultFromValAddress.text = rewardValAddress
            rewardResultFromValAddress.adjustsFontSizeToFitWidth = true
            rewardResultMemo.text = mTxInfo?.tx.value.memo
            
            
        } else if (mTxType == COSMOS_MSG_TYPE_WITHDRAW_MIDIFY) {
            self.addressChangeResultView.isHidden = false
            self.loadingView.isHidden = true
            
            addressChangeResultType.text = NSLocalizedString("tx_change_reward_address", comment: "")
            addressChangeResultHash.text = mTxInfo?.txhash
            addressChangeResultBlock.text = mTxInfo?.height
            addressChangeResultTime.text = WUtils.txTimetoString(input: (mTxInfo?.txTime)!)
            
            addressChangeResultFee.attributedText = WUtils.displayAmout((mTxInfo?.tx.value.fee.amount[0].amount)!, addressChangeResultFee.font, 6)
            addressChangeResultAddress.text = mTxInfo?.tx.value.msg[0].value.withdraw_address
            addressChangeResultAddress.adjustsFontSizeToFitWidth = true
            addressChangeResultMemo.text = mTxInfo?.tx.value.memo
            
        }  else if (mTxType == COSMOS_MULTI_MSG_TYPE_REINVEST) {
            self.reInvestResultView.isHidden = false
            self.loadingView.isHidden = true
            
            reInvestResultType.text = NSLocalizedString("tx_reinvest", comment: "")
            reInvestResultHash.text = mStakTxInfo?.txhash
            reInvestResultTime.text = mStakTxInfo?.height
            reInvestResultBlock.text = WUtils.txTimetoString(input: (mStakTxInfo?.txTime)!)
            
            reInvestResultDelegateAmount.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.msg[1].value.amount?.amount)!, reInvestResultDelegateAmount.font, 6)
            reInvestResultFee.attributedText = WUtils.displayAmout((mStakTxInfo?.tx.value.fee.amount[0].amount)!, reInvestResultFee.font, 6)
            reInvestValidatorAddress.text = mStakTxInfo?.tx.value.msg[0].value.validator_address
            reInvestValidatorAddress.adjustsFontSizeToFitWidth = true
            reInvestResultMemo.text = mStakTxInfo?.tx.value.memo
        }

        self.actionLayer.isHidden = false
        self.dismissBtn.isHidden = true
    }

    
    
    @IBAction func onClickDismiss(_ sender: UIButton) {
        self.onStartMainTab()
    }
    
    @IBAction func onClickExplorer(_ sender: UIButton) {
        if (mTxType == COSMOS_MSG_TYPE_DELEGATE ||
                mTxType == COSMOS_MSG_TYPE_UNDELEGATE2 ||
                mTxType == COSMOS_MSG_TYPE_REDELEGATE2 ||
                mTxType == COSMOS_MULTI_MSG_TYPE_REINVEST) {
            guard let url = URL(string: "https://www.mintscan.io/txs/" + mStakTxInfo!.txhash) else { return }
            let safariViewController = SFSafariViewController(url: url)
            present(safariViewController, animated: true, completion: nil)

        } else {
            guard let url = URL(string: "https://www.mintscan.io/txs/" + mTxInfo!.txhash) else { return }
            let safariViewController = SFSafariViewController(url: url)
            present(safariViewController, animated: true, completion: nil)

        }
    }
    
    @IBAction func onClickShare(_ sender: UIButton) {
        if (mTxType == COSMOS_MSG_TYPE_DELEGATE ||
                mTxType == COSMOS_MSG_TYPE_UNDELEGATE2 ||
                mTxType == COSMOS_MSG_TYPE_REDELEGATE2 ||
                mTxType == COSMOS_MULTI_MSG_TYPE_REINVEST) {
            let text = "https://www.mintscan.io/txs/" + mStakTxInfo!.txhash
            let textToShare = [ text ]
            let activityViewController = UIActivityViewController(activityItems: textToShare, applicationActivities: nil)
            activityViewController.popoverPresentationController?.sourceView = self.view
            self.present(activityViewController, animated: true, completion: nil)
        } else {
            let text = "https://www.mintscan.io/txs/" + mTxInfo!.txhash
            let textToShare = [ text ]
            let activityViewController = UIActivityViewController(activityItems: textToShare, applicationActivities: nil)
            activityViewController.popoverPresentationController?.sourceView = self.view
            self.present(activityViewController, animated: true, completion: nil)
            
        }
    }
    
    @IBAction func onClickOK(_ sender: UIButton) {
        self.onStartMainTab()
    }
    
    func onShowMoreWait() {
        let noticeAlert = UIAlertController(title: NSLocalizedString("more_wait_title", comment: ""), message: NSLocalizedString("more_wait_msg", comment: ""), preferredStyle: .alert)
        noticeAlert.addAction(UIAlertAction(title: NSLocalizedString("close", comment: ""), style: .default, handler: { [weak noticeAlert] (_) in
            self.dismiss(animated: true, completion: nil)
            self.onStartMainTab()
        }))
        noticeAlert.addAction(UIAlertAction(title: NSLocalizedString("wait", comment: ""), style: .default, handler: { [weak noticeAlert] (_) in
            self.fetchCnt = 10
            self.onFetchTx(self.mTxHash!)
        }))
        self.present(noticeAlert, animated: true) {
            let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.dismissAlertController))
            noticeAlert.view.superview?.subviews[0].addGestureRecognizer(tapGesture)
        }
    }
    
    @objc func dismissAlertController(){
        self.dismiss(animated: true, completion: nil)
    }
    
    
    var fetchCnt = 10
    func onFetchTx(_ txHash: String) {
        let url = CSS_LCD_URL_TX + txHash
        let request = Alamofire.request(url, method: .get, parameters: [:], encoding: URLEncoding.default, headers: [:])
        request.responseJSON { (response) in
            switch response.result {
            case .success(let res):
                if(SHOW_LOG) { print("onFetchTx ", res) }
                guard let info = res as? [String : Any], info["error"] == nil else {
                    self.fetchCnt = self.fetchCnt - 1
                    if(self.fetchCnt > 0) {
                        DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(6000), execute: {
                            self.onFetchTx(txHash)
                        })
                    } else {
                        self.onShowMoreWait()
                    }
                    return
                }
                if (self.mTxType == COSMOS_MSG_TYPE_DELEGATE ||
                        self.mTxType == COSMOS_MSG_TYPE_UNDELEGATE2 ||
                        self.mTxType == COSMOS_MSG_TYPE_REDELEGATE2 ||
                        self.mTxType == COSMOS_MULTI_MSG_TYPE_REINVEST) {
                    self.mStakTxInfo = StakeTxInfo.init(info)
                } else {
                    self.mTxInfo = TxInfo.init(info)
                }
                
            case .failure(let error):
                if(SHOW_LOG) {
                    print("onFetchTx ", error)
                }
            }
            self.onTxDetailView()
        }
        
    }
}
