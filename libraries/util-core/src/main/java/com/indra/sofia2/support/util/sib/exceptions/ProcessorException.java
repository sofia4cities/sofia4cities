/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.support.util.sib.exceptions;

import com.indra.sofia2.ssap.ssap.SSAPErrorCode;

public class ProcessorException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private SSAPErrorCode errorCode;
	
	public ProcessorException(Exception e, SSAPErrorCode errorCode){
		super(e);
		this.errorCode=errorCode;
	}
	
	public ProcessorException(String msg, SSAPErrorCode errorCode){
		super(msg);
		this.errorCode=errorCode;
	}
	
	public ProcessorException(String msg, Throwable e, SSAPErrorCode errorCode){
		super(msg, e);
		this.errorCode=errorCode;
	}
	
	public SSAPErrorCode getErrorCode(){
		return this.errorCode;
	}
	
}
