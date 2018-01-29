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

import com.indra.sofia2.support.parsersqlnative.util.ParserConstants;

@SuppressWarnings("unused")
public class ParserTokenManager implements ParserConstants
{

	public String sqlStream;

	protected SimpleCharStream inputStream;

	private final int[] prounds = new int[47];

	private final int[] stateSet = new int[94];

	protected char curChar;

	public ParserTokenManager(String stream, SimpleCharStream simplestream){
		if (stream == null) {
			throw new Error("ERROR: empty string");
		}
		
		if (SimpleCharStream.STATIC) {
			throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		}
		
		sqlStream = stream;
		inputStream = simplestream;
	}
	
	public ParserTokenManager(SimpleCharStream stream){
		if (SimpleCharStream.STATIC) {
			throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		}
		inputStream = stream;
	}

	public ParserTokenManager(SimpleCharStream stream, int lexState){
		this(stream);
		switchTo(lexState);
	}

	private final int stopStringLiteralDfa0(int pos, long active0, long active1)
	{
		switch (pos)
		{
		case 0:
			if ((active1 & 0x4000000000L) != 0L) {
				return 0;
			}
			if ((active1 & 0x10020000000L) != 0L) {
				return 47;
			}
			if ((active1 & 0x80000000000L) != 0L) {
				return 3;
			}
			if ((active0 & 0xffffffffffffffe0L) != 0L || (active1 & 0xfffL) != 0L)
			{
				pMatchedKind = 82;
				return 48;
			}
			return -1;
		case 1:
			if ((active0 & 0x1a003f00004300L) != 0L) {
				return 48;
			}
			if ((active0 & 0xffe5ffc0ffffbce0L) != 0L || (active1 & 0xfffL) != 0L)
			{
				if (pMatchedPos != 1)
				{
					pMatchedKind = 82;
					pMatchedPos = 1;
				}
				return 48;
			}
			return -1;
		case 2:
			if ((active0 & 0xebf5d8deefffb800L) != 0L || (active1 & 0xffeL) != 0L)
			{
				if (pMatchedPos != 2)
				{
					pMatchedKind = 82;
					pMatchedPos = 2;
				}
				return 48;
			}
			if ((active0 & 0x14002700100006e0L) != 0L || (active1 & 0x1L) != 0L) {
				return 48;
			}
			return -1;
		case 3:
			if ((active0 & 0x1c488d024508000L) != 0L || (active1 & 0x600L) != 0L) {
				return 48;
			}
			if ((active0 & 0xea31540ecbaf3800L) != 0L || (active1 & 0x9feL) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 3;
				return 48;
			}
			return -1;
		case 4:
			if ((active0 & 0xa030040048080000L) != 0L || (active1 & 0x90aL) != 0L)
				return 48;
			if ((active0 & 0x4a01500e83a73800L) != 0L || (active1 & 0xf4L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 4;
				return 48;
			}
			return -1;
		case 5:
			if ((active0 & 0x4200100c01853800L) != 0L || (active1 & 0xc4L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 5;
				return 48;
			}
			if ((active0 & 0x801400282220000L) != 0L || (active1 & 0x30L) != 0L)
				return 48;
			return -1;
		case 6:
			if ((active0 & 0x1000L) != 0L)
			{
				if (pMatchedPos != 6)
				{
					pMatchedKind = 82;
					pMatchedPos = 6;
				}
				return 11;
			}
			if ((active0 & 0x100400052800L) != 0L || (active1 & 0xc0L) != 0L)
				return 48;
			if ((active0 & 0x4200000801800000L) != 0L || (active1 & 0x4L) != 0L)
			{
				if (pMatchedPos != 6)
				{
					pMatchedKind = 82;
					pMatchedPos = 6;
				}
				return 48;
			}
			return -1;
		case 7:
			if ((active0 & 0x4200000000800000L) != 0L)
				return 48;
			if ((active0 & 0x1000L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 7;
				return 11;
			}
			if ((active1 & 0x40L) != 0L)
				return 11;
			if ((active0 & 0x801000000L) != 0L || (active1 & 0x4L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 7;
				return 48;
			}
			return -1;
		case 8:
			if ((active1 & 0x4L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 8;
				return 48;
			}
			if ((active0 & 0x801000000L) != 0L)
				return 48;
			if ((active0 & 0x1000L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 8;
				return 11;
			}
			return -1;
		case 9:
			if ((active1 & 0x4L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 9;
				return 48;
			}
			if ((active0 & 0x1000L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 9;
				return 11;
			}
			return -1;
		case 10:
			if ((active1 & 0x4L) != 0L)
				return 48;
			if ((active0 & 0x1000L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 10;
				return 11;
			}
			return -1;
		case 11:
			if ((active0 & 0x1000L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 11;
				return 11;
			}
			return -1;
		case 12:
			if ((active0 & 0x1000L) != 0L)
			{
				pMatchedKind = 82;
				pMatchedPos = 12;
				return 11;
			}
			return -1;
		default :
			return -1;
		}
	}

	private final int startNfa0(int pos, long active0, long active1)
	{
		return moveNfa0(stopStringLiteralDfa0(pos, active0, active1), pos + 1);
	}

	private final int stopAtPos(int pos, int kind)
	{
		pMatchedKind = kind;
		pMatchedPos = pos;
		return pos + 1;
	}

	private final int startNfaWithStates0(int pos, int kind, int state)
	{
		pMatchedKind = kind;
		pMatchedPos = pos;
		try { 
			curChar = inputStream.readChar(); }
		catch(java.io.IOException e) { return pos + 1; }
		return moveNfa0(state, pos + 1);
	}

	private final int moveStringLiteralDfa0x0()
	{
		switch(curChar)
		{
		case 33:
			return moveStringLiteralDfa1x0(0x0L, 0x40000000L);
		case 35:
			return stopAtPos(0, 95);
		case 40:
			return stopAtPos(0, 88);
		case 41:
			return stopAtPos(0, 90);
		case 42:
			pMatchedKind = 103;
			return moveStringLiteralDfa1x0(0x0L, 0x100000000000L);
		case 43:
			return stopAtPos(0, 101);
		case 44:
			return stopAtPos(0, 89);
		case 45:
			return startNfaWithStates0(0, 102, 0);
		case 46:
			pMatchedKind = 93;
			return moveStringLiteralDfa1x0(0x0L, 0x10000000000L);
		case 47:
			return startNfaWithStates0(0, 107, 3);
		case 59:
			return stopAtPos(0, 91);
		case 60:
			pMatchedKind = 99;
			return moveStringLiteralDfa1x0(0x0L, 0x1100000000L);
		case 61:
			return stopAtPos(0, 92);
		case 62:
			pMatchedKind = 97;
			return moveStringLiteralDfa1x0(0x0L, 0x400000000L);
		case 63:
			return stopAtPos(0, 105);
		case 65:
		case 97:
			return moveStringLiteralDfa1x0(0x7e0L, 0x0L);
		case 66:
		case 98:
			return moveStringLiteralDfa1x0(0x7800L, 0x0L);
		case 67:
		case 99:
			return moveStringLiteralDfa1x0(0xf8000L, 0x0L);
		case 68:
		case 100:
			return moveStringLiteralDfa1x0(0xf00000L, 0x0L);
		case 69:
		case 101:
			return moveStringLiteralDfa1x0(0x7000000L, 0x0L);
		case 70:
		case 102:
			return moveStringLiteralDfa1x0(0x38000000L, 0x0L);
		case 71:
		case 103:
			return moveStringLiteralDfa1x0(0x40000000L, 0x0L);
		case 72:
		case 104:
			return moveStringLiteralDfa1x0(0x80000000L, 0x0L);
		case 73:
		case 105:
			return moveStringLiteralDfa1x0(0x3f00000000L, 0x0L);
		case 76:
		case 108:
			return moveStringLiteralDfa1x0(0xc000000000L, 0x0L);
		case 77:
		case 109:
			return moveStringLiteralDfa1x0(0xf0000000000L, 0x0L);
		case 78:
		case 110:
			return moveStringLiteralDfa1x0(0x1f00000000000L, 0x0L);
		case 79:
		case 111:
			return moveStringLiteralDfa1x0(0x1e000000000000L, 0x0L);
		case 80:
		case 112:
			return moveStringLiteralDfa1x0(0x20000000000000L, 0x0L);
		case 81:
		case 113:
			return moveStringLiteralDfa1x0(0x40000000000000L, 0x0L);
		case 82:
		case 114:
			return moveStringLiteralDfa1x0(0x780000000000000L, 0x0L);
		case 83:
		case 115:
			return moveStringLiteralDfa1x0(0xf800000000000000L, 0x1L);
		case 84:
		case 116:
			return moveStringLiteralDfa1x0(0x0L, 0x6L);
		case 85:
		case 117:
			return moveStringLiteralDfa1x0(0x0L, 0x18L);
		case 86:
		case 118:
			return moveStringLiteralDfa1x0(0x0L, 0xe0L);
		case 87:
		case 119:
			return moveStringLiteralDfa1x0(0x0L, 0xf00L);
		case 124:
			return moveStringLiteralDfa1x0(0x0L, 0x40000000000L);
		default :
			return moveNfa0(2, 0);
		}
	}
	private final int moveStringLiteralDfa1x0(long active0, long active1)
	{
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(0, active0, active1);
			return 1;
		}
		switch(curChar)
		{
		case 42:
			if ((active1 & 0x10000000000L) != 0L)
				return stopAtPos(1, 104);
			else if ((active1 & 0x100000000000L) != 0L)
				return stopAtPos(1, 108);
			break;
		case 61:
			if ((active1 & 0x40000000L) != 0L)
				return stopAtPos(1, 94);
			else if ((active1 & 0x400000000L) != 0L)
				return stopAtPos(1, 98);
			else if ((active1 & 0x1000000000L) != 0L)
				return stopAtPos(1, 100);
			break;
		case 62:
			if ((active1 & 0x100000000L) != 0L)
				return stopAtPos(1, 96);
			break;
		case 65:
		case 97:
			return moveStringLiteralDfa2x0(active0, 0x110080100000L, active1, 0xe2L);
		case 69:
		case 101:
			return moveStringLiteralDfa2x0(active0, 0x1980000000600800L, active1, 0L);
		case 70:
		case 102:
			if ((active0 & 0x2000000000000L) != 0L)
				return startNfaWithStates0(1, 49, 48);
			break;
		case 72:
		case 104:
			return moveStringLiteralDfa2x0(active0, 0x2000000000008000L, active1, 0x100L);
		case 73:
		case 105:
			return moveStringLiteralDfa2x0(active0, 0x64000801000L, active1, 0x200L);
		case 76:
		case 108:
			return moveStringLiteralDfa2x0(active0, 0x8000020L, active1, 0L);
		case 77:
		case 109:
			return moveStringLiteralDfa2x0(active0, 0x4000000000000000L, active1, 0L);
		case 78:
		case 110:
			if ((active0 & 0x100000000L) != 0L)
			{
				pMatchedKind = 32;
				pMatchedPos = 1;
			}
			return moveStringLiteralDfa2x0(active0, 0x4001e000000c0L, active1, 0x8L);
		case 79:
		case 111:
			return moveStringLiteralDfa2x0(active0, 0x6006880100f2000L, active1, 0x400L);
		case 80:
		case 112:
			return moveStringLiteralDfa2x0(active0, 0L, active1, 0x10L);
		case 82:
		case 114:
			if ((active0 & 0x8000000000000L) != 0L)
			{
				pMatchedKind = 51;
				pMatchedPos = 1;
			}
			return moveStringLiteralDfa2x0(active0, 0x30000060000000L, active1, 0x804L);
		case 83:
		case 115:
			if ((active0 & 0x100L) != 0L)
			{
				pMatchedKind = 8;
				pMatchedPos = 1;
			}
			else if ((active0 & 0x2000000000L) != 0L)
				return startNfaWithStates0(1, 37, 48);
			return moveStringLiteralDfa2x0(active0, 0x200L, active1, 0L);
		case 84:
		case 116:
			return moveStringLiteralDfa2x0(active0, 0x8000000000000000L, active1, 0L);
		case 85:
		case 117:
			return moveStringLiteralDfa2x0(active0, 0x41800000000000L, active1, 0x1L);
		case 86:
		case 118:
			return moveStringLiteralDfa2x0(active0, 0x400L, active1, 0L);
		case 88:
		case 120:
			return moveStringLiteralDfa2x0(active0, 0x7000000L, active1, 0L);
		case 89:
		case 121:
			if ((active0 & 0x4000L) != 0L)
				return startNfaWithStates0(1, 14, 48);
			break;
		case 124:
			if ((active1 & 0x40000000000L) != 0L)
				return stopAtPos(1, 106);
			break;
		default :
			break;
		}
		return startNfa0(0, active0, active1);
	}
	private final int moveStringLiteralDfa2x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(0, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(1, active0, active1);
			return 2;
		}
		switch(curChar)
		{
		case 65:
		case 97:
			return moveStringLiteralDfa3x0(active0, 0xe180000000008000L, active1, 0x4L);
		case 66:
		case 98:
			return moveStringLiteralDfa3x0(active0, 0L, active1, 0x2L);
		case 67:
		case 99:
			if ((active0 & 0x200L) != 0L)
				return startNfaWithStates0(2, 9, 48);
			return moveStringLiteralDfa3x0(active0, 0x8001000000L, active1, 0L);
		case 68:
		case 100:
			if ((active0 & 0x40L) != 0L)
				return startNfaWithStates0(2, 6, 48);
			return moveStringLiteralDfa3x0(active0, 0x10080000000000L, active1, 0x10L);
		case 69:
		case 101:
			return moveStringLiteralDfa3x0(active0, 0L, active1, 0x100L);
		case 71:
		case 103:
			if ((active0 & 0x400L) != 0L)
				return startNfaWithStates0(2, 10, 48);
			break;
		case 73:
		case 105:
			return moveStringLiteralDfa3x0(active0, 0x60000006000000L, active1, 0x808L);
		case 75:
		case 107:
			return moveStringLiteralDfa3x0(active0, 0x4000000000L, active1, 0L);
		case 76:
		case 108:
			if ((active0 & 0x20L) != 0L)
				return startNfaWithStates0(2, 5, 48);
			return moveStringLiteralDfa3x0(active0, 0xa04800000200000L, active1, 0x20L);
		case 77:
		case 109:
			if ((active1 & 0x1L) != 0L)
				return startNfaWithStates0(2, 64, 48);
			return moveStringLiteralDfa3x0(active0, 0x1000000030000L, active1, 0L);
		case 78:
		case 110:
			if ((active0 & 0x20000000000L) != 0L)
			{
				pMatchedKind = 41;
				pMatchedPos = 2;
			}
			return moveStringLiteralDfa3x0(active0, 0x40000041000L, active1, 0L);
		case 79:
		case 111:
			return moveStringLiteralDfa3x0(active0, 0x68002000L, active1, 0L);
		case 82:
		case 114:
			if ((active0 & 0x10000000L) != 0L)
				return startNfaWithStates0(2, 28, 48);
			return moveStringLiteralDfa3x0(active0, 0L, active1, 0x4c0L);
		case 83:
		case 115:
			return moveStringLiteralDfa3x0(active0, 0x200c00000L, active1, 0L);
		case 84:
		case 116:
			if ((active0 & 0x200000000000L) != 0L)
				return startNfaWithStates0(2, 45, 48);
			else if ((active0 & 0x1000000000000000L) != 0L)
				return startNfaWithStates0(2, 60, 48);
			return moveStringLiteralDfa3x0(active0, 0x101c00100800L, active1, 0x200L);
		case 85:
		case 117:
			return moveStringLiteralDfa3x0(active0, 0x80000L, active1, 0L);
		case 86:
		case 118:
			return moveStringLiteralDfa3x0(active0, 0x80000000L, active1, 0L);
		case 87:
		case 119:
			if ((active0 & 0x400000000000000L) != 0L)
				return startNfaWithStates0(2, 58, 48);
			return moveStringLiteralDfa3x0(active0, 0x400000000000L, active1, 0L);
		case 88:
		case 120:
			if ((active0 & 0x10000000000L) != 0L)
				return startNfaWithStates0(2, 40, 48);
			break;
		case 89:
		case 121:
			if ((active0 & 0x80L) != 0L)
				return startNfaWithStates0(2, 7, 48);
			break;
		default :
			break;
		}
		return startNfa0(1, active0, active1);
	}
	private final int moveStringLiteralDfa3x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(1, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(2, active0, active1);
			return 3;
		}
		switch(curChar)
		{
		case 65:
		case 97:
			return moveStringLiteralDfa4x0(active0, 0x400008001000L, active1, 0x10L);
		case 66:
		case 98:
			return moveStringLiteralDfa4x0(active0, 0x1000000000000L, active1, 0L);
		case 67:
		case 99:
			if ((active0 & 0x400000L) != 0L)
				return startNfaWithStates0(3, 22, 48);
			return moveStringLiteralDfa4x0(active0, 0L, active1, 0xc0L);
		case 68:
		case 100:
			if ((active0 & 0x80000000000000L) != 0L)
				return startNfaWithStates0(3, 55, 48);
			break;
		case 69:
		case 101:
			if ((active0 & 0x100000L) != 0L)
				return startNfaWithStates0(3, 20, 48);
			else if ((active0 & 0x4000000000L) != 0L)
				return startNfaWithStates0(3, 38, 48);
			else if ((active0 & 0x80000000000L) != 0L)
				return startNfaWithStates0(3, 43, 48);
			return moveStringLiteralDfa4x0(active0, 0x810000e00200000L, active1, 0L);
		case 72:
		case 104:
			if ((active1 & 0x200L) != 0L)
				return startNfaWithStates0(3, 73, 48);
			break;
		case 73:
		case 105:
			return moveStringLiteralDfa4x0(active0, 0x80000000L, active1, 0L);
		case 75:
		case 107:
			if ((active0 & 0x8000000000L) != 0L)
				return startNfaWithStates0(3, 39, 48);
			else if ((active1 & 0x400L) != 0L)
				return startNfaWithStates0(3, 74, 48);
			break;
		case 76:
		case 108:
			if ((active0 & 0x800000000000L) != 0L)
				return startNfaWithStates0(3, 47, 48);
			else if ((active0 & 0x100000000000000L) != 0L)
				return startNfaWithStates0(3, 56, 48);
			return moveStringLiteralDfa4x0(active0, 0x4200000001002000L, active1, 0x2L);
		case 77:
		case 109:
			if ((active0 & 0x20000000L) != 0L)
				return startNfaWithStates0(3, 29, 48);
			return moveStringLiteralDfa4x0(active0, 0x30000L, active1, 0L);
		case 78:
		case 110:
			return moveStringLiteralDfa4x0(active0, 0xc0000L, active1, 0x4L);
		case 79:
		case 111:
			if ((active0 & 0x1000000000L) != 0L)
				return startNfaWithStates0(3, 36, 48);
			return moveStringLiteralDfa4x0(active0, 0x20000000000000L, active1, 0x8L);
		case 82:
		case 114:
			if ((active0 & 0x8000L) != 0L)
				return startNfaWithStates0(3, 15, 48);
			return moveStringLiteralDfa4x0(active0, 0xa000000000000000L, active1, 0x100L);
		case 83:
		case 115:
			return moveStringLiteralDfa4x0(active0, 0x2000000L, active1, 0L);
		case 84:
		case 116:
			if ((active0 & 0x4000000L) != 0L)
				return startNfaWithStates0(3, 26, 48);
			else if ((active0 & 0x40000000000000L) != 0L)
				return startNfaWithStates0(3, 54, 48);
			return moveStringLiteralDfa4x0(active0, 0x800000L, active1, 0x800L);
		case 85:
		case 117:
			return moveStringLiteralDfa4x0(active0, 0x140040000000L, active1, 0x20L);
		case 87:
		case 119:
			return moveStringLiteralDfa4x0(active0, 0x800L, active1, 0L);
		case 89:
		case 121:
			if ((active0 & 0x4000000000000L) != 0L)
				return startNfaWithStates0(3, 50, 48);
			break;
		default :
			break;
		}
		return startNfa0(2, active0, active1);
	}
	private final int moveStringLiteralDfa4x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(2, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(3, active0, active1);
			return 4;
		}
		switch(curChar)
		{
		case 66:
		case 98:
			return moveStringLiteralDfa5x0(active0, 0x200000000000000L, active1, 0L);
		case 67:
		case 99:
			return moveStringLiteralDfa5x0(active0, 0x800000000000000L, active1, 0L);
		case 69:
		case 101:
			if ((active0 & 0x2000000000000000L) != 0L)
				return startNfaWithStates0(4, 61, 48);
			else if ((active1 & 0x2L) != 0L)
				return startNfaWithStates0(4, 65, 48);
			else if ((active1 & 0x100L) != 0L)
				return startNfaWithStates0(4, 72, 48);
			else if ((active1 & 0x800L) != 0L)
				return startNfaWithStates0(4, 75, 48);
			return moveStringLiteralDfa5x0(active0, 0x1000000052800L, active1, 0x20L);
		case 71:
		case 103:
			return moveStringLiteralDfa5x0(active0, 0x400000000L, active1, 0L);
		case 72:
		case 104:
			return moveStringLiteralDfa5x0(active0, 0L, active1, 0xc0L);
		case 73:
		case 105:
			return moveStringLiteralDfa5x0(active0, 0x400000820000L, active1, 0L);
		case 76:
		case 108:
			return moveStringLiteralDfa5x0(active0, 0x4000000000000000L, active1, 0L);
		case 78:
		case 110:
			if ((active1 & 0x8L) != 0L)
				return startNfaWithStates0(4, 67, 48);
			return moveStringLiteralDfa5x0(active0, 0x80000000L, active1, 0L);
		case 80:
		case 112:
			if ((active0 & 0x40000000L) != 0L)
				return startNfaWithStates0(4, 30, 48);
			break;
		case 82:
		case 114:
			if ((active0 & 0x10000000000000L) != 0L)
				return startNfaWithStates0(4, 52, 48);
			else if ((active0 & 0x20000000000000L) != 0L)
				return startNfaWithStates0(4, 53, 48);
			return moveStringLiteralDfa5x0(active0, 0x100a00001000L, active1, 0L);
		case 83:
		case 115:
			if ((active0 & 0x40000000000L) != 0L)
				return startNfaWithStates0(4, 42, 48);
			return moveStringLiteralDfa5x0(active0, 0L, active1, 0x4L);
		case 84:
		case 116:
			if ((active0 & 0x80000L) != 0L)
				return startNfaWithStates0(4, 19, 48);
			else if ((active0 & 0x8000000L) != 0L)
				return startNfaWithStates0(4, 27, 48);
			else if ((active0 & 0x8000000000000000L) != 0L)
				return startNfaWithStates0(4, 63, 48);
			return moveStringLiteralDfa5x0(active0, 0x2200000L, active1, 0x10L);
		case 85:
		case 117:
			return moveStringLiteralDfa5x0(active0, 0x1000000L, active1, 0L);
		default :
			break;
		}
		return startNfa0(3, active0, active1);
	}
	private final int moveStringLiteralDfa5x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(3, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(4, active0, active1);
			return 5;
		}
		switch(curChar)
		{
		case 65:
		case 97:
			return moveStringLiteralDfa6x0(active0, 0x200100000002000L, active1, 0xc4L);
		case 67:
		case 99:
			return moveStringLiteralDfa6x0(active0, 0x40000L, active1, 0L);
		case 69:
		case 101:
			if ((active0 & 0x200000L) != 0L)
				return startNfaWithStates0(5, 21, 48);
			else if ((active1 & 0x10L) != 0L)
				return startNfaWithStates0(5, 68, 48);
			return moveStringLiteralDfa6x0(active0, 0x400000800L, active1, 0L);
		case 71:
		case 103:
			if ((active0 & 0x80000000L) != 0L)
				return startNfaWithStates0(5, 31, 48);
			break;
		case 73:
		case 105:
			return moveStringLiteralDfa6x0(active0, 0x4000000000000000L, active1, 0L);
		case 78:
		case 110:
			return moveStringLiteralDfa6x0(active0, 0x810000L, active1, 0L);
		case 82:
		case 114:
			if ((active0 & 0x1000000000000L) != 0L)
				return startNfaWithStates0(5, 48, 48);
			break;
		case 83:
		case 115:
			if ((active0 & 0x2000000L) != 0L)
				return startNfaWithStates0(5, 25, 48);
			else if ((active1 & 0x20L) != 0L)
				return startNfaWithStates0(5, 69, 48);
			return moveStringLiteralDfa6x0(active0, 0x801000000L, active1, 0L);
		case 84:
		case 116:
			if ((active0 & 0x20000L) != 0L)
				return startNfaWithStates0(5, 17, 48);
			else if ((active0 & 0x200000000L) != 0L)
				return startNfaWithStates0(5, 33, 48);
			else if ((active0 & 0x400000000000L) != 0L)
				return startNfaWithStates0(5, 46, 48);
			else if ((active0 & 0x800000000000000L) != 0L)
				return startNfaWithStates0(5, 59, 48);
			break;
		case 89:
		case 121:
			return moveStringLiteralDfa6x0(active0, 0x1000L, active1, 0L);
		default :
			break;
		}
		return startNfa0(4, active0, active1);
	}
	private final int moveStringLiteralDfa6x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(4, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(5, active0, active1);
			return 6;
		}
		switch(curChar)
		{
		case 95:
			return moveStringLiteralDfa7x0(active0, 0x1000L, active1, 0L);
		case 67:
		case 99:
			return moveStringLiteralDfa7x0(active0, 0x200000000800000L, active1, 0x4L);
		case 69:
		case 101:
			return moveStringLiteralDfa7x0(active0, 0x800000000L, active1, 0L);
		case 73:
		case 105:
			return moveStringLiteralDfa7x0(active0, 0x1000000L, active1, 0L);
		case 76:
		case 108:
			if ((active0 & 0x100000000000L) != 0L)
				return startNfaWithStates0(6, 44, 48);
			break;
		case 78:
		case 110:
			if ((active0 & 0x800L) != 0L)
				return startNfaWithStates0(6, 11, 48);
			else if ((active0 & 0x2000L) != 0L)
				return startNfaWithStates0(6, 13, 48);
			return moveStringLiteralDfa7x0(active0, 0x4000000000000000L, active1, 0L);
		case 82:
		case 114:
			if ((active0 & 0x400000000L) != 0L)
				return startNfaWithStates0(6, 34, 48);
			else if ((active1 & 0x80L) != 0L)
			{
				pMatchedKind = 71;
				pMatchedPos = 6;
			}
			return moveStringLiteralDfa7x0(active0, 0L, active1, 0x40L);
		case 84:
		case 116:
			if ((active0 & 0x10000L) != 0L)
				return startNfaWithStates0(6, 16, 48);
			else if ((active0 & 0x40000L) != 0L)
				return startNfaWithStates0(6, 18, 48);
			break;
		default :
			break;
		}
		return startNfa0(5, active0, active1);
	}
	private final int moveStringLiteralDfa7x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(5, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(6, active0, active1);
			return 7;
		}
		switch(curChar)
		{
		case 50:
			if ((active1 & 0x40L) != 0L)
				return startNfaWithStates0(7, 70, 11);
			break;
		case 67:
		case 99:
			return moveStringLiteralDfa8x0(active0, 0x800000000L, active1, 0L);
		case 73:
		case 105:
			return moveStringLiteralDfa8x0(active0, 0x1000L, active1, 0L);
		case 75:
		case 107:
			if ((active0 & 0x200000000000000L) != 0L)
				return startNfaWithStates0(7, 57, 48);
			break;
		case 84:
		case 116:
			if ((active0 & 0x800000L) != 0L)
				return startNfaWithStates0(7, 23, 48);
			else if ((active0 & 0x4000000000000000L) != 0L)
				return startNfaWithStates0(7, 62, 48);
			return moveStringLiteralDfa8x0(active0, 0L, active1, 0x4L);
		case 86:
		case 118:
			return moveStringLiteralDfa8x0(active0, 0x1000000L, active1, 0L);
		default :
			break;
		}
		return startNfa0(6, active0, active1);
	}
	private final int moveStringLiteralDfa8x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(6, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(7, active0, active1);
			return 8;
		}
		switch(curChar)
		{
		case 69:
		case 101:
			if ((active0 & 0x1000000L) != 0L)
				return startNfaWithStates0(8, 24, 48);
			break;
		case 73:
		case 105:
			return moveStringLiteralDfa9x0(active0, 0L, active1, 0x4L);
		case 78:
		case 110:
			return moveStringLiteralDfa9x0(active0, 0x1000L, active1, 0L);
		case 84:
		case 116:
			if ((active0 & 0x800000000L) != 0L)
				return startNfaWithStates0(8, 35, 48);
			break;
		default :
			break;
		}
		return startNfa0(7, active0, active1);
	}
	private final int moveStringLiteralDfa9x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(7, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(8, active0, active1);
			return 9;
		}
		switch(curChar)
		{
		case 79:
		case 111:
			return moveStringLiteralDfa10x0(active0, 0L, active1, 0x4L);
		case 84:
		case 116:
			return moveStringLiteralDfa10x0(active0, 0x1000L, active1, 0L);
		default :
			break;
		}
		return startNfa0(8, active0, active1);
	}
	private final int moveStringLiteralDfa10x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(8, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(9, active0, active1);
			return 10;
		}
		switch(curChar)
		{
		case 69:
		case 101:
			return moveStringLiteralDfa11x0(active0, 0x1000L, active1, 0L);
		case 78:
		case 110:
			if ((active1 & 0x4L) != 0L)
				return startNfaWithStates0(10, 66, 48);
			break;
		default :
			break;
		}
		return startNfa0(9, active0, active1);
	}
	private final int moveStringLiteralDfa11x0(long old0, long active0, long old1, long active1)
	{
		if (((active0 &= old0) | (active1 &= old1)) == 0L)
			return startNfa0(9, old0, old1); 
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(10, active0, 0L);
			return 11;
		}
		switch(curChar)
		{
		case 71:
		case 103:
			return moveStringLiteralDfa12x0(active0, 0x1000L);
		default :
			break;
		}
		return startNfa0(10, active0, 0L);
	}
	private final int moveStringLiteralDfa12x0(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
			return startNfa0(10, old0, 0L);
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(11, active0, 0L);
			return 12;
		}
		switch(curChar)
		{
		case 69:
		case 101:
			return moveStringLiteralDfa13x0(active0, 0x1000L);
		default :
			break;
		}
		return startNfa0(11, active0, 0L);
	}
	private final int moveStringLiteralDfa13x0(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
			return startNfa0(11, old0, 0L);
		try { curChar = inputStream.readChar(); }
		catch(java.io.IOException e) {
			stopStringLiteralDfa0(12, active0, 0L);
			return 13;
		}
		switch(curChar)
		{
		case 82:
		case 114:
			if ((active0 & 0x1000L) != 0L)
				return startNfaWithStates0(13, 12, 11);
			break;
		default :
			break;
		}
		return startNfa0(12, active0, 0L);
	}
	private final void checkNAdd(int state)
	{
		if (prounds[state] != round)
		{
			stateSet[newStateCnt++] = state;
			prounds[state] = round;
		}
	}
	private final void addStates(int start, int end)
	{
		int startAux = start;
		do {
			stateSet[newStateCnt++] = pNextStates[startAux];
		} while (startAux++ != end);
	}
	private final void checkNAddTwoStates(int state1, int state2)
	{
		checkNAdd(state1);
		checkNAdd(state2);
	}

	private final void checkNAddStates(int start, int end)
	{
		int startAux = start;
		do {
			checkNAdd(pNextStates[startAux]);
		} while (startAux++ != end);
	}

	static final long[] bitVec0 = {
		0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
	};

	private final int moveNfa0(int startState, int curPos)
	{
		int curPosAux = curPos;
		int startsAt = 0;
		newStateCnt = 47;
		int i = 1;
		stateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (;;)
		{
			if (++round == 0x7fffffff)
				reInitRounds();
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch(stateSet[--i])
					{
					case 2:
						if ((0x3ff000000000000L & l) != 0L)
						{
							if (kind > 76)
								kind = 76;
							checkNAddStates(0, 6);
						}
						else if (curChar == 46)
							checkNAddTwoStates(27, 37);
						else if (curChar == 34)
							checkNAddTwoStates(24, 25);
						else if (curChar == 39)
							checkNAddTwoStates(19, 20);
						else if (curChar == 58)
							stateSet[newStateCnt++] = 13;
						else if (curChar == 47)
							stateSet[newStateCnt++] = 3;
						else if (curChar == 45)
							stateSet[newStateCnt++] = 0;
						break;
					case 48:
					case 11:
						if ((0x3ff001000000000L & l) == 0L)
							break;
						if (kind > 82)
							kind = 82;
						checkNAdd(11);
						break;
					case 47:
						if ((0x3ff000000000000L & l) != 0L)
						{
							if (kind > 76)
								kind = 76;
							checkNAdd(37);
						}
						if ((0x3ff000000000000L & l) != 0L)
						{
							if (kind > 76)
								kind = 76;
							checkNAddTwoStates(27, 28);
						}
						break;
					case 0:
						if (curChar != 45)
							break;
						if (kind > 80)
							kind = 80;
						checkNAdd(1);
						break;
					case 1:
						if ((0xffffffffffffdbffL & l) == 0L)
							break;
						if (kind > 80)
							kind = 80;
						checkNAdd(1);
						break;
					case 3:
						if (curChar == 42)
							checkNAddTwoStates(4, 5);
						break;
					case 4:
						if ((0xfffffbffffffffffL & l) != 0L)
							checkNAddTwoStates(4, 5);
						break;
					case 5:
						if (curChar == 42)
							checkNAddStates(7, 9);
						break;
					case 6:
						if ((0xffff7bffffffffffL & l) != 0L)
							checkNAddTwoStates(7, 5);
						break;
					case 7:
						if ((0xfffffbffffffffffL & l) != 0L)
							checkNAddTwoStates(7, 5);
						break;
					case 8:
						if (curChar == 47 && kind > 81)
							kind = 81;
						break;
					case 9:
						if (curChar == 47)
							stateSet[newStateCnt++] = 3;
						break;
					case 12:
						if (curChar == 58)
							stateSet[newStateCnt++] = 13;
						break;
					case 14:
						if ((0x3ff001000000000L & l) == 0L)
							break;
						if (kind > 85)
							kind = 85;
						addStates(10, 11);
						break;
					case 15:
						if (curChar == 46)
							stateSet[newStateCnt++] = 16;
						break;
					case 17:
						if ((0x3ff001000000000L & l) == 0L)
							break;
						if (kind > 85)
							kind = 85;
						stateSet[newStateCnt++] = 17;
						break;
					case 18:
						if (curChar == 39)
							checkNAddTwoStates(19, 20);
						break;
					case 19:
						if ((0xffffff7fffffffffL & l) != 0L)
							checkNAddTwoStates(19, 20);
						break;
					case 20:
						if (curChar != 39)
							break;
						if (kind > 86)
							kind = 86;
						stateSet[newStateCnt++] = 21;
						break;
					case 21:
						if (curChar == 39)
							checkNAddTwoStates(22, 20);
						break;
					case 22:
						if ((0xffffff7fffffffffL & l) != 0L)
							checkNAddTwoStates(22, 20);
						break;
					case 23:
						if (curChar == 34)
							checkNAddTwoStates(24, 25);
						break;
					case 24:
						if ((0xfffffffbffffdbffL & l) != 0L)
							checkNAddTwoStates(24, 25);
						break;
					case 25:
						if (curChar == 34 && kind > 87)
							kind = 87;
						break;
					case 26:
						if (curChar == 46)
							checkNAddTwoStates(27, 37);
						break;
					case 27:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddTwoStates(27, 28);
						break;
					case 29:
						if ((0x280000000000L & l) != 0L)
							addStates(12, 13);
						break;
					case 30:
						if (curChar == 46)
							checkNAdd(31);
						break;
					case 31:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAdd(31);
						break;
					case 32:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddStates(14, 16);
						break;
					case 33:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAdd(33);
						break;
					case 34:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddTwoStates(34, 35);
						break;
					case 35:
						if (curChar == 46)
							checkNAdd(36);
						break;
					case 36:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAdd(36);
						break;
					case 37:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAdd(37);
						break;
					case 38:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddStates(0, 6);
						break;
					case 39:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddTwoStates(39, 28);
						break;
					case 40:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddStates(17, 19);
						break;
					case 41:
						if (curChar == 46)
							checkNAdd(42);
						break;
					case 42:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddTwoStates(42, 28);
						break;
					case 43:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAddTwoStates(43, 44);
						break;
					case 44:
						if (curChar == 46)
							checkNAdd(45);
						break;
					case 45:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAdd(45);
						break;
					case 46:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 76)
							kind = 76;
						checkNAdd(46);
						break;
					default : break;
					}
				} while(i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch(stateSet[--i])
					{
					case 2:
					case 10:
						if ((0x7fffffe07fffffeL & l) == 0L)
							break;
						if (kind > 82)
							kind = 82;
						checkNAddTwoStates(10, 11);
						break;
					case 48:
						if ((0x7fffffe87fffffeL & l) != 0L)
						{
							if (kind > 82)
								kind = 82;
							checkNAdd(11);
						}
						if ((0x7fffffe07fffffeL & l) != 0L)
						{
							if (kind > 82)
								kind = 82;
							checkNAddTwoStates(10, 11);
						}
						break;
					case 1:
						if (kind > 80)
							kind = 80;
						stateSet[newStateCnt++] = 1;
						break;
					case 4:
						checkNAddTwoStates(4, 5);
						break;
					case 6:
					case 7:
						checkNAddTwoStates(7, 5);
						break;
					case 11:
						if ((0x7fffffe87fffffeL & l) == 0L)
							break;
						if (kind > 82)
							kind = 82;
						checkNAdd(11);
						break;
					case 13:
						if ((0x7fffffe07fffffeL & l) == 0L)
							break;
						if (kind > 85)
							kind = 85;
						checkNAddStates(20, 22);
						break;
					case 14:
						if ((0x7fffffe87fffffeL & l) == 0L)
							break;
						if (kind > 85)
							kind = 85;
						checkNAddTwoStates(14, 15);
						break;
					case 16:
						if ((0x7fffffe07fffffeL & l) == 0L)
							break;
						if (kind > 85)
							kind = 85;
						checkNAddTwoStates(16, 17);
						break;
					case 17:
						if ((0x7fffffe87fffffeL & l) == 0L)
							break;
						if (kind > 85)
							kind = 85;
						checkNAdd(17);
						break;
					case 19:
						checkNAddTwoStates(19, 20);
						break;
					case 22:
						checkNAddTwoStates(22, 20);
						break;
					case 24:
						addStates(23, 24);
						break;
					case 28:
						if ((0x2000000020L & l) != 0L)
							addStates(25, 27);
						break;
					default : break;
					}
				} while(i != startsAt);
			}
			else
			{
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch(stateSet[--i])
					{
					case 1:
						if ((bitVec0[i2] & l2) == 0L)
							break;
						if (kind > 80)
							kind = 80;
						stateSet[newStateCnt++] = 1;
						break;
					case 4:
						if ((bitVec0[i2] & l2) != 0L)
							checkNAddTwoStates(4, 5);
						break;
					case 6:
					case 7:
						if ((bitVec0[i2] & l2) != 0L)
							checkNAddTwoStates(7, 5);
						break;
					case 19:
						if ((bitVec0[i2] & l2) != 0L)
							checkNAddTwoStates(19, 20);
						break;
					case 22:
						if ((bitVec0[i2] & l2) != 0L)
							checkNAddTwoStates(22, 20);
						break;
					case 24:
						if ((bitVec0[i2] & l2) != 0L)
							addStates(23, 24);
						break;
					default : break;
					}
				} while(i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				pMatchedKind = kind;
				pMatchedPos = curPosAux;
				kind = 0x7fffffff;
			}
			++curPosAux;
			if ((i = newStateCnt) == (startsAt = 47 - (newStateCnt = startsAt)))
				return curPosAux;
			try { curChar = inputStream.readChar(); }
			catch(java.io.IOException e) { return curPosAux; }
		}
	}

	static final int[] pNextStates = {
		39, 40, 41, 28, 43, 44, 46, 5, 6, 8, 14, 15, 30, 32, 33, 34, 
		35, 40, 41, 28, 13, 14, 15, 24, 25, 29, 30, 32, 
	};

	public static final String[] strLiteralImages = {
		"", null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "\50", "\54", "\51", "\73", "\75", "\56", "\41\75", 
		"\43", "\74\76", "\76", "\76\75", "\74", "\74\75", "\53", "\55", "\52", "\56\52", 
		"\77", "\174\174", "\57", "\52\52", };

	public static final String[] lexStateNames = {
		"DEFAULT", 
	};

	static final long[] ptoToken = {
		0xffffffffffffffe1L, 0x1fffffe41fffL, 
	};

	static final long[] ptoSkip = {
		0x1eL, 0x30000L, 
	};

	static final long[] ptoSpecial = {
		0x0L, 0x30000L, 
	};

	public void reInit(SimpleCharStream stream)
	{
		pMatchedPos = newStateCnt = 0;
		curLexState = defaultLexState;
		inputStream = stream;
		reInitRounds();
	}

	private final void reInitRounds()
	{
		int i;
		round = 0x80000001;
		for (i = 47; i-- > 0;)
			prounds[i] = 0x80000000;
	}

	public void reInit(SimpleCharStream stream, int lexState)
	{
		reInit(stream);
		switchTo(lexState);
	}

	public void switchTo(int lexState)
	{
		if (lexState >= 1 || lexState < 0)
			throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
		else
			curLexState = lexState;
	}

	protected Token pFillToken()
	{
		Token t = Token.newToken(pMatchedKind);
		t.kind = pMatchedKind;
		String im = strLiteralImages[pMatchedKind];
		t.image = (im == null) ? inputStream.getImage() : im;
		t.beginLine = inputStream.getBeginLine();
		t.beginColumn = inputStream.getBeginColumn();
		t.endLine = inputStream.getEndLine();
		t.endColumn = inputStream.getEndColumn();
		return t;
	}

	int curLexState = 0;
	int defaultLexState = 0;
	int newStateCnt;
	int round;
	int pMatchedPos;
	int pMatchedKind;


	public Token getNextToken() {

		Token specialToken = null;
		Token matchedToken;
		int curPos = 0;

		EOFLoop :
			for (;;)
			{   
				try {     
					curChar = inputStream.beginToken();
				}     
				catch(java.io.IOException e){        
					pMatchedKind = 0;
					matchedToken = pFillToken();
					matchedToken.specialToken = specialToken;
					return matchedToken;
				}

				try { 
					inputStream.backup(0);
					while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
						curChar = inputStream.beginToken();
				}
				catch (java.io.IOException e1) { 
					continue EOFLoop; 
				}
				pMatchedKind = 0x7fffffff;
				pMatchedPos = 0;
				curPos = moveStringLiteralDfa0x0();
				if (pMatchedKind != 0x7fffffff)
				{
					if (pMatchedPos + 1 < curPos)
						inputStream.backup(curPos - pMatchedPos - 1);
					
					if ((ptoToken[pMatchedKind >> 6] & (1L << (pMatchedKind & 077))) != 0L)
					{
						matchedToken = pFillToken();
						matchedToken.specialToken = specialToken;
						specialToken = matchedToken;
						
						if(specialToken.toString().toUpperCase().equals("LIMIT"))
							matchedToken.kind = K_LIMIT;
						if(specialToken.toString().toUpperCase().equals("SKIP"))
							matchedToken.kind = K_SKIP;
						
						return matchedToken;
					}
					else
					{
						if ((ptoSpecial[pMatchedKind >> 6] & (1L << (pMatchedKind & 077))) != 0L)
						{
							matchedToken = pFillToken();
							if (specialToken == null)
								specialToken = matchedToken;
							else
							{
								matchedToken.specialToken = specialToken;
								specialToken = (specialToken.next = matchedToken);
							}
						}
						continue EOFLoop;
					}
				}
				int error_line = inputStream.getEndLine();
				int error_column = inputStream.getEndColumn();
				String error_after = null;
				boolean EOFSeen = false;
				
				try { 
					
					inputStream.readChar(); 
					inputStream.backup(1); 
					
				}catch (java.io.IOException e1) {
					EOFSeen = true;
					error_after = curPos <= 1 ? "" : inputStream.getImage();
					if (curChar == '\n' || curChar == '\r') {
						error_line++;
						error_column = 0;
					}
					else
						error_column++;
				}
				if (!EOFSeen) {
					inputStream.backup(1);
					error_after = curPos <= 1 ? "" : inputStream.getImage();
				}
				throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
			}
	}

}
