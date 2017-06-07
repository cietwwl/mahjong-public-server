package com.randioo.mahjong_public_server.module.money.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.module.login.service.LoginService;
import com.randioo.mahjong_public_server.protocol.Error.ErrorCode;
import com.randioo.mahjong_public_server.protocol.MoneyExchange.MoneyExchangeResponse;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.mahjong_public_server.util.HttpConnection;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Service("moneyExchangeService")
public class MoneyExchangeServiceImpl extends ObserveBaseService implements MoneyExchangeService {

	
	@Autowired
	private LoginService loginService;

	@Override
	public void newRoleInit(Role role) {
		role.setMoneyExchangeTimeStr(TimeUtils.getCurrentTimeStr());
	}

	@Override
	public GeneratedMessage moneyExchange(Role role, boolean add, int num) {
		System.out.println("@@@"+role.getAccount());
		int max = role.getMoneyExchangeNum();
		String today = TimeUtils.getCurrentTimeStr();
		String time = role.getMoneyExchangeTimeStr();
		int randiooMoney = num;
		if(num % 1000 != 0 || num * 0.001 < 1){
			return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
		}
		
		if(add){			
			num *= 0.1;
		}else{
			//TODO 每天5W
			
			if(time == null){
				max = num;
			}else{
				time = time.substring(0, 10);				
				if(today.equals(time)){
					if(max + num > 50000){
						return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
					}
					else{
						max += num;
					}
				}
				else{
					max = num;
					if(num > 50000){
						return MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.MONEY_NUM_ERROR.getNumber()).build();
					}
				}
			}
			
			
			num *= 0.085;
			
		}
		if(exchangeMoney(role, num, add)){
			role.setMoney(role.getMoney()+(add?1:-1)*randiooMoney);
			if(!add){
				role.setMoneyExchangeTimeStr(time);
				role.setMoneyExchangeNum(max);
			}
    		return SC.newBuilder().setMoneyExchangeResponse(MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()).setRoleData(loginService.getRoleData(role))).build();
    	}else{
    		return SC.newBuilder().setMoneyExchangeResponse(MoneyExchangeResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber())).build();
    	}
    	
		
	}
	
	public  boolean exchangeMoney(Role role, int money, boolean add){
		String url = /*"http://manager.app.randioo.com/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";*/
				"http://10.0.51.6/APPadmin/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";
    	String tpram = "";
    	tpram = tpram.concat(role.getAccount());
    	tpram = tpram.concat("&&money_num="+money);
    	tpram = tpram.concat("&&type="+(add?0:1));
    	System.out.println("URL"+url);
    	System.out.println("PRAM"+tpram);
    	HttpConnection conn = new HttpConnection(url,tpram);
    	conn.connect();
    	System.out.println(conn.result+""+conn.result.getClass().getName()+(conn.result.charAt(0) == '1'));
    	if(conn.result.charAt(0) == '1'){
    		return true;
    	}else{
    		return false;
    	}
	}

	

}
