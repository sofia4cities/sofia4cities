package com.indracompany.sofia2.iotbroker.common.util;

import java.util.Optional;

import com.indracompany.sofia2.persistence.enums.AccessMode;
import com.indracompany.sofia2.ssap.SSAPMessageTypes;

public class SSAP2PersintenceUtil {

	public static Optional<AccessMode>  formSSAPMessageType2TableAccesMode(SSAPMessageTypes rType) {
		Optional<AccessMode> optional = Optional.empty();
		switch (rType) {
		case INSERT:
			optional = Optional.of(AccessMode.INSERT);
			break;
		case UPDATE:
			optional = Optional.of(AccessMode.UPDATE);
			break;
		case DELETE:
			optional = Optional.of(AccessMode.DELETE);
			break;
		case QUERY:
			optional = Optional.of(AccessMode.SELECT);
			break;
		}
		
		return optional;
	}

}

