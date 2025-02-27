package wannabit.io.cosmostaion.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.common.BitMatrix;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseData;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.dao.Balance;
import wannabit.io.cosmostaion.dao.BondingState;
import wannabit.io.cosmostaion.dao.Reward;
import wannabit.io.cosmostaion.dao.UnBondingState;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.model.type.IrisProposal;
import wannabit.io.cosmostaion.model.type.Proposal;
import wannabit.io.cosmostaion.model.type.Validator;
import wannabit.io.cosmostaion.network.res.ResLcdAccountInfo;
import wannabit.io.cosmostaion.network.res.ResLcdBondings;
import wannabit.io.cosmostaion.network.res.ResLcdUnBondings;

public class WUtil {

    public static Account getAccountFromLcd(long id, ResLcdAccountInfo lcd) {
        Account result = new Account();
        result.id = id;
        if(lcd.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT) ||
                lcd.type.equals(BaseConstant.IRIS_BANK_TYPE_ACCOUNT)) {
            result.address = lcd.value.address;
            result.sequenceNumber = Integer.parseInt(lcd.value.sequence);
            result.accountNumber = Integer.parseInt(lcd.value.account_number);
            return result;
        } else {
            result.address = lcd.value.BaseVestingAccount.BaseAccount.address;
            result.sequenceNumber = Integer.parseInt(lcd.value.BaseVestingAccount.BaseAccount.sequence);
            result.accountNumber = Integer.parseInt(lcd.value.BaseVestingAccount.BaseAccount.account_number);
            return result;
        }
    }

    public static ArrayList<Balance> getBalancesFromLcd(long accountId, ResLcdAccountInfo lcd) {
        long time = System.currentTimeMillis();
        ArrayList<Balance> result = new ArrayList<>();
        if(lcd.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT) ||
                lcd.type.equals(BaseConstant.IRIS_BANK_TYPE_ACCOUNT)) {
            for(Coin coin : lcd.value.coins) {
                Balance temp = new Balance();
                temp.accountId = accountId;
                temp.symbol = coin.denom;
                temp.balance = new BigDecimal(coin.amount);
                temp.fetchTime = time;
                result.add(temp);
            }
            return result;
        } else {
            for(Coin coin : lcd.value.BaseVestingAccount.BaseAccount.coins) {
                Balance temp = new Balance();
                temp.accountId = accountId;
                temp.symbol = coin.denom;
                temp.balance = new BigDecimal(coin.amount);
                temp.fetchTime = time;
                result.add(temp);
            }
            return result;
        }

    }

    public static ArrayList<BondingState> getBondingFromLcds(long accountId, ArrayList<ResLcdBondings> list, BaseChain chain) {
        long time = System.currentTimeMillis();
        ArrayList<BondingState> result = new ArrayList<>();
        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            for(ResLcdBondings val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                BondingState temp = new BondingState(accountId, valAddress, new BigDecimal(val.shares), time);
                result.add(temp);
            }

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            for(ResLcdBondings val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                BondingState temp = new BondingState(accountId, valAddress, new BigDecimal(val.shares).movePointRight(18), time);
                result.add(temp);
            }
        }

        return result;
    }

    public static BondingState getBondingFromLcd(long accountId, ResLcdBondings lcd) {
        String valAddress = "";
        if(!TextUtils.isEmpty(lcd.validator_addr))
            valAddress = lcd.validator_addr;
        if(!TextUtils.isEmpty(lcd.validator_address))
            valAddress = lcd.validator_address;
        return new BondingState(accountId, valAddress, new BigDecimal(lcd.shares), System.currentTimeMillis());
    }

    public static ArrayList<UnBondingState> getUnbondingFromLcds(Context c, BaseChain chain, long accountId, ArrayList<ResLcdUnBondings> list) {
        long time = System.currentTimeMillis();
        ArrayList<UnBondingState> result = new ArrayList<>();
        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            for(ResLcdUnBondings val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                for(ResLcdUnBondings.Entry entry:val.entries) {
                    UnBondingState temp = new UnBondingState(
                            accountId,
                            valAddress,
                            entry.creation_height,
                            WUtil.cosmosTimetoLocalLong(c, entry.completion_time),
                            new BigDecimal(entry.getinitial_balance()),
                            new BigDecimal(entry.getbalance()),
                            time
                    );
                    result.add(temp);
                }
            }

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            for(ResLcdUnBondings val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                UnBondingState temp = new UnBondingState(
                        accountId,
                        valAddress,
                        val.creation_height,
                        WUtil.cosmosTimetoLocalLong(c, val.min_time),
                        new BigDecimal(val.initial_balance.replace("iris","")).movePointRight(18),
                        new BigDecimal(val.balance.replace("iris","")).movePointRight(18),
                        time
                );
                result.add(temp);
            }
        }
        return result;
    }

    //TODO check multi unbonding with one validator
    //TOOD check Chain Type need??
    public static ArrayList<UnBondingState> getUnbondingFromLcd(Context c, long accountId, ResLcdUnBondings lcd) {
        long time = System.currentTimeMillis();
        ArrayList<UnBondingState> result = new ArrayList<>();
        for(ResLcdUnBondings.Entry entry:lcd.entries) {
            String valAddress = "";
            if(!TextUtils.isEmpty(lcd.validator_addr))
                valAddress = lcd.validator_addr;
            if(!TextUtils.isEmpty(lcd.validator_address))
                valAddress = lcd.validator_address;

            UnBondingState temp = new UnBondingState(
                    accountId,
                    valAddress,
                    entry.creation_height,
                    WUtil.cosmosTimetoLocalLong(c, entry.completion_time),
                    new BigDecimal(entry.getinitial_balance()),
                    new BigDecimal(entry.getbalance()),
                    time
            );
            result.add(temp);
        }
        return result;
    }



    public static String prettyPrinter(Object object) {
        String result = "";
        try {
            result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            result = "Print json error";
        }
        return result;
    }


    public static boolean checkPasscodePattern(String pincode) {
        if(pincode.length() != 5)
            return false;
        String regex = "^\\d{4}+[A-Z]{1}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pincode);
        boolean isNormal = m.matches();
        return isNormal;
    }


    public static long cosmosTimetoLocalLong(Context c, String rawValue) {
        try {
            SimpleDateFormat cosmosFormat = new SimpleDateFormat(c.getString(R.string.str_block_time_format));
            cosmosFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return cosmosFormat.parse(rawValue).getTime();
        } catch (Exception e) {

        }
        return 0;
    }


    public static Gson getPresentor(){
//        return new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        return new GsonBuilder().disableHtmlEscaping().create();
    }



    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static int[] Bytearray2intarray(byte[] barray) {
        int[] iarray = new int[barray.length];
        int i = 0;
        for (byte b : barray)
            iarray[i++] = b & 0xff;
        return iarray;
    }

    public static String BytearryToDecimalString(byte[] barray) {
        String result = "";
        int[] iarray = new int[barray.length];
        int i = 0;
        for (byte b : barray) {
            iarray[i++] = b & 0xff;
            result = result + " " + (b & 0xff);
        }
        return result;
    }

    public static byte[] integerToBytes(BigInteger s, int length) {
        byte[] bytes = s.toByteArray();

        if (length < bytes.length) {
            byte[] tmp = new byte[length];
            System.arraycopy(bytes, bytes.length - tmp.length, tmp, 0, tmp.length);
            return tmp;
        } else if (length > bytes.length) {
            byte[] tmp = new byte[length];
            System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
            return tmp;
        }
        return bytes;
    }

    public static String str2Hex(String bin) {
        char[] digital = "0123456789abcdef".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }

    public static String hexToStr(String hex) {
        String digital = "0123456789abcdef";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return new String(bytes);
    }



    //TODO for ssh ignore test
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }


    /**
     * Sorts
     */
    public static void onSortByValidatorName(ArrayList<Validator> validators) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;
                return o1.description.moniker.compareTo(o2.description.moniker);
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortByValidatorPower(ArrayList<Validator> validators) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                if (Double.parseDouble(o1.tokens) > Double.parseDouble(o2.tokens)) return -1;
                else if (Double.parseDouble(o1.tokens) < Double.parseDouble(o2.tokens)) return 1;
                else return 0;
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortByDelegate(final long userId, ArrayList<Validator> validators, final BaseData dao) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                BigDecimal bondingO1 = BigDecimal.ZERO;
                BigDecimal bondingO2 = BigDecimal.ZERO;
                if(dao.onSelectBondingState(userId, o1.operator_address) != null &&
                        dao.onSelectBondingState(userId, o1.operator_address).getBondingAtom(o1) != null) {
                    bondingO1  = dao.onSelectBondingState(userId, o1.operator_address).getBondingAtom(o1) ;
                }
                if(dao.onSelectBondingState(userId, o2.operator_address) != null &&
                        dao.onSelectBondingState(userId, o2.operator_address).getBondingAtom(o2)  != null) {
                    bondingO2  = dao.onSelectBondingState(userId, o2.operator_address).getBondingAtom(o2) ;
                }
                return bondingO2.compareTo(bondingO1);

            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortByReward(ArrayList<Validator> validators, final ArrayList<Reward> rewards) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                BigDecimal rewardO1 = WDp.getValidatorReward(rewards, o1.operator_address);
                BigDecimal rewardO2 = WDp.getValidatorReward(rewards, o2.operator_address);
                return rewardO2.compareTo(rewardO1);
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortingByCommission(ArrayList<Validator> validators) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                if (Float.parseFloat(o1.commission.rate) > Float.parseFloat(o2.commission.rate)) return 1;
                else if (Float.parseFloat(o1.commission.rate) < Float.parseFloat(o2.commission.rate)) return -1;
                else return 0;
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortingProposal(ArrayList<Proposal> proposals) {
        Collections.sort(proposals, new Comparator<Proposal>() {
            @Override
            public int compare(Proposal o1, Proposal o2) {
                if (Integer.parseInt(o1.proposal_id) < Integer.parseInt(o2.proposal_id)) return 1;
                else if (Integer.parseInt(o1.proposal_id) > Integer.parseInt(o2.proposal_id)) return -1;
                else return 0;

            }
        });
    }

    public static void onSortingIrisProposal(ArrayList<IrisProposal> proposals) {
        Collections.sort(proposals, new Comparator<IrisProposal>() {
            @Override
            public int compare(IrisProposal o1, IrisProposal o2) {
                if (Integer.parseInt(o1.value.BasicProposal.proposal_id) < Integer.parseInt(o2.value.BasicProposal.proposal_id)) return 1;
                else if (Integer.parseInt(o1.value.BasicProposal.proposal_id) > Integer.parseInt(o2.value.BasicProposal.proposal_id)) return -1;
                else return 0;

            }
        });
    }





    public static ArrayList<Validator> getIrisTops(ArrayList<Validator> allValidators) {
        ArrayList<Validator> result = new ArrayList<>();
        for(Validator v:allValidators) {
            if(v.status == Validator.BONDED) {
                result.add(v);
            }
        }
        return result;

    }

    public static ArrayList<Validator> getIrisOthers(ArrayList<Validator> allValidators) {
        ArrayList<Validator> result = new ArrayList<>();
        for(Validator v:allValidators) {
            if(v.status != Validator.BONDED) {
                result.add(v);
            }
        }
        return result;
    }


    public static int getCMCId(BaseChain chain) {
        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            return BaseConstant.CMC_ATOM;

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            return BaseConstant.CMC_IRIS;
        }
        return BaseConstant.CMC_ATOM;
    }
}
