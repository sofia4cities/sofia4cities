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
package com.indracompany.sofia2.router.service.app.service.suscription;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Service
public class SuscriptionRepository<K,T> {
   // private ConcurrentHashMap<String , T> storage = new ConcurrentHashMap<String , T>();
	Multimap<K, T> storage = ArrayListMultimap.create();
    public T add(K id, T type ) {
    	storage.put(id,type);
        return type;
    }

    public void delete(K id, T type) {
    	storage.remove(id, type);
    }

    public Collection<T> findById(K id) {
        return storage.get(id);
    }
  
    public List<T> findAll() {
        return storage
                .values()
                .stream()
                .collect(Collectors.toList());
    }
    
}