package com.hxxdemo.plug.service;

import java.util.List;
import java.util.Map;

public interface InvitationCodeService {
	
	public String getInvitationCode(String telephone);
	
	public void addInvitationCode(String invitationCode ,Long userid);
	
	public void addInvitationCodePlus(String invitationCode ,Long userid);

	public boolean checkInvitationCode(Map<String,Object> params);
	
	public void addBeInvited(Map<String,Object> params);
	
	int getInvitationNum(Long userId);
	
	void checkVip(Map<String,Object> params);
	
	public String createInvitationCode(String invitationCode);
	
	public String getInvitationCodeByUserUuid(String token);
	
	public List<Long> getUserIdsList();
}
