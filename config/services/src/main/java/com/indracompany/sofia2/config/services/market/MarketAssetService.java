/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
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
package com.indracompany.sofia2.config.services.market;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.indracompany.sofia2.config.model.MarketAsset;

public interface MarketAssetService {

	List<MarketAsset> loadMarketAssetByFilter(String marketAssetId, String userId);

	String createMarketAsset(MarketAsset marketAsset);

	byte[] getImgBytes(String id);

	byte[] getContent(String id);

	void downloadDocument(String id, HttpServletResponse response) throws Exception;

	void updateMarketAsset(String id, MarketAsset marketAssetMultipartMap);

	String updateState(String id, String state, String reasonData);

	void delete(String id, String userId);

	void rate(String id, String rate, String userId);

	void createComment(String id, String userId, String title, String comment);

	void deleteComment(String id);
}
