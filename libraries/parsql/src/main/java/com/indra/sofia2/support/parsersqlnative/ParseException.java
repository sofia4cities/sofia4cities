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
package com.indra.sofia2.support.parsersqlnative;

public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal){
		super("");
		specialConstructor = true;
		currentToken = currentTokenVal;
		expectedTokenSequences = expectedTokenSequencesVal;
		tokenImage = tokenImageVal;
	}

	public ParseException() {
		super();
		specialConstructor = false;
	}

	public ParseException(String message) {
		super(message);
		specialConstructor = false;
	}

	protected boolean specialConstructor;

	public Token currentToken;

	public int[][] expectedTokenSequences;

	public String[] tokenImage;

	public String getMessage() {
		if (!specialConstructor) {
			return super.getMessage();
		}
		StringBuffer expected = new StringBuffer();
		int maxSize = 0;
		for (int i = 0; i < expectedTokenSequences.length; i++) {
			if (maxSize < expectedTokenSequences[i].length) {
				maxSize = expectedTokenSequences[i].length;
			}
			for (int j = 0; j < expectedTokenSequences[i].length; j++) {
				expected.append(tokenImage[expectedTokenSequences[i][j]]).append(" ");
			}
			if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
				expected.append("...");
			}
			expected.append(eol).append("    ");
		}
		String retval = "Encountered \"";
		Token tok = currentToken.next;
		for (int i = 0; i < maxSize; i++) {
			if (i != 0) retval += " ";
			if (tok.kind == 0) {
				retval += tokenImage[0];
				break;
			}
			retval += addEscapes(tok.image);
			tok = tok.next; 
		}
		retval += "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn;
		retval += "." + eol;
		if (expectedTokenSequences.length == 1) {
			retval += "Was expecting:" + eol + "    ";
		} else {
			retval += "Was expecting one of:" + eol + "    ";
		}
		retval += expected.toString();
		return retval;
	}

	protected String eol = System.getProperty("line.separator", "\n");

	protected String addEscapes(String str) {
		StringBuffer retval = new StringBuffer();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i))
			{
			case 0 :
				continue;
			case '\b':
				retval.append("\\b");
				continue;
			case '\t':
				retval.append("\\t");
				continue;
			case '\n':
				retval.append("\\n");
				continue;
			case '\f':
				retval.append("\\f");
				continue;
			case '\r':
				retval.append("\\r");
				continue;
			case '\"':
				retval.append("\\\"");
				continue;
			case '\'':
				retval.append("\\\'");
				continue;
			case '\\':
				retval.append("\\\\");
				continue;
			default:
				if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
					String s = "0000" + Integer.toString(ch, 16);
					retval.append("\\u" + s.substring(s.length() - 4, s.length()));
				} else {
					retval.append(ch);
				}
				continue;
			}
		}
		return retval.toString();
	}

}
