package com.indracompany.sofia2.examples.scalability.msgs;

import lombok.Getter;

public class BasicMsg {
	
	@Getter String msg;
	
	public BasicMsg(String msg) {
		this.msg = msg;
	}

}
