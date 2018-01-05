/*******************************************************************************
 * Â© Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.persistence.mongodb.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MFindQuery extends BasicDBObject implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	private String origStr;
	private String collName;

	// --------------------------------------------------------------------------------
	public Object clone() {
		MFindQuery fq = new MFindQuery(origStr, new HashMap(this.toMap()));
		fq.setCollName(collName);
		return fq;
	}

	// --------------------------------------------------------------------------------
	public void setCollName(String s) {
		collName = s;
	}

	// --------------------------------------------------------------------------------
	public String getCollName() {
		return collName;
	}

	// --------------------------------------------------------------------------------
	public MFindQuery(String s, Map m) {
		super(m);
		origStr = s;
	}

	// --------------------------------------------------------------------------------
	public void setLimitArg(int i) {
		put("limitArg", Integer.valueOf(i));
		if (getFindArg().size() >= 3) {
			getFindArg().set(2, Integer.valueOf(i));
		}
		if (i <= 0) {
			getInvokedFunctionNameList().remove("limit");
		} else {
			if (!getInvokedFunctionNameList().contains("limit")) {
				getInvokedFunctionNameList().add("limit");
			}
		}
	}

	// --------------------------------------------------------------------------------
	public void setSkipArg(int i) {
		put("skipArg", Integer.valueOf(i));
		if (getFindArg().size() >= 4) {
			getFindArg().set(3, Integer.valueOf(i));
		}
		if (i <= 0) {
			getInvokedFunctionNameList().remove("skip");
		} else {
			if (!getInvokedFunctionNameList().contains("skip")) {
				getInvokedFunctionNameList().add("skip");
			}
		}
	}

	// --------------------------------------------------------------------------------
	public List getFindArg() {
		return (List) this.get("findArg");
	}

	// --------------------------------------------------------------------------------
	public int getLimitArg() {
		int _limit = getInteger("limitArg");
		if (_limit == -1) {
			List findArg = getFindArg();
			if (findArg.size() >= 3) {
				_limit = MStringUtil.parseInt(findArg.get(2));
			}
		}
		return _limit;
	}

	// --------------------------------------------------------------------------------
	private int getInteger(Object key) {
		if (this.containsKey(key)) {
			Object value = this.get(key);
			if (value instanceof Double) {
				return ((Double) value).intValue();
			} else if (value instanceof Integer) {
				return ((Integer) value).intValue();
			} else {
				return MStringUtil.parseInt(value);
			}
		} else {
			return -1;
		}
	}

	// --------------------------------------------------------------------------------
	public int getSkipArg() {
		int _skip = getInteger("skipArg");
		if (_skip == -1) {
			List findArg = getFindArg();
			if (findArg.size() == 4) {
				_skip = MStringUtil.parseInt(findArg.get(3));
			}
		}
		return _skip;
	}

	// --------------------------------------------------------------------------------
	public BasicDBObject getSortArg() {
		if (this.containsField("sortArg")) {
			return (BasicDBObject) get("sortArg");
		} else {
			return new BasicDBObject();
		}
	}

	// --------------------------------------------------------------------------------
	public List getInvokedFunctionNameList() {
		return (List) this.get("invoked");
	}

	// --------------------------------------------------------------------------------
	public void skip(int i) {

	}
	// --------------------------------------------------------------------------------
}
